<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/e2632247-86a0-406b-ac90-55925b3b906b

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


**Option A — Android Studio:**
1. Open Android Studio, select **Open**, and choose this project's directory.
2. Let it sync, then run on an emulator or a physical device (Run ▶).

**Option B — command line + adb (no Android Studio needed):**
1. Connect your phone over USB with USB debugging enabled, and confirm it's visible: `adb devices`
2. From the project root: `./gradlew installDebug` (Linux/macOS) or `gradlew.bat installDebug` (Windows).
   This builds the debug APK and installs it on the connected device in one step.
3. The Gradle wrapper (`gradlew`/`gradlew.bat`) is already included — no separate Gradle install needed, just a JDK 17+.

If you've already published this app and need a signed release build, you'll need your own upload keystore — see the `release` signing config in `app/build.gradle.kts`. If you've already published via AI Studio, see [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.

## Generating phrase audio

This app ships with 600 Iraqi-Arabic phrases, each with a male and a female
voice-over (1200 audio files total). To (re)generate them, see
`generate_voice_over.py` in the project root.
"# translate" 
