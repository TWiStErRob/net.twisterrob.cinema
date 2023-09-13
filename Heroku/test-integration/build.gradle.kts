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
	Deps.slf4jToLog4jForTest(project, testType = "integrationExternalTest")
	integrationExternalTestImplementation(projects.testHelpers)

	implementation(libs.test.assertj)
	implementation(libs.test.selenium)
	implementation(libs.test.selenium.angular)
	integrationExternalTestRuntimeOnly(libs.test.selenium.jdkHttp)
}

tasks.integrationExternalTest.configure {
	val propertyNamesToExposeToJUnitTests = listOf(
		"net.twisterrob.test.selenium.baseUrl",
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

	val screenshotsDir = layout.buildDirectory.dir("reports/tests/integrationExternalTest-screenshots")
	doFirst {
		screenshotsDir.get().asFile.parentFile.mkdirs()
	}
	systemProperty("net.twisterrob.test.selenium.screenshot.dir", screenshotsDir.get().asFile.absolutePath)
}
