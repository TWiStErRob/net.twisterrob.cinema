plugins {
	id("net.twisterrob.cinema.application")
}

application {
	mainClass = "net.twisterrob.cinema.cineworld.sync.Main"
	tasks.named<JavaExec>("run") {
		val configFiles = if (project.property("net.twisterrob.run.verboseSync").toString().toBoolean())
			"log4j2.xml"
		else
			"log4j2.xml,log4j2-sync.xml"
		jvmArgs(
			"-Dlog4j.configurationFile=${configFiles}",
			"-Dlog4j2.skipJansi=false",
		)
		// Can be overridden with `gradlew :backend:sync:run --args="<sync-type...>"`.
		args(
			// Types of entities synchronized from syndication.
			"cinemas", "films", "performances"
		)
	}
}

tasks.register<JavaExec>("generate") {
	dependsOn(tasks.jar)
	classpath(sourceSets["main"].runtimeClasspath)
	mainClass = "net.twisterrob.cinema.cineworld.generate.Main"
	args("test/weekly_film_times.xml")
}

dependencies {
	implementation(projects.backend.database)
	implementation(projects.backend.feed)
	implementation(projects.backend.network)

	implementation(libs.kotlin.stdlib8)
	runtimeOnly(libs.ktor.client.engine.okhttp)
	Deps.slf4jToLog4j(project)
	Deps.dagger(project)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(libs.test.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = libs.test.junit.vintage.get().module.group, module = libs.test.junit.vintage.get().module.name)
	}
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(projects.testHelpers)
	testImplementation(testFixtures(projects.backend.database))

	testImplementation(libs.jackson.module.kotlin)
	testImplementation(libs.jackson.datatype.java8)
	testImplementation(libs.test.containersJupiter)
	testImplementation(libs.test.containersNeo4j)

	integrationTestImplementation(projects.testHelpers)

	testFixturesImplementation(projects.backend.database)
	testFixturesImplementation(projects.testHelpers)
	testFixturesImplementation(libs.kotlin.stdlib8)
	testFixturesImplementation(libs.test.jfixture)
}

configurations.all {
	//resolutionStrategy.failOnVersionConflict()
}
