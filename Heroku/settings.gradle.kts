import net.twisterrob.gradle.doNotNagAbout
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
	id("net.twisterrob.cinema.settings")
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
		lockFile = file("gradle/dependency-locks/root-settings.lockfile")
	}
}

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle sync in IDEA 2022.3.1: https://youtrack.jetbrains.com/issue/IDEA-306975
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Please use the archiveFile property instead. " +
			"For more information, please refer to " +
			"https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath" +
			" in the Gradle documentation.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in IDEA 2022.3.1: https://youtrack.jetbrains.com/issue/IDEA-306975
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Please use the archiveFile property instead. " +
			"For more information, please refer to " +
			"https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath" +
			" in the Gradle documentation.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)

// TODEL Gradle sync in IDEA 2023.1 (vs. Gradle 8.1): https://issuetracker.google.com/issues/274469173 
@Suppress("MaxLineLength")
doNotNagAbout(
	"The org.gradle.util.VersionNumber type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#org_gradle_util_reports_deprecations",
	// There are 3 instances on consecutive lines, so just ignore the whole file.
	"at com.android.ide.gradle.model.builder.AndroidStudioToolingPluginKt.isGradleAtLeast(AndroidStudioToolingPlugin.kt:"
)
