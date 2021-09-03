import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java")
	id("application")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

application {
	mainClass.set("net.twisterrob.cinema.cineworld.backend.MainKt")
	tasks.named<JavaExec>("run") {
		// The folder that :frontend builds into.
		// Can be overridden with `gradlew :backend:endpoint:run --args <folder>`.
		args("../../deploy/static", "../src/test/fake")
	}
}

dependencies {
	implementation(project(":backend:database"))
	implementation(project(":backend:quickbook"))

	implementation(Deps.Kotlin.core)
	Deps.Ktor.server.default(project)
	runtimeOnly(Deps.Ktor.client.engine_okhttp)
	implementation(Deps.Ktor.client.jackson)
	implementation(Deps.Ktor.client.logging_jvm)
	Deps.Log4J2.slf4j(project)
	Deps.Dagger2.default(project)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(Deps.JFixture.jfixture)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(Deps.Hamcrest.jsonAssert)
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

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = freeCompilerArgs + listOf(
			"-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI"
		)
	}
}

tasks {
	val copyConfigResources = register<Copy>("copyConfigResources") {
		from(project(":backend").file("config/default-env.json"))
		into(project.sourceSets["main"].resources.srcDirs.first())
	}
	"processResources" {
		dependsOn(copyConfigResources)
	}
}
