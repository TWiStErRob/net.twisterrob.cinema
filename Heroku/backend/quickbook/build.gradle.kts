plugins {
	id("net.twisterrob.cinema.library")
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("net.twisterrob.cinema.heroku.plugins.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)

	Deps.dagger(project)

	api(libs.ktor.client)
	implementation(libs.ktor.client.content)
	implementation(libs.ktor.serialization.jackson)
	implementation(libs.jackson.datatype.java8)
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

	Deps.slf4jToLog4jForTest(project)
}
