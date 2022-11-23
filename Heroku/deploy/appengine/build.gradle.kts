plugins {
	id("java-library")
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#app-engine-appyaml-based-projects
	alias(libs.plugins.appengine.yaml)
}

dependencies {
	//implementation(projects.backend.endpoint)
}

val copyJarDependencies = tasks.register<Copy>("copyJarDependencies") {
	from(configurations.runtimeClasspath)
	into(layout.buildDirectory.dir("libs-deps"))
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
		setExtraFilesDirectories(
			listOf(
//				copyJarDependencies,
				"src/main/appengine",
			)
		)
		// TODEL https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/435
//		tasks.named("appengineStage").configure { dependsOn(copyJarDependencies) }
	}
	deploy {
		projectId = "twisterrob-cinema"
		version = "2"
	}
}
