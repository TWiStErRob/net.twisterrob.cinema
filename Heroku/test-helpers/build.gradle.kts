plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
}

dependencies {
	implementation(Deps.Kotlin.core)

	compileOnly(Deps.JUnit.jupiter)
	compileOnly(Deps.JFixture.jfixture)
	compileOnly(Deps.Hamcrest.core)
	compileOnly(Deps.Mockito.core3)
	compileOnly(Deps.Mockito.kotlin)

	compileOnly(Deps.Neo4JOGM.harness)

	compileOnly(Deps.Ktor.client.mock_jvm)
	compileOnly(Deps.Ktor.server.test)

	Deps.JUnit.junit5(project)
	testImplementation(Deps.JUnit.jupiter)
	testImplementation(Deps.Hamcrest.core)
	// TODO add more testImplementation when writing tests
}
