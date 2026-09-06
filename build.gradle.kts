// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

// --- Temporary CI diagnostics: surface compiler errors as GitHub Actions
// annotations, since raw job logs aren't reachable from this dev environment. ---
run {
    val ciErrorBuffer = StringBuilder()
    gradle.rootProject {
        allprojects {
            tasks.configureEach {
                logging.addStandardErrorListener { msg -> synchronized(ciErrorBuffer) { ciErrorBuffer.append(msg) } }
                logging.addStandardOutputListener { msg -> synchronized(ciErrorBuffer) { ciErrorBuffer.append(msg) } }
            }
        }
    }
    gradle.buildFinished {
        if (this.failure != null) {
            val full = ciErrorBuffer.toString()
            val lines = full.lines()
            val relevant = lines.filter { l ->
                val lower = l.lowercase()
                lower.contains("error:") || l.trimStart().startsWith("e:") ||
                    lower.contains("unresolved reference") || lower.contains("failed") ||
                    lower.contains("exception") || lower.contains("aapt") || lower.contains("caused by")
            }
            relevant.take(80).forEach { l ->
                val clean = l.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
                println("::error::$clean")
            }
            // Always also dump a raw tail chunk, split into pieces, so nothing is missed
            // even if the keyword filter above doesn't match this failure's wording.
            val tail = full.takeLast(6000)
            tail.chunked(900).forEachIndexed { i, chunk ->
                val clean = chunk.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
                println("::error::CI-DIAG-TAIL-$i: $clean")
            }
        }
    }
}
