plugins {
	id("net.twisterrob.cinema.library")
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("net.twisterrob.cinema.heroku.plugins.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.ktor.client)
	implementation(libs.ktor.client.logging)
	implementation(libs.slf4j.core)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(libs.ktor.client.mock)
	testImplementation(libs.ktor.client.logging)
	testImplementation(projects.testHelpers)
	Deps.slf4jToLog4jForTest(project)
}
