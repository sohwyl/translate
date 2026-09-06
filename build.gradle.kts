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
            val lines = ciErrorBuffer.toString().lines()
            val relevant = lines.filter { l ->
                l.contains("error:") || l.trimStart().startsWith("e:") || l.contains("Unresolved reference") || l.contains("unresolved reference")
            }
            relevant.take(80).forEach { l ->
                val clean = l.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
                println("::error::$clean")
            }
            if (relevant.isEmpty()) {
                println("::error::CI-DIAG: no matched error lines; bufferSize=${ciErrorBuffer.length}; failure=${this.failure?.message?.take(500)}")
                // Dump a chunk of the raw buffer tail so we at least see *something*.
                val tail = ciErrorBuffer.toString().takeLast(3000)
                    .replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
                println("::error::CI-DIAG-TAIL: $tail")
            }
        }
    }
}
