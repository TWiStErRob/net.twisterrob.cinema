dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}

buildscript {
	dependencyLocking {
		lockAllConfigurations()
		lockFile.set(file("../gradle/dependency-locks/buildSrc-settings.lockfile"))
	}
}
