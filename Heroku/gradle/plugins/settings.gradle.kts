import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly

// TODO https://github.com/TWiStErRob/net.twisterrob.gradle/issues/570
enableFeaturePreviewQuietly("STABLE_CONFIGURATION_CACHE", "Kotlin DSL property assignment")

dependencyResolutionManagement {
	repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
	repositories {
		mavenCentral()
	}
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}

buildscript {
	dependencyLocking {
		lockAllConfigurations()
		// To prevent "Kotlin DSL property assignment", don't use it here.
		lockFile.set(file("../../gradle/dependency-locks/plugins-settings.lockfile"))
	}
}

plugins {
	id("net.twisterrob.gradle.plugin.nagging") version "0.16"
}

val isCI: Boolean
	get() = System.getenv("GITHUB_ACTIONS") == "true"

val gradleVersion: String = GradleVersion.current().version

// TODEL When this is fixed: https://github.com/gradle/gradle/issues/22481
// Source: https://youtrack.jetbrains.com/issue/KT-55563
@Suppress("MaxLineLength", "StringLiteralDuplication")
if (GradleVersion.current().baseVersion < GradleVersion.version("9.0")) {
	@Suppress("MaxLineLength", "StringLiteralDuplication")
	doNotNagAbout(
		// > Task :plugins:compileKotlin
		"Invocation of Task.project at execution time has been deprecated. " +
				"This will fail with an error in Gradle 9.0. " +
				"Consult the upgrading guide for further information: " +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_7.html#task_project",
		// The real stack trace is this, but it's too low down and not visible.
		//"at org.jetbrains.kotlin.compilerRunner.GradleCompilerRunner\$Companion.buildModulesInfo\$kotlin_gradle_plugin_common(GradleKotlinCompilerRunner.kt:"
		"at org.gradle.internal.event.DefaultListenerManager\$EventBroadcast\$ListenerDispatch.dispatch(DefaultListenerManager.java:"
	)
} else {
	val error: (String) -> Unit = if (isCI) ::error else logger::warn
	error("Gradle version changed, please review hack.")
}
