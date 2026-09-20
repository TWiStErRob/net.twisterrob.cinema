import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly

rootProject.name = "net-twisterrob-cinema-planner"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
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
include(":test-reports")

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
	id("net.twisterrob.gradle.plugin.nagging") version "0.20"
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
		lockFile = file("gradle/dependency-locks/root-settings.lockfile")
	}
}

val gradleVersion: String = GradleVersion.current().version

// TODEL AppEngine Gradle plugin 2.8.7 vs Gradle 9.6.0 https://github.com/GoogleCloudPlatform/appengine-plugins/issues/1078
doNotNagAbout(
	"The Project.getProperties method has been deprecated. " +
			"This will fail with an error in Gradle 10. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_9.html#deprecated_get_properties",
	"at com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin.lambda\$configureExtensions\$0(AppEngineAppYamlPlugin.java:96)"
)
