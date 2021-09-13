import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register

/**
 * Build a JAR file of the application with a manifest that has everything ready to be launched with `java -jar`.
 */
fun Project.publishSlimJar() {
	val application = this.extensions.getByName<JavaApplication>("application")
	val dependencies = this.configurations.named("runtimeClasspath")
	this.tasks {
		"jar"(Jar::class) {
			manifest {
				val dependencyFiles = dependencies.map { it.files }
				attributes(
					mapOf(
						"Main-Class" to application.mainClass,
						"Class-Path" to dependencyFiles.map { it.joinToString(separator = " ") { dep -> dep.name } }
					)
				)
			}
		}
		register<Copy>("copyJarDependencies") {
			from(dependencies.get())
			into(tasks.named("jar").map { it.outputs.files.singleFile.parentFile })
		}
	}
}
