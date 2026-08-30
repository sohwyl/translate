#!/usr/bin/env python3
"""
generate_voice_over.py
=======================

Generates natural, neural-quality male + female Iraqi-Arabic voice-overs for
every phrase in the app's phrase database, and drops them exactly where the
Android app expects them:

    app/src/main/assets/audio/male/phrase_<id>_male.mp3
    app/src/main/assets/audio/female/phrase_<id>_female.mp3

This is the ONE script you need to run. It replaces the ~17 overlapping,
half-finished, and in some cases fake (sine-wave "TTS") scripts that used to
be scattered across the repo (root, app/, app/scripts/, app/applet/...).

Why this engine
----------------
Voice: Microsoft Edge neural TTS (via the free `edge-tts` library), using the
two dedicated Iraqi-Arabic neural voices Microsoft ships:

    ar-IQ-BasselNeural   (male)
    ar-IQ-RanaNeural     (female)

These are, at the time of writing, the *only* Iraqi-dialect neural voices
available from any major TTS provider for free/local use — generic "ar-SA"
(Saudi) or "ar-EG" (Egyptian) voices exist too but sound noticeably foreign
against the Iraqi phrases in this app, which is exactly the kind of
"unnatural" result the previous scripts sometimes produced. No API key is
required.

If you have an ElevenLabs / Azure Speech (paid) key and want an even higher
fidelity pass later, the same phrase source (Arabic_Voweled column) can be
fed to those engines too — this script only handles the free/local path.

Why "Arabic_Voweled" and not "Arabic_Plain"
--------------------------------------------
The phrase database stores each phrase twice: fully voweled (with tashkeel,
e.g. "اَلسَّلامُ عَلَیکُم") and plain (no diacritics, e.g. "السلام عليكم").
Arabic TTS engines pronounce voweled text far more accurately (diacritics
disambiguate word forms and vowel sounds the engine would otherwise have to
guess), so this script always synthesizes from Arabic_Voweled, falling back
to Arabic_Plain only if a row is missing vowels.

Usage
-----
    pip install edge-tts --break-system-packages   # (or inside a venv)
    python3 generate_voice_over.py                 # generate everything
    python3 generate_voice_over.py --gender male    # only male voice
    python3 generate_voice_over.py --limit 10       # quick smoke test
    python3 generate_voice_over.py --rate -5% --pitch -2Hz   # tweak prosody
    python3 generate_voice_over.py --concurrency 4  # slower connections

The script is fully resumable: re-running it skips any phrase/gender file
that already exists and is non-empty, so if it gets interrupted (or you add
new phrases later) you can just run it again.
"""

from __future__ import annotations

import argparse
import asyncio
import json
import sys
from dataclasses import dataclass
from pathlib import Path

try:
    import edge_tts
except ImportError:
    sys.exit(
        "edge-tts is not installed.\n"
        "Install it first:\n"
        "    pip install edge-tts --break-system-packages\n"
        "(or inside a virtualenv without the --break-system-packages flag)"
    )

# ---------------------------------------------------------------------------
# Config
# ---------------------------------------------------------------------------

REPO_ROOT = Path(__file__).resolve().parent
PHRASES_JSON = REPO_ROOT / "phrases_database_600_triple_format.json"
ASSETS_DIR = REPO_ROOT / "app" / "src" / "main" / "assets" / "audio"

VOICES = {
    "male": "ar-IQ-BasselNeural",
    "female": "ar-IQ-RanaNeural",
}

DEFAULT_RATE = "-4%"    # slightly slower than default -> clearer for learners
DEFAULT_PITCH = "+0Hz"  # keep natural pitch; tune per-voice here if needed
DEFAULT_VOLUME = "+0%"

MIN_VALID_MP3_BYTES = 800  # a real synthesized phrase is always well above this


@dataclass
class Phrase:
    id: int
    text: str  # the voweled Arabic text actually sent to the TTS engine


def load_phrases() -> list[Phrase]:
    if not PHRASES_JSON.exists():
        sys.exit(f"Phrase database not found at {PHRASES_JSON}")

    raw = json.loads(PHRASES_JSON.read_text(encoding="utf-8"))
    phrases: list[Phrase] = []
    for row in raw:
        text = (row.get("Arabic_Voweled") or row.get("Arabic_Plain") or "").strip()
        if not text:
            print(f"⚠️  Skipping phrase id={row.get('id')} — no Arabic text found")
            continue
        phrases.append(Phrase(id=row["id"], text=text))

    # Sanity: ids should be contiguous 1..N with no duplicates/gaps, since the
    # Android app (DatabaseInitializer.kt) hard-codes ids 1..600.
    ids = sorted(p.id for p in phrases)
    expected = list(range(1, len(ids) + 1))
    if ids != expected:
        print("⚠️  Warning: phrase ids are not a clean contiguous 1..N sequence.")
        missing = sorted(set(expected) - set(ids))
        if missing:
            print(f"   Missing ids: {missing[:20]}{' ...' if len(missing) > 20 else ''}")

    return phrases


def out_path(gender: str, phrase_id: int) -> Path:
    return ASSETS_DIR / gender / f"phrase_{phrase_id}_{gender}.mp3"


def is_already_done(path: Path) -> bool:
    return path.exists() and path.stat().st_size >= MIN_VALID_MP3_BYTES


async def synthesize_one(
    phrase: Phrase,
    gender: str,
    voice: str,
    rate: str,
    pitch: str,
    volume: str,
    semaphore: asyncio.Semaphore,
    max_retries: int = 4,
) -> tuple[Phrase, str, bool, str]:
    """Returns (phrase, gender, success, message)."""
    dest = out_path(gender, phrase.id)

    if is_already_done(dest):
        return phrase, gender, True, "skipped (already exists)"

    dest.parent.mkdir(parents=True, exist_ok=True)
    tmp_dest = dest.with_suffix(".mp3.part")

    last_error = "unknown error"
    async with semaphore:
        for attempt in range(1, max_retries + 1):
            try:
                communicator = edge_tts.Communicate(
                    text=phrase.text,
                    voice=voice,
                    rate=rate,
                    pitch=pitch,
                    volume=volume,
                )
                await communicator.save(str(tmp_dest))

                if tmp_dest.exists() and tmp_dest.stat().st_size >= MIN_VALID_MP3_BYTES:
                    tmp_dest.replace(dest)
                    return phrase, gender, True, f"ok ({dest.stat().st_size} bytes)"
                else:
                    last_error = "output file was empty/too small"
            except Exception as exc:  # noqa: BLE001 - want to retry on anything transient
                last_error = f"{type(exc).__name__}: {exc}"

            # Exponential backoff before retrying (Microsoft's endpoint rate-limits
            # aggressively if you hit it too fast with high concurrency).
            await asyncio.sleep(min(2 ** attempt, 20))

        tmp_dest.unlink(missing_ok=True)
        return phrase, gender, False, f"FAILED after {max_retries} attempts: {last_error}"


async def run(args: argparse.Namespace) -> int:
    phrases = load_phrases()
    if args.limit:
        phrases = phrases[: args.limit]

    genders = [args.gender] if args.gender != "both" else ["male", "female"]

    print(f"📚 Loaded {len(phrases)} phrases from {PHRASES_JSON.name}")
    print(f"🎙️  Voices: " + ", ".join(f"{g}={VOICES[g]}" for g in genders))
    print(f"🎚️  rate={args.rate}  pitch={args.pitch}  volume={args.volume}")
    print(f"⚙️  concurrency={args.concurrency}")
    print(f"📁 Output: {ASSETS_DIR}")
    print("-" * 70)

    semaphore = asyncio.Semaphore(args.concurrency)
    tasks = [
        synthesize_one(
            phrase, gender, VOICES[gender], args.rate, args.pitch, args.volume, semaphore
        )
        for gender in genders
        for phrase in phrases
    ]

    total = len(tasks)
    done = 0
    failures: list[str] = []
    skipped = 0
    generated = 0

    for coro in asyncio.as_completed(tasks):
        phrase, gender, success, message = await coro
        done += 1
        tag = f"[{done:>4}/{total}] phrase#{phrase.id:<4} {gender:<6}"
        if success:
            if "skipped" in message:
                skipped += 1
            else:
                generated += 1
            if done % 25 == 0 or done == total:
                print(f"{tag} ✅ {message}")
        else:
            failures.append(f"phrase#{phrase.id} ({gender}): {message}")
            print(f"{tag} ❌ {message}")

    print("-" * 70)
    print(f"✅ Newly generated: {generated}")
    print(f"⏭️  Already existed (skipped): {skipped}")
    print(f"❌ Failed: {len(failures)}")

    if failures:
        print("\nFailed items:")
        for line in failures:
            print(f"  - {line}")
        print(
            "\nJust re-run the script — it will only retry the missing/failed files."
        )
        return 1

    print(
        f"\n🎉 All {len(phrases)} phrases now have {'both male and female' if len(genders) == 2 else genders[0]} audio."
    )
    return 0


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--gender", choices=["male", "female", "both"], default="both",
                         help="Which voice(s) to generate (default: both)")
    parser.add_argument("--limit", type=int, default=None,
                         help="Only process the first N phrases (useful for a quick test run)")
    parser.add_argument("--concurrency", type=int, default=6,
                         help="Max simultaneous requests to the TTS engine (default: 6)")
    parser.add_argument("--rate", default=DEFAULT_RATE,
                         help=f"Speaking rate offset, e.g. -10%%, +5%% (default: {DEFAULT_RATE})")
    parser.add_argument("--pitch", default=DEFAULT_PITCH,
                         help=f"Pitch offset, e.g. -2Hz, +3Hz (default: {DEFAULT_PITCH})")
    parser.add_argument("--volume", default=DEFAULT_VOLUME,
                         help=f"Volume offset, e.g. -10%%, +5%% (default: {DEFAULT_VOLUME})")
    return parser.parse_args()


if __name__ == "__main__":
    exit_code = asyncio.run(run(parse_args()))
    sys.exit(exit_code)
