plugins {
	id("java")
}

val topProject = this.project
subprojects {
	val subproject = this@subprojects
	plugins.withType<JavaPlugin> {
		val copyLoggingResources = subproject.tasks.register<Copy>("copyLoggingResources") {
			this.from(topProject.file("config/log4j2.xml"))
			this.into(subproject.sourceSets["main"].resources.srcDirs.first())
		}
		subproject.tasks.named("processResources").configure { dependsOn(copyLoggingResources) }

		val copyLoggingTestResources = subproject.tasks.register<Copy>("copyLoggingTestResources") {
			this.from(topProject.file("config/log4j2.xml"))
			this.into(subproject.sourceSets["test"].resources.srcDirs.first())
		}
		subproject.tasks.named("processTestResources").configure { dependsOn(copyLoggingTestResources) }
	}
}
