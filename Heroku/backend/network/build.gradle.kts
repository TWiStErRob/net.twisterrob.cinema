plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("io.gitlab.arturbosch.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.ktor.client.client)
	implementation(libs.ktor.client.logging.jvm)
	implementation(libs.slf4j.core)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(libs.ktor.client.mock.jvm)
	testImplementation(libs.ktor.client.logging.jvm)
	testImplementation(project(":test-helpers"))
	Deps.Log4J2.slf4jForTest(project)
}
