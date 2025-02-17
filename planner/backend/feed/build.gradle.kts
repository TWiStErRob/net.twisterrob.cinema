plugins {
	id("net.twisterrob.cinema.library")
}

@Suppress("UnstableApiUsage")
testing.suites.named<JvmTestSuite>("integrationTest") {
	sources.resources.srcDir("../sync/test")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)
	implementation(projects.shared)

	Deps.dagger(project)

	implementation(libs.jackson.dataformat.xml)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.jackson.datatype.java8)
}

// Network
dependencies {
	api(libs.ktor.client)
	implementation(libs.ktor.client)
	implementation(libs.ktor.client.content)
	implementation(libs.ktor.serialization.jackson)
}

// Logging
dependencies {
	implementation(libs.slf4j.core)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(projects.testHelpers)

	testImplementation(libs.ktor.client.mock)
	testImplementation(libs.ktor.client.logging)
	testRuntimeOnly(libs.ktor.client.engine.okhttp)

	testFixturesImplementation(libs.test.junit.jupiter)

	Deps.slf4jToLog4jForTest(project)
}
