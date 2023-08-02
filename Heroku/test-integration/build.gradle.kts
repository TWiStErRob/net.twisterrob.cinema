plugins {
	id("org.gradle.java")
	id("net.twisterrob.cinema.build.dependencies")
	id("net.twisterrob.cinema.build.detekt")
	id("net.twisterrob.cinema.build.logging")
	id("net.twisterrob.cinema.build.compilation")
	id("net.twisterrob.cinema.build.testing")
	id("net.twisterrob.cinema.build.publishing")
}

dependencies {
	Deps.junit5(project)
	Deps.slf4jToLog4jForTest(project, testType = "integrationTest")
	integrationTestImplementation(projects.testHelpers)

	implementation(libs.test.selenium)
	integrationTestRuntimeOnly(libs.test.selenium.jdkHttp)
	implementation("org.assertj:assertj-core:3.24.2") // STOPSHIP
	implementation("com.paulhammant:ngwebdriver:1.2")
}

tasks.integrationTest.configure {
	val propertyNamesToExposeToJUnitTests = listOf(
		"net.twisterrob.test.selenium.headless",
		"net.twisterrob.test.selenium.user.name",
		"net.twisterrob.test.selenium.user.pass",
	)
	val properties = propertyNamesToExposeToJUnitTests
		.associateBy({ it }) { project.property(it) }
		.toMutableMap()
	// TODEL https://github.com/gradle/gradle/issues/861
	properties.forEach { (name, value) -> inputs.property(name, value) }
	properties.forEach { (name, value) -> value?.let { systemProperty(name, value) } }

	if (!providers.systemProperty("idea.paths.selector").isPresent) {
		logger.info("Writing logs to a file instead of console.")
		// TODO when tests run in parallel, sessions will overwrite each other.
		val logFile = layout.buildDirectory.file("logs/integrationTest-selenium.log")
		doFirst {
			logFile.get().asFile.parentFile.mkdirs()
		}
		systemProperty("webdriver.chrome.logfile", logFile.get().asFile.absolutePath)
	}
}
