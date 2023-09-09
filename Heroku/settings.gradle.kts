import net.twisterrob.cinema.build.dsl.isCI
import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly

rootProject.name = "Cinema-Heroku"

// TODO https://github.com/TWiStErRob/net.twisterrob.gradle/issues/570
enableFeaturePreviewQuietly("STABLE_CONFIGURATION_CACHE", "Kotlin DSL property assignment")
enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":backend")
include(":backend:sync")
include(":backend:feed")
include(":backend:quickbook")
include(":backend:database")
include(":backend:network")
include(":backend:endpoint")
include(":deploy")
include(":deploy:appengine")
include(":shared")

include(":test-helpers")
include(":test-integration")

pluginManagement {
	includeBuild("gradle/plugins")
	resolutionStrategy {
		eachPlugin {
			when (requested.id.id) {
				"com.google.cloud.tools.appengine",
				"com.google.cloud.tools.appengine-appyaml",
				"com.google.cloud.tools.appengine-appenginewebxml",
				-> {
					useModule("com.google.cloud.tools:appengine-gradle-plugin:${requested.version}")
				}
			}
		}
	}
}

plugins {
	id("net.twisterrob.cinema.settings")
	id("net.twisterrob.gradle.plugin.nagging") version "0.16"
}

dependencyResolutionManagement {
	repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
	repositories {
		mavenCentral()
	}
}

buildscript {
	dependencyLocking {
		lockAllConfigurations()
		// To prevent "Kotlin DSL property assignment", don't use it here.
		lockFile.set(file("gradle/dependency-locks/root-settings.lockfile"))
	}
}

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle 8.2 sync in IDEA 2023.1 https://youtrack.jetbrains.com/issue/IDEA-320266.
@Suppress("MaxLineLength", "StringLiteralDuplication")
if ((System.getProperty("idea.version") ?: "") < "2023.3") {
	// There are ton of warnings, ignoring them all by their class names in one suppression.
	doNotNagAbout(
		Regex(
			"^" +
					"(" +
					Regex.escape("The Project.getConvention() method has been deprecated. ") +
					"|" +
					Regex.escape("The org.gradle.api.plugins.Convention type has been deprecated. ") +
					"|" +
					Regex.escape("The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. ") +
					")" +
					Regex.escape("This is scheduled to be removed in Gradle 9.0. ") +
					Regex.escape("Consult the upgrading guide for further information: ") +
					Regex.escape("https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#") +
					".*" +
					"(" +
					Regex.escape("at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.") +
					"|" +
					Regex.escape("at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil.") +
					"|" +
					Regex.escape("at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.") +
					"|" +
					Regex.escape("at org.jetbrains.plugins.gradle.tooling.builder.ProjectExtensionsDataBuilderImpl.") +
					")" +
					".*$"
		)
	)
} else {
	val error: (String) -> Unit = if (isCI) ::error else logger::warn
	error("IDEA version changed, please review hack.")
}

// TODEL Gradle 8.2 sync in IDEA 2023.1 https://youtrack.jetbrains.com/issue/IDEA-320307.
@Suppress("MaxLineLength", "StringLiteralDuplication")
if ((System.getProperty("idea.version") ?: "") < "2023.3") {
	@Suppress("MaxLineLength", "StringLiteralDuplication")
	doNotNagAbout(
		"The BuildIdentifier.getName() method has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Use getBuildPath() to get a unique identifier for the build. " +
				"Consult the upgrading guide for further information: " +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		// There are 4 stack traces coming to this line, ignore them all at once.
		"at org.jetbrains.plugins.gradle.tooling.util.resolve.DependencyResolverImpl.resolveDependencies(DependencyResolverImpl.java:266)"
	)
} else {
	val error: (String) -> Unit = if (isCI) ::error else logger::warn
	error("IDEA version changed, please review hack.")
}
