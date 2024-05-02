enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

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
		lockFile = file("../../gradle/dependency-locks/plugins-settings.lockfile")
	}
}

plugins {
	id("net.twisterrob.gradle.plugin.nagging") version "0.17"
}
