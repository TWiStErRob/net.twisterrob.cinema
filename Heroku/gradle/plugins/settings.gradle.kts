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
