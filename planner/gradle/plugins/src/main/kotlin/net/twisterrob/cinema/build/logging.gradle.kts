package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.logging.configureSLF4JBindings
import net.twisterrob.cinema.build.logging.configureVerboseReportsForGithubActions

plugins {
	id("org.gradle.java")
}

configureSLF4JBindings()

tasks.withType<Test>().configureEach {
	if (project.property("net.twisterrob.build.verboseReports").toString().toBoolean()) {
		configureVerboseReportsForGithubActions()
	} else {
		//afterTest(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
		//	logger.quiet("Executing test ${descriptor.className}.${descriptor.name} with result: ${result.resultType}")
		//}))
	}
	jvmArgs(
		"-Djava.util.logging.config.file=${rootProject.file("config/logging.properties")}"
	)
}

registerCopyLoggingFor(java.sourceSets["main"])

@Suppress("UnstableApiUsage")
extensions.getByName<TestingExtension>("testing").suites.withType<JvmTestSuite>().configureEach {
	registerCopyLoggingFor(sources)
}

fun Project.registerCopyLoggingFor(sourceSet: SourceSet) {
	val copy = tasks.register<Copy>(sourceSet.getTaskName("copyLogging", "resources")) {
		from(project.rootProject.file("config/log4j2.xml"))
		into(sourceSet.resources.srcDirs.first())
	}

	tasks.named(sourceSet.processResourcesTaskName) {
		dependsOn(copy)
	}
}
