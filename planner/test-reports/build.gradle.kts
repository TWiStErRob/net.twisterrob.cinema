import net.twisterrob.cinema.build.dsl.isCI

plugins {
	id("org.gradle.test-report-aggregation")
	id("net.twisterrob.cinema.build.testing")
}

dependencies {
	rootProject.allprojects.forEach {
		if (it.path !in setOf(":", ":backend", ":deploy", project.path)) {
			implementation(it)
		}
	}
}

@Suppress("UnstableApiUsage") // Gradle Test Suites are incubating.
testing.suites.withType<JvmTestSuite>().configureEach suite@{
	targets.configureEach target@{
		testTask.configure task@{
			finalizedBy("${this@suite.name}AggregateTestReport")
		}
	}
}

tasks.withType<TestReport>().configureEach {
	inputs.property("isCI", isCI)
	doLast {
		val reportFile = destinationDirectory.file("index.html").get().asFile
		val successRegex = """(?s)<div class="infoBox" id="failures">\s*<div class="counter">0<\/div>""".toRegex()
		if (!successRegex.containsMatchIn(reportFile.readText())) {
			val clickableUri = reportFile.toURI().toString().replace("file:/", "file:///")
			val message = "There were failing tests. See the report at: ${clickableUri}"
			if (isCI) {
				// On CI we follow the ignoreFailures = true of tests for this task too. Report will fail the check run.
				logger.warn(message)
			} else {
				// Locally blow up.
				throw GradleException(message)
			}
		}
	}
}
