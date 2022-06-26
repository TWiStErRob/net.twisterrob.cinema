plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("io.gitlab.arturbosch.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(Deps.Ktor.client.client)
	implementation(Deps.Ktor.client.logging_jvm)
	implementation(libs.slf4j.core)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(Deps.Ktor.client.mock_jvm)
	testImplementation(Deps.Ktor.client.logging_jvm)
	testImplementation(project(":test-helpers"))
	Deps.Log4J2.slf4jForTest(project)
}
