import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.venniktech)
}


val versions = Properties()
file("version.properties").inputStream().use { stream ->
    versions.load(stream)
}

group = "eu.gryta"
val library: String = "compose.elements"

fun bumpPatchVersion(version: String): String {
    val parts = version.split(".")
    require(parts.size == 3) { "Version must be in MAJOR.MINOR.PATCH format" }
    val (major, minor, patch) = parts
    val newPatch = patch.toInt() + 1
    return "$major.$minor.$newPatch"
}

val baseVersion = versions.getProperty("version")
val isCI = System.getenv("GITHUB_ACTIONS") == "true"
version = if (isCI) baseVersion else "${bumpPatchVersion(baseVersion)}-SNAPSHOT"

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }

    withSourcesJar(publish = false)

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "$group.$library".replace(oldChar = '.', newChar = '-')
            isStatic = true
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutinesTest)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "$group.$library"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

// Compose generated packages setup
compose.resources {
    publicResClass = true
    packageOfResClass = "$group.$library.resources"
    generateResClass = auto
}

// Add boot for ios device - patching missing certificates that cause test failure
val deviceName = project.findProperty("iosDevice") as? String ?: "iPhone 16"

tasks.register<Exec>("bootIOSSimulator") {
    isIgnoreExitValue = true
    errorOutput = System.err
    commandLine("xcrun", "simctl", "boot", deviceName)

    doLast {
        val result = executionResult.get()
        if (result.exitValue != 148 && result.exitValue != 149) { // ignoring device already booted errors
            result.assertNormalExitValue()
        }
    }
}

tasks.withType<KotlinNativeSimulatorTest>().configureEach {
    if (Os.isFamily(Os.FAMILY_MAC)) {
        dependsOn("bootIOSSimulator")
        standalone.set(false)
        device.set(deviceName)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rgryta/Compose-Elements")
            credentials {
                username =
                    project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
            }
        }
        mavenLocal()
    }
}

mavenPublishing {
    coordinates(
        groupId = group.toString(),
        artifactId = library,
        version = version.toString()
    )

    pom {
        name.set("KMP Library containing various commonly used Jetpack Compose elements")
        description.set("This library can be utilized by various KMP targets to build Jetpack Compose based views")
        inceptionYear.set("2025")
        url.set("https://github.com/rgryta/Compose-Elements")

        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("rgryta")
                name.set("Radosław Gryta")
                email.set("radek.gryta@gmail.com")
                url.set("https://github.com/rgryta/")
            }
        }

        scm {
            url.set("https://github.com/rgryta/Compose-Elements")
            connection.set("scm:git:git://github.com/rgryta/Compose-Elements.git")
            developerConnection.set("scm:git:ssh://git@github.com/rgryta/Compose-Elements.git")
        }
    }
}

val libraryGroup = group.toString()
val libraryArtifact = library

tasks.register("purgeMavenLocal") {
    group = "publishing"
    description = "Removes all KMP target artifacts for this library from Maven Local"
    notCompatibleWithConfigurationCache("Accesses file system at task execution time")

    doLast {
        val m2 = File(System.getProperty("user.home"), ".m2/repository")
        val groupPath = libraryGroup.replace('.', '/')
        val baseArtifact = libraryArtifact

        val groupDir = File(m2, groupPath)
        if (!groupDir.exists()) {
            println("Group directory does not exist: $groupDir")
            return@doLast
        }

        groupDir.listFiles()
            ?.filter { it.isDirectory && (it.name == baseArtifact || it.name.startsWith("$baseArtifact-")) }
            ?.forEach { dir ->
                dir.deleteRecursively()
                println("Deleted ${dir.name}")
            }
    }
}