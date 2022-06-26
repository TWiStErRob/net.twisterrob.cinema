@file:Suppress(
	"unused", // these will be used from build.gradle files
	"MemberVisibilityCanBePrivate", // these will be used from build.gradle files
	"ClassName", // object _foo, val foo = _foo, object foo are OK
	"RemoveCurlyBracesFromTemplate" // want to have clean separation
)

package deps

import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.repositories
import net.twisterrob.cinema.heroku.plugins.internal.libs

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

object Hamcrest {
	const val version = "2.2"
	const val versionShazamcrest = "0.11"
	const val versionJsonAssert = "1.5.0"

	const val core = "org.hamcrest:hamcrest-core:${version}"
	const val shazamcrest = "com.shazam:shazamcrest:${versionShazamcrest}"
	const val jsonAssert = "org.skyscreamer:jsonassert:${versionJsonAssert}"
}

/**
 * http://ktor.io/quickstart/quickstart/intellij-idea/gradle.html
 * `maven { name = "ktor"; url = "https://dl.bintray.com/kotlin/ktor" }`
 */
object Ktor {

	const val version = "1.6.3"

	val client = _client // hack for "Nested object '*' accessed via instance reference"

	object _client {
		const val client = "io.ktor:ktor-client:${version}"
		const val logging_jvm = "io.ktor:ktor-client-logging-jvm:${version}"
		const val core_jvm = "io.ktor:ktor-client-core-jvm:${version}"
		const val engine_okhttp = "io.ktor:ktor-client-okhttp:${version}"

		const val json = "io.ktor:ktor-client-json:${version}"
		const val json_jvm = "io.ktor:ktor-client-json-jvm:${version}"
		const val gson = "io.ktor:ktor-client-gson:${version}"
		const val jackson = "io.ktor:ktor-client-jackson:${version}"
		const val kotlinx_serialization = "io.ktor:ktor-client-serialization-jvm:${version}"

		const val mock = "io.ktor:ktor-client-mock:${version}"
		const val mock_jvm = "io.ktor:ktor-client-mock-jvm:${version}"
		const val mock_js = "io.ktor:ktor-client-mock-js:${version}"
		const val mock_native = "io.ktor:ktor-client-mock-native:${version}"

		fun default(project: Project) {
			project.dependencies {
				add("api", core_jvm)
				add("implementation", client)
				add("implementation", jackson)
				add("testImplementation", mock_jvm)
				add("testImplementation", logging_jvm)
				add("testRuntimeOnly", engine_okhttp)
				//add("implementation", libs.kotlinx.coroutines)
			}
		}
	}

	val server = _server // hack for "Nested object '*' accessed via instance reference"

	object _server {
		const val core = "io.ktor:ktor-server-core:${version}"
		const val test = "io.ktor:ktor-server-tests:${version}"
		const val locations = "io.ktor:ktor-locations:${version}"

		object engine {
			const val netty = "io.ktor:ktor-server-netty:${version}"
		}

		object content {
			const val jackson = "io.ktor:ktor-jackson:${version}"
			const val html = "io.ktor:ktor-html-builder:${version}"
			const val freemarker = "io.ktor:ktor-freemarker:${version}"
		}

		fun default(project: Project) {
			project.dependencies {
				add("implementation", core)
				add("implementation", locations)
				add("implementation", engine.netty)
				add("implementation", content.jackson)
				add("implementation", content.html)
				add("testImplementation", client.mock_jvm)
				add("testImplementation", test) {
					exclude(group = "ch.qos.logback", module = "logback-classic")
				}
			}
		}
	}
}

object Log4J2 {
	const val version = "2.14.1"

	const val core = "org.apache.logging.log4j:log4j-core:${version}"
	const val api = "org.apache.logging.log4j:log4j-api:${version}"
	const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:${version}"
	const val jul = "org.apache.logging.log4j:log4j-jul:${version}"

	fun slf4j(project: Project) {
		project.dependencies {
			add("implementation", project.libs.slf4j.core)
			add("runtimeOnly", api)
			add("runtimeOnly", core)
			add("runtimeOnly", slf4j)
		}
	}

	fun slf4jForTest(project: Project) {
		project.dependencies {
			add("testRuntimeOnly", project.libs.slf4j.core)
			add("testRuntimeOnly", api)
			add("testRuntimeOnly", core)
			add("testRuntimeOnly", slf4j)
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
