plugins {
	id("java")
}

val topProject = project
subprojects {
	plugins.withType<JavaPlugin> {
		this@subprojects.tasks {
			val copyLoggingResources = register<Copy>("copyLoggingResources") {
				from(topProject.file("config/log4j2.xml"))
				into(this@subprojects.sourceSets["main"].resources.srcDirs.first())
			}
			"processResources" {
				dependsOn(copyLoggingResources)
			}
			val copyLoggingTestResources = register<Copy>("copyLoggingTestResources") {
				from(topProject.file("config/log4j2.xml"))
				into(this@subprojects.sourceSets["test"].resources.srcDirs.first())
			}
			"processTestResources"{
				dependsOn(copyLoggingTestResources)
			}
		}
	}
}
