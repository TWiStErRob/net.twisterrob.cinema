plugins {
	id("java-library")
	id("java-test-fixtures")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(Deps.Kotlin.core)
	implementation(Deps.Kotlin.reflect)

	Deps.Dagger2.default(project)
}

// Graph database
dependencies {
	api(Deps.Jackson.databind) // override to equalize versions
	api(Deps.Neo4JOGM.core)
	implementation(Deps.Neo4JOGM.driver)
	runtimeOnly(Deps.Neo4JOGM.driver_bolt)
	runtimeOnly(Deps.Neo4JOGM.driver_bolt_native_types)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(Deps.JFixture.jfixture)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(Deps.Hamcrest.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = "junit", module = "junit")
	}
	testImplementation(project(":test-helpers"))

	testImplementation(Deps.Neo4JOGM.harness) {
		exclude(group = "org.slf4j", module = "slf4j-nop")
	}

	testRuntimeOnly(Deps.Log4J2.core)
	testRuntimeOnly(Deps.Log4J2.slf4j)

	testFixturesImplementation(project(":backend:quickbook"))
	testFixturesImplementation(Deps.Kotlin.core)
	testFixturesImplementation(Deps.JFixture.jfixture)
	testFixturesImplementation(Deps.JUnit.jupiter)
	testFixturesImplementation(project(":test-helpers"))
}
