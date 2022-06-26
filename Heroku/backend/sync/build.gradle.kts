plugins {
	id("java")
	id("java-test-fixtures")
	id("application")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

application {
	publishSlimJar()
	mainClass.set("net.twisterrob.cinema.cineworld.sync.Main")
	tasks.named<JavaExec>("run") {
		jvmArgs(
			"-Dlog4j.configurationFile=log4j2.xml,log4j2-sync.xml"
		)
		// Can be overridden with `gradlew :backend:sync:run --args <sync-type...>`.
		args(
			// Types of entities synchronized from syndication.
			"cinemas", "films", "performances"
		)
	}
}

dependencies {
	implementation(project(":backend:database"))
	implementation(project(":backend:feed"))
	implementation(project(":backend:network"))

	implementation(libs.kotlin.stdlib8)
	runtimeOnly(Deps.Ktor.client.engine_okhttp)
	Deps.Log4J2.slf4j(project)
	Deps.Dagger2.default(project)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(libs.test.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = libs.test.junit.vintage.get().module.group, module = libs.test.junit.vintage.get().module.name)
	}
	testImplementation(libs.test.mockito.inline)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(project(":test-helpers"))
	testImplementation(testFixtures(project(":backend:database")))

	testImplementation(libs.jackson.module.kotlin)
	testImplementation(libs.jackson.datatype.java8)
	testImplementation(libs.neo4j.harness) {
		exclude(group = libs.slf4j.nop.get().module.group, module = libs.slf4j.nop.get().module.name)
	}

	testFixturesImplementation(project(":backend:database"))
	testFixturesImplementation(project(":test-helpers"))
	testFixturesImplementation(libs.kotlin.stdlib8)
	testFixturesImplementation(libs.test.jfixture)
}

configurations.all {
	//resolutionStrategy.failOnVersionConflict()
}
