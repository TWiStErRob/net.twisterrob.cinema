import net.twisterrob.cinema.heroku.plugins.internal.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

object Deps {

	fun junit5(project: Project) {
		project.dependencies {
			add("testImplementation", project.libs.test.junit.jupiter)
			add("testImplementation", project.libs.test.junit.jupiter.params)
			add("testRuntimeOnly", project.libs.test.junit.platform)
			add("testRuntimeOnly", project.libs.test.junit.jupiter.engine)
		}
		//project.tasks.named<Test>("test").configure { useJUnitPlatform() }
	}

	fun slf4jToLog4j(project: Project) {
		project.dependencies {
			add("implementation", project.libs.slf4j.core)
			add("runtimeOnly", project.libs.bundles.log4j)
		}
	}

	fun slf4jToLog4jForTest(project: Project) {
		project.dependencies {
			add("testRuntimeOnly", project.libs.slf4j.core)
			add("testRuntimeOnly", project.libs.bundles.log4j)
		}
	}

	fun dagger(project: Project) {
		project.dependencies {
			add("implementation", project.libs.dagger)
			add("kapt", project.libs.dagger.apt)
			add("kaptTest", project.libs.dagger.apt)
		}
	}
}
