plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(project(":backend:database"))

	implementation(Deps.Kotlin.core)
	Deps.Ktor.server.default(project)

	implementation(Deps.Dagger2.core)
	kapt(Deps.Dagger2.apt)
}

// Logging
dependencies {
	implementation(Deps.SLF4J.core)
	runtimeOnly(Deps.Log4J2.api)
	runtimeOnly(Deps.Log4J2.core)
	runtimeOnly(Deps.Log4J2.slf4j)
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
	testImplementation(Deps.Mockito.core3Inline)
	testImplementation(Deps.Mockito.kotlin)
	testImplementation(project(":test-helpers"))
	testImplementation(testFixtures(project(":backend:database")))

	testImplementation(Deps.Jackson.module_kotlin)
	testImplementation(Deps.Jackson.datatype_java8)
	kaptTest(Deps.Dagger2.apt)
}
