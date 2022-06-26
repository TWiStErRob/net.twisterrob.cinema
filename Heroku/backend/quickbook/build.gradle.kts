plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("io.gitlab.arturbosch.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)

	Deps.Dagger2.default(project)
	Deps.Ktor.client.default(project)
	implementation(libs.jackson.datatype.java8)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(project(":test-helpers"))
	Deps.Log4J2.slf4jForTest(project)
}
