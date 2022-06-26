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

object Mockito {
	const val version3 = "3.2.4"
	const val versionKotlin = "2.2.0"

	const val core3 = "org.mockito:mockito-core:${version3}"
	const val core3Inline = "org.mockito:mockito-inline:${version3}"
	const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${versionKotlin}"
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
	const val version = "2.42"

	const val inject = "javax.inject:javax.inject:1"
	const val core = "com.google.dagger:dagger:${version}"
	const val apt = "com.google.dagger:dagger-compiler:${version}"

	const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"

	fun default(project: Project) {
		project.dependencies {
			add("implementation", core)
			add("kapt", apt)
			add("kaptTest", apt)
		}
	}
}

object Neo4JOGM {

	/**
	 * @see Neo4JOGM.version_neo4j should be listed in https://neo4j.com/developer/neo4j-ogm/#reference:getting-started:versions
	 */
	const val version = "3.2.25"

	/**
	 * https://mvnrepository.com/artifact/org.neo4j.test/neo4j-harness
	 * Match version of DB at https://console.neo4j.io/#databases, exactly to minor.
	 * @see Neo4JOGM.version should match supporting version at https://neo4j.com/developer/neo4j-ogm/#reference:getting-started:versions
	 * @see Log4J2.version should match shaded dependency in [https://github.com/neo4j/neo4j/blob/4.2/pom.xml](neo4j)
	 */
	const val version_neo4j = "4.2.4"

	/**
	 * Warning "The following annotation processors were detected on the compile classpath:"
	 *  * 'org.neo4j.kernel.impl.annotations.ServiceProcessor'
	 *  * 'org.neo4j.kernel.impl.annotations.DocumentationProcessor'
	 *
	 * Solution (remove processors): `exclude module: 'neo4j-kernel'`
	 *
	 * Solution (disable classpath detection):
	 * ```kotlin
	 * plugins.withId("org.jetbrains.kotlin.kapt") {
	 *     val kapt = this@allprojects.extensions.getByName<KaptExtension>("kapt")
	 *     kapt.apply {
	 *       includeCompileClasspath = false
	 *     }
	 * }
	 * ```
	 * @see [https://neo4j.com/docs/ogm-manual/current/]
	 */
	const val core = "org.neo4j:neo4j-ogm-core:${version}"
	const val driver = "org.neo4j.driver:neo4j-java-driver:${version_neo4j}"
	const val driver_bolt = "org.neo4j:neo4j-ogm-bolt-driver:${version}"

	/**
	 *  * [Opt-in to use native types](https://neo4j.com/docs/ogm-manual/current/reference/#reference:native-property-types:optin)
	 *  * [Native vs Java 8 types](https://neo4j.com/docs/ogm-manual/current/reference/#reference:native-property-types:mapping)
	 *  * [Cypher temporal functions](https://neo4j.com/docs/cypher-manual/current/syntax/temporal/)
	 */
	const val driver_bolt_native_types = "org.neo4j:neo4j-ogm-bolt-native-types:${version}"

	/**
	 * When using this, mind https://github.com/neo4j/neo4j/issues/12770.
	 * To work around, use:
	 * ```
	 * testImplementation(Deps.Neo4JOGM.harness) {
	 *     exclude(group = "org.slf4j", module = "slf4j-nop")
	 * }
	 * ```
	 */
	const val harness = "org.neo4j.test:neo4j-harness:${version_neo4j}"
}
