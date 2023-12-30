import net.twisterrob.cinema.build.dsl.libs
import net.twisterrob.cinema.build.testing.kapt
import org.gradle.api.Project
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.base.TestingExtension

@Suppress("StringLiteralDuplication", "MaxChainedCallsOnSameLine")
object Deps {

	fun junit5(project: Project) {
		project.dependencies {
			add("testImplementation", project.libs.test.junit.jupiter)
			add("testImplementation", project.libs.test.junit.jupiter.params)
			add("testImplementation", project.libs.test.junit.pioneer)
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

	fun slf4jToLog4jForTest(project: Project, testType: String = "test") {
		project.dependencies {
			add("${testType}RuntimeOnly", project.libs.slf4j.core)
			add("${testType}RuntimeOnly", project.libs.bundles.log4j)
		}
	}

	fun dagger(project: Project) {
		project.dependencies {
			add("implementation", project.libs.dagger)
			add("kapt", project.libs.dagger.apt)
		}
		// `add("kaptTest", project.libs.dagger.apt)` is not enough, because there are multiple test configurations.
		project.the<TestingExtension>().suites.withType<JvmTestSuite>().configureEach {
			dependencies { kapt(project.libs.dagger.apt) }
		}
	}
}
