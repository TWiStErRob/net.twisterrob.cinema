@file:Suppress(
	"unused", // these will be used from build.gradle files
	"MemberVisibilityCanBePrivate", // these will be used from build.gradle files
	"ClassName", // object _foo, val foo = _foo, object foo are OK
	"RemoveCurlyBracesFromTemplate" // want to have clean separation
)

package deps

import net.twisterrob.cinema.heroku.plugins.internal.libs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

object JUnit {
	fun junit5(project: Project) {
		project.dependencies {
			add("testImplementation", project.libs.test.junit.jupiter)
			add("testRuntimeOnly", project.libs.test.junit.platform)
			add("testRuntimeOnly", project.libs.test.junit.jupiter.engine)
		}
		//project.tasks.named<Test>("test").configure { useJUnitPlatform() }
	}
}

object Ktor {

	val client = _client // hack for "Nested object '*' accessed via instance reference"

	object _client {

		fun default(project: Project) {
			project.dependencies {
				add("api", project.libs.ktor.client.core.jvm)
				add("implementation", project.libs.ktor.client.client)
				add("implementation", project.libs.ktor.client.jackson)
				add("testImplementation", project.libs.ktor.client.mock.jvm)
				add("testImplementation", project.libs.ktor.client.logging.jvm)
				add("testRuntimeOnly", project.libs.ktor.client.engine.okhttp)
				//add("implementation", project.libs.kotlinx.coroutines)
			}
		}
	}

	val server = _server // hack for "Nested object '*' accessed via instance reference"

	object _server {

		fun default(project: Project) {
			project.dependencies {
				add("implementation", project.libs.ktor.server.core)
				add("implementation", project.libs.ktor.server.locations)
				add("implementation", project.libs.ktor.server.engine.netty)
				add("implementation", project.libs.ktor.server.content.jackson)
				add("implementation", project.libs.ktor.server.content.html)
				add("testImplementation", project.libs.ktor.client.mock.jvm)
				add("testImplementation", project.libs.ktor.server.test) {
					exclude(group = "ch.qos.logback", module = "logback-classic")
				}
			}
		}
	}
}

object Log4J2 {
	fun slf4j(project: Project) {
		project.dependencies {
			add("implementation", project.libs.slf4j.core)
			add("runtimeOnly", project.libs.log4j.api)
			add("runtimeOnly", project.libs.log4j.core)
			add("runtimeOnly", project.libs.log4j.slf4j)
		}
	}

	fun slf4jForTest(project: Project) {
		project.dependencies {
			add("testRuntimeOnly", project.libs.slf4j.core)
			add("testRuntimeOnly", project.libs.log4j.api)
			add("testRuntimeOnly", project.libs.log4j.core)
			add("testRuntimeOnly", project.libs.log4j.slf4j)
		}
	}
}

object Dagger2 {
	fun default(project: Project) {
		project.dependencies {
			add("implementation", project.libs.dagger)
			add("kapt", project.libs.dagger.apt)
			add("kaptTest", project.libs.dagger.apt)
		}
	}
}

private fun DependencyHandlerScope.add(
	configurationName: String,
	dependencyNotation: Provider<MinimalExternalModuleDependency>,
	configuration: Action<ExternalModuleDependency>
) {
	addProvider(configurationName, dependencyNotation, configuration)
}

