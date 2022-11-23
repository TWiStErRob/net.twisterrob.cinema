plugins {
	id("java-library")
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#app-engine-appyaml-based-projects
	alias(libs.plugins.appengine.yaml)
}
appengine {
	// Eagerly configure Jar task, because AppEngineAppYamlPlugin uses Groovy properties APIs to access it.
	tasks.named("jar").get()
	tools {
		// By default it downloads to %LOCALAPPDATA%\google\ct4j-cloud-sdk\
		System.getenv("GCLOUD_HOME")?.let { setCloudSdkHome(it) }
	}
}
