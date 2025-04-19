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
	id("net.twisterrob.gradle.plugin.nagging") version "0.18"
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
