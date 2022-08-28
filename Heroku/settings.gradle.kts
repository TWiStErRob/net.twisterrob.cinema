rootProject.name = "Cinema-Heroku"

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":backend:sync")
include(":backend:feed")
include(":backend:quickbook")
include(":backend:database")
include(":backend:network")
include(":backend:endpoint")

include(":test-helpers")

pluginManagement {
	@Suppress("UnstableApiUsage")
	includeBuild("gradle/plugins")
}

plugins {
	id("net.twisterrob.cinema.heroku.plugins.settings")
}
