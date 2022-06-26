plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("io.gitlab.arturbosch.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)

	Deps.dagger(project)

	api(libs.ktor.client.core.jvm)
	implementation(libs.ktor.client.client)
	implementation(libs.ktor.client.jackson)
	implementation(libs.jackson.datatype.java8)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(projects.testHelpers)

	testImplementation(libs.ktor.client.mock.jvm)
	testImplementation(libs.ktor.client.logging.jvm)
	testRuntimeOnly(libs.ktor.client.engine.okhttp)

	Deps.slf4jToLog4jForTest(project)
}
