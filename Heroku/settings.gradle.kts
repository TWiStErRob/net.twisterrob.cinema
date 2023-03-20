import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly

rootProject.name = "Cinema-Heroku"

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

include(":test-helpers")

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
	id("net.twisterrob.cinema.heroku.plugins.settings")
	id("net.twisterrob.gradle.plugin.settings") version "0.15.1"
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
		lockFile.set(file("gradle/dependency-locks/root-settings.lockfile"))
	}
}
