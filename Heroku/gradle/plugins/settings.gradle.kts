import net.twisterrob.gradle.doNotNagAbout

plugins {
	id("net.twisterrob.gradle.plugin.settings") version "0.15.1"
}

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

val gradleVersion: String = GradleVersion.current().version

// The resolvable usage is already allowed on configuration ':plugins:generatePrecompiledScriptPluginAccessors:accessors8939604837258708605:testFixturesRuntimeClasspath'. This behavior has been deprecated. This behavior is scheduled to be removed in Gradle 9.0. Remove the call to setCanBeResolved(true), it has no effect. Consult the upgrading guide for further information: https://docs.gradle.org/8.2-milestone-1/userguide/upgrading_version_8.html#redundant_configuration_usage_activation
// TODEL Gradle 8.2 vs Kotlin 1.8.20 https://github.com/gradle/gradle/pull/24271#issuecomment-1546706115
@Suppress("MaxLineLength")
doNotNagAbout(
	Regex(
		Regex.escape("The resolvable usage is already allowed on configuration ") +
				"'(:plugins)?:generatePrecompiledScriptPluginAccessors:accessors\\d+:testFixturesRuntimeClasspath'. " +
				Regex.escape("This behavior has been deprecated. ") +
				Regex.escape("This behavior is scheduled to be removed in Gradle 9.0. ") +
				Regex.escape("Remove the call to setCanBeResolved(true), it has no effect. ") +
				Regex.escape("Consult the upgrading guide for further information: ") +
				Regex.escape("https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#redundant_configuration_usage_activation") +
				".*" +
				// Task :generatePrecompiledScriptPluginAccessors 
				Regex.escape("at org.jetbrains.kotlin.gradle.plugin.mpp.compilationImpl.factory.KotlinCompilationDependencyConfigurationsFactoriesKt.KotlinCompilationDependencyConfigurationsContainer") +
				".*"
	)
)
