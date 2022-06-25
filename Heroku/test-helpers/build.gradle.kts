plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
}

dependencies {
	implementation(Deps.Kotlin.core)

	// Note all these are optional dependencies.
	// Add them to testImplementation wherever testImplementation(project(":test-helpers")) is used.

	compileOnly(Deps.JUnit.jupiter)
	compileOnly(Deps.JFixture.core)
	compileOnly(Deps.Hamcrest.core)
	compileOnly(Deps.Mockito.core3)
	compileOnly(Deps.Mockito.kotlin)

	compileOnly(Deps.Neo4JOGM.harness)

	compileOnly(Deps.Ktor.client.mock_jvm)
	compileOnly(Deps.Ktor.server.test)

	// Expose this to every module to make sure java.util.logging.config.file / logging.properties can pick it up.
	runtimeOnly(libs.slf4j.jul)

	Deps.JUnit.junit5(project)
	testImplementation(Deps.JUnit.jupiter)
	testImplementation(Deps.Hamcrest.core)
	Deps.Log4J2.slf4jForTest(project)
	// TODO add more testImplementation when writing tests
}
