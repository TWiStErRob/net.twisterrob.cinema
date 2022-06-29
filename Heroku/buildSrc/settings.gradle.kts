dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	versionCatalogs {
		create("libs") {
			from(files("../gradle/libs.versions.toml"))
		}
	}
}

buildscript {
	dependencyLocking {
		lockAllConfigurations()
		lockMode.set(LockMode.STRICT)
		lockFile.set(file("../gradle/dependency-locks/buildSrc-settings.lockfile"))
	}
}
