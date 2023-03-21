package net.twisterrob.cinema.build

import configureSLF4JBindings
import configureVerboseReportsForGithubActions

project.configureSLF4JBindings()

plugins.withId("java") {
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
}

plugins.withId("java") {
	project.tasks {
		val sourceSets = project.the<JavaPluginExtension>().sourceSets
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
		"processTestResources"{
			dependsOn(copyLoggingTestResources)
		}
	}
}
