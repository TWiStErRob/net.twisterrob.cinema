import net.twisterrob.cinema.build.dsl.isCI

plugins {
	id("org.gradle.test-report-aggregation")
	// Apply a similar setup to all other modules in the project.
	id("net.twisterrob.cinema.build.testing")
}

dependencies {
	rootProject.allprojects
		// Skip ourselves to prevent circular dependency.
		.filterNot { it.path == project.path }
		// Skip grouping modules (they don't have suites, i.e. no variants exists in these modules).
		.filterNot { it.path in setOf(":", ":backend", ":deploy") }
		// Add dependency on all modules.
		.forEach { implementation(it) }
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
		val reportHtml = reportFile.readText()
		val failureRegex = """(?s)<div class="infoBox">\s*<div class="counter">(\d+)<\/div>\s*<p>failures</p>""".toRegex()
		if (!failureRegex.containsMatchIn(reportHtml)
			|| failureRegex.findAll(reportHtml).any { it.groups[1]?.value != "0" }
		) {
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

// `gradlew allATR --continue`, otherwise the failures from suite-aggregation tasks fail the build early.
tasks.register<TestReport>("allAggregateTestReport") {
	group = "verification"
	destinationDirectory.convention(java.testReportDir.map { it.dir("all/aggregated-results") })
	testResults.from(testSuite("unitTest"))
	testResults.from(testSuite("functionalTest"))
	testResults.from(testSuite("integrationTest"))
}

fun testSuite(name: String): Provider<FileCollection> =
	configurations.aggregateTestReportResults.map { configuration ->
		@Suppress("UnstableApiUsage")
		configuration.incoming.artifactView {
			withVariantReselection()
			componentFilter { id -> id is ProjectComponentIdentifier }
			attributes {
				attribute(
					Category.CATEGORY_ATTRIBUTE,
					objects.named(Category::class, Category.VERIFICATION)
				)
				attributes.attribute(
					TestSuiteName.TEST_SUITE_NAME_ATTRIBUTE,
					objects.named(TestSuiteName::class, name)
				)
				attribute(
					VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
					objects.named(VerificationType::class, VerificationType.TEST_RESULTS)
				)
			}
		}.files
	}
