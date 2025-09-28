plugins {
	id("net.twisterrob.cinema.library")
}

dependencies {
	implementation(libs.kotlin.stdlib8)

	// Note all these are optional dependencies.
	// Add them to testImplementation wherever testImplementation(projects.testHelpers) is used.

	compileOnly(libs.test.junit.jupiter)
	compileOnly(libs.test.jfixture)
	compileOnly(libs.test.hamcrest)
	compileOnly(libs.test.mockito)
	compileOnly(libs.test.mockito.kotlin)

	compileOnly(libs.neo4j)
	compileOnly(libs.neo4j.harness)
	compileOnly(libs.test.containersNeo4j)

	compileOnly(libs.ktor.client.mock)
	compileOnly(libs.ktor.server.test)

	// Expose this to every module to make sure java.util.logging.config.file / logging.properties can pick it up.
	runtimeOnly(libs.slf4j.jul)

	Deps.junit5(project)
	testImplementation(libs.test.junit.jupiter)
	testImplementation(libs.test.hamcrest)
	Deps.slf4jToLog4jForTest(project)
	// TODO add more testImplementation when writing tests
}
