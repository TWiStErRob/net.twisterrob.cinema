rootProject.name = "Cinema-Heroku"

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":backend:sync")
include(":backend:feed")
include(":backend:quickbook")
include(":backend:database")
include(":backend:network")
include(":backend:endpoint")
include(":deploy:appengine")

include(":test-helpers")

pluginManagement {
	@Suppress("UnstableApiUsage")
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
				"net.twisterrob.settings" -> {
					useModule("net.twisterrob.gradle:twister-convention-settings:${requested.version}")
				}
			}
		}
	}
}

plugins {
	id("net.twisterrob.cinema.heroku.plugins.settings")
	id("net.twisterrob.settings") version "0.15"
}

buildscript {
	dependencyLocking {
		lockAllConfigurations()
		lockFile.set(file("gradle/dependency-locks/root-settings.lockfile"))
	}
}
