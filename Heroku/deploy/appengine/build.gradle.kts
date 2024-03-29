import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
	id("net.twisterrob.cinema.library")
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#app-engine-appyaml-based-projects
	alias(libs.plugins.appengine.yaml)
}

dependencies {
	implementation(projects.backend.endpoint)
}

val copyAppengineResources = tasks.register<ProcessResources>("copyAppengineResources") {
	from(file("src/main/appengine"))
	filesMatching(listOf("**/app.yaml")) {
		val replacements = mapOf(
			"NEO4J_URL" to System.getenv("NEO4J_URL"),
		)
		filter(mapOf("tokens" to replacements), org.apache.tools.ant.filters.ReplaceTokens::class.java)
	}
	into(layout.buildDirectory.dir("stage-prep/appengine"))
}

val copyJarDependencies = tasks.register<Copy>("copyJarDependencies") {
	from(configurations.runtimeClasspath)
	into(layout.buildDirectory.dir("stage-prep/dependencies"))
}

val copyFrontendResources = tasks.register<Copy>("copyFrontendResources") {
	from(file("../frontend"))
	into(layout.buildDirectory.dir("stage-prep/frontend"))
}

appengine {
	tools {
		// Windows installer puts it in %LOCALAPPDATA%\Google\Cloud SDK\
		// By default Gradle downloads to %LOCALAPPDATA%\google\ct4j-cloud-sdk\
		System.getenv("GCLOUD_HOME")?.let { setCloudSdkHome(it) }
		verbosity = "info" // debug, info, warning, error, critical, none
	}
	// Eagerly configure Jar task, because AppEngineAppYamlPlugin uses Groovy properties APIs to access it.
	// TODEL https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/393
	tasks.jar.get()
	stage {
		setAppEngineDirectory(file(copyAppengineResources.get().destinationDir))
		val extraTasks = listOf(
			copyAppengineResources,
			copyJarDependencies,
			copyFrontendResources,
		)
		setExtraFilesDirectories(extraTasks) // Careful, this will eagerly resolve the output dirs.
		// TODEL https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/435
		tasks.named("appengineStage").configure { dependsOn(extraTasks) }
	}
	deploy {
		projectId = "twisterrob-cinema"
		version = deployName.takeIf { !deployReplaceLive } ?: OffsetDateTime
			.now(ZoneOffset.UTC)
			.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
		promote = deployReplaceLive
		stopPreviousVersion = deployReplaceLive
	}
}

val Project.deployReplaceLive: Boolean
	get() = property("net.twisterrob.deploy.replaceLive").toString().toBoolean()

val Project.deployName: String?
	get() = property("net.twisterrob.deploy.versionName").toString().takeIf { it != "release" }

// TODEL https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/454
listOf(
	// com.google.cloud.tools.gradle.appengine.core.CheckCloudSdkTask::class, // OK
	com.google.cloud.tools.gradle.appengine.core.DownloadCloudSdkTask::class, // getProject()
	com.google.cloud.tools.gradle.appengine.core.GcloudTask::class, // DeployExtension.gradleProject (except CloudSdkLoginTask)
	com.google.cloud.tools.gradle.appengine.core.ShowConfigurationTask::class, // getProject()
	com.google.cloud.tools.gradle.appengine.appyaml.StageAppYamlTask::class, // StageAppYamlExtension.project + getProject()
	com.google.cloud.tools.gradle.appengine.standard.StageStandardTask::class, // StageStandardExtension.project + getProject()
	com.google.cloud.tools.gradle.appengine.standard.DevAppServerRunTask::class, // RunExtension.project
	com.google.cloud.tools.gradle.appengine.standard.DevAppServerStartTask::class, // RunExtension.project
	com.google.cloud.tools.gradle.appengine.standard.DevAppServerStopTask::class, // RunExtension.project
	com.google.cloud.tools.gradle.appengine.sourcecontext.GenRepoInfoFileTask::class, // GenRepoInfoFileExtension.project
).forEach {
	tasks.withType(it).configureEach {
		notCompatibleWithConfigurationCache("https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/454")
	}
}
