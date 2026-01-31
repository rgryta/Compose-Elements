import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.ben.manes).apply(true)
}

val isNonStable: (ModuleComponentIdentifier) -> Boolean = { candidate ->
    val version = candidate.version
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = Regex("^[0-9,.v-]+(-r)?$")
    val oauth2regex = Regex("^v\\d+-rev\\d{8}-(\\d+\\.){2}\\d+$")
    !stableKeyword && !regex.matches(version) && !oauth2regex.matches(version)
}

allprojects {
    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate)
        }
        // Workaround for CME with AGP 9: https://github.com/ben-manes/gradle-versions-plugin/issues/930
        filterConfigurations = Spec { !it.name.contains("test", ignoreCase = true) }
    }
}