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

	implementation(libs.test.selenium)
	integrationTestRuntimeOnly(libs.test.selenium.jdkHttp)
	implementation("org.assertj:assertj-core:3.24.2")
}
