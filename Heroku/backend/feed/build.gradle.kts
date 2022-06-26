plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("io.gitlab.arturbosch.detekt")
}

sourceSets {
	test {
		resources.srcDir(project(":backend:sync").file("test"))
	}
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)

	Deps.dagger(project)

	implementation(libs.jackson.dataformat.xml)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.jackson.datatype.java8)
}

// Network
dependencies {
	api(libs.ktor.client.core.jvm)
	implementation(libs.ktor.client.client)
	implementation(libs.ktor.client.jackson)
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
	testImplementation(project(":test-helpers"))

	testImplementation(libs.ktor.client.mock.jvm)
	testImplementation(libs.ktor.client.logging.jvm)
	testRuntimeOnly(libs.ktor.client.engine.okhttp)

	Deps.slf4jToLog4jForTest(project)
}
