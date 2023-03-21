package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.logging.configureSLF4JBindings
import net.twisterrob.cinema.build.logging.configureVerboseReportsForGithubActions

plugins {
	id("org.gradle.java")
}

configureSLF4JBindings()

tasks.withType<Test> {
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

tasks {
	val sourceSets = java.sourceSets
	val copyLoggingResources = register<Copy>("copyLoggingResources") {
		from(rootProject.file("config/log4j2.xml"))
		into(sourceSets["main"].resources.srcDirs.first())
	}
	"processResources" {
		dependsOn(copyLoggingResources)
	}
	val copyLoggingTestResources = register<Copy>("copyLoggingTestResources") {
		from(rootProject.file("config/log4j2.xml"))
		into(sourceSets["test"].resources.srcDirs.first())
	}
	"processTestResources" {
		dependsOn(copyLoggingTestResources)
	}
}
