plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
}

dependencies {
	implementation(Deps.Kotlin.core)
	implementation(Deps.Ktor.client.client)
	implementation(Deps.Ktor.client.logging_jvm)
	Deps.Log4J2.slf4j(project)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(Deps.Mockito.core3)
	testImplementation(Deps.Mockito.kotlin)
	testImplementation(Deps.Ktor.client.mock_jvm)
	testImplementation(Deps.Ktor.client.logging_jvm)
	testImplementation(project(":test-helpers"))
}
