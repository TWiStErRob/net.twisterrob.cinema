import net.twisterrob.cinema.build.dsl.isCI
import net.twisterrob.gradle.doNotNagAbout

tasks.register<Delete>("clean") {
	delete(rootProject.buildDir)
}

tasks.register<Task>("allDependencies") {
	val projects = project.allprojects.sortedBy { it.name }
	doFirst {
		println(projects.joinToString(prefix = "Printing dependencies for modules:\n", separator = "\n") { " * ${it}" })
	}
	val dependenciesTasks = projects.map { it.tasks.named("dependencies") }
	// Builds a dependency chain: 1 <- 2 <- 3 <- 4, so when executed they're in order.
	dependenciesTasks.reduce { acc, task -> task.apply { get().dependsOn(acc) } }
	// Use finalizedBy instead of dependsOn to make sure this task executes first.
	this@register.finalizedBy(dependenciesTasks)
}

// Need to eagerly create this, so that we can call tasks.withType in it.
tasks.create<TestReport>("allTestsReport") {
	destinationDirectory = file("${buildDir}/reports/tests/all")
	project.evaluationDependsOnChildren()
	allprojects.forEach { subproject ->
		subproject.tasks.withType<Test> {
			if (this.name == "unitTest" || this.name == "functionalTest" || this.name == "integrationTest") {
				ignoreFailures = true
				reports.junitXml.required = true
				this@create.testResults.from(this@withType)
			}
		}
	}
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

val gradleVersion: String = GradleVersion.current().version

if (libs.versions.appengine.get() < "2.4.6") {
	// TODEL AppEngine 2.4.5 v Gradle 8: https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/446
	@Suppress("MaxLineLength")
	doNotNagAbout(
		"The AbstractArchiveTask.archivePath property has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Please use the archiveFile property instead. " +
				"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
		"at com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin.lambda\$configureExtensions$0"
	)
} else {
	error("AppEngine 2.4.6 deprecation fixed, remove suppression.")
}
