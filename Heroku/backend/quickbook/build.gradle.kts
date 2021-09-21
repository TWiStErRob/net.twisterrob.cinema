plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(Deps.Kotlin.core)
	implementation(Deps.Kotlin.reflect)

	Deps.Dagger2.default(project)
	Deps.Ktor.client.default(project)
	implementation(Deps.Jackson.datatype_java8)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(Deps.JFixture.jfixture)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(project(":test-helpers"))
	Deps.Log4J2.slf4jForTest(project)
}
