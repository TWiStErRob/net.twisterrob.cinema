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

private object Versions {
	const val okhttp3 = "3.10.0"
}

object OkHttp3 {
	const val version = "3.10.0"

	const val core = "com.squareup.okhttp3:okhttp:${version}"
	const val interceptor_logging = "com.squareup.okhttp3:logging-interceptor:${version}"
	/** `exclude module: 'junit'` */
	const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${version}"

	fun default(project: Project) {
		project.dependencies {
			add("implementation", core)
			add("implementation", interceptor_logging)
			add("testImplementation", mockwebserver) {
				exclude(module = "junit")
			}
		}
	}
}

object Retrofit2 {
	const val version = "2.4.0"

	const val core = "com.squareup.retrofit2:retrofit:${version}"
	const val mock = "com.squareup.retrofit2:retrofit-mock:${version}"

	const val converter_scalars = "com.squareup.retrofit2:converter-scalars:${version}"
	const val converter_simplexml = "com.squareup.retrift2:converter-simplexml:${version}"
	const val converter_gson = "com.squareup.retrift2:converter-gson:${version}"
	const val converter_moshi = "com.squareup.retrift2:converter-moshi:${version}"
	const val converter_jackson = "com.squareup.retrift2:converter-jackson:${version}"
	const val converter_wire = "com.squareup.retrift2:converter-wire:${version}"
	const val converter_protobuf = "com.squareup.retrift2:converter-protobuf:${version}"

	const val adapter_java8 = "com.squareup.retrofit2:adapter-java8:${version}"
	/** `exclude module: 'rxjava'` */
	const val adapter_rxjava = "com.squareup.retrofit2:adapter-rxjava:${version}"
	/** `exclude module: 'rxjava'` */
	const val adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${version}"
	const val adapter_guava = "com.squareup.retrofit2:adapter-guava:${version}"

	fun default(project: Project) {
		project.dependencies {
			add("implementation", core) {
				exclude(module = "okhttp")
			}
			add("implementation", converter_scalars)
			add("testImplementation", mock)
		}
	}
}

object RxJava2 {
	const val version = "2.1.13"

	const val core = "io.reactivex.rxjava2:rxjava:${version}"
}

object JUnit {
	const val version4 = "4.13"
	const val versionPlatform = "1.6.0"
	const val versionJupiter = "5.6.0"

	const val junit4 = "junit:junit:${version4}"
	const val platform = "org.junit.platform:junit-platform-launcher:${versionPlatform}"
	const val jupiter = "org.junit.jupiter:junit-jupiter-api:${versionJupiter}"
	const val jupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${versionJupiter}"
	const val vintage = junit4
	const val vintageEngine = "org.junit.vintage:junit-vintage-engine:${versionJupiter}"

	fun junit5(project: Project) {
		project.dependencies {
			add("testImplementation", jupiter)
			add("testRuntimeOnly", platform)
			add("testRuntimeOnly", jupiterEngine)
		}
		//project.tasks.named<Test>("test").configure { useJUnitPlatform() }
	}
}

object Mockito {
	const val version1 = "1.10.19"
	const val version2 = "2.28.2"
	const val version3 = "3.2.4"
	const val versionKotlin = "2.2.0"

	const val core1 = "org.mockito:mockito-core:${version1}"
	const val core2 = "org.mockito:mockito-core:${version2}"
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

object JFixture {
	const val version = "2.7.2"

	const val jfixture = "com.flextrade.jfixture:jfixture:${version}"
}

object Kotlin {
	const val version = "1.3.61"

	const val core = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
	const val reflect = "org.jetbrains.kotlin:kotlin-reflect"
	const val kotlinx_html = "org.jetbrains.kotlinx:kotlinx-html-js:0.6.11"
	const val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3"
}

/**
 * http://ktor.io/quickstart/quickstart/intellij-idea/gradle.html
 * `maven { name = "ktor"; url = "https://dl.bintray.com/kotlin/ktor" }`
 */
object Ktor {

	const val version = "1.3.1"

	val client = _client // hack for "Nested object '*' accessed via instance reference"

	object _client {
		const val client = "io.ktor:ktor-client:${version}"
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
				add("testRuntimeOnly", engine_okhttp)
				//add("implementation", Kotlin.coroutines)
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
				add("testImplementation", test)
			}
		}
	}

	fun repo(project: Project) {
		project.repositories {
			maven { name = "ktor"; setUrl("https://dl.bintray.com/kotlin/ktor") }
		}
	}
}

object SLF4J {
	const val version = "1.7.25"

	const val core = "org.slf4j:slf4j-api:${version}"
	const val log4j12 = "org.slf4j:slf4j-log4j12:${version}"
}

object Log4J {
	const val version = "1.2.17"

	const val core = "log4j:log4j:${version}"
}

object Log4J2 {
	const val version = "2.11.0"

	const val core = "org.apache.logging.log4j:log4j-core:${version}"
	const val api = "org.apache.logging.log4j:log4j-api:${version}"
	const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:${version}"
	const val jul = "org.apache.logging.log4j:log4j-jul:${version}"
}

object Dagger2 {
	const val version = "2.25.4"

	const val inject = "javax.inject:javax.inject:1"
	const val core = "com.google.dagger:dagger:${version}"
	const val apt = "com.google.dagger:dagger-compiler:${version}"

	const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"
}

object Neo4JOGM {

	const val version = "3.2.8" // https://neo4j.com/developer/neo4j-ogm/#reference:getting-started:versions
	const val version_neo4j = "3.4.9"

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
	const val driver_bolt = "org.neo4j:neo4j-ogm-bolt-driver:${version}"
	const val driver_http = "org.neo4j:neo4j-ogm-http-driver:${version}"

	const val harness = "org.neo4j.test:neo4j-harness:${version_neo4j}"
}

object Jackson {
	const val version = "2.10.2"

	const val databind = "com.fasterxml.jackson.core:jackson-databind:2.10.1"
	const val dataformat_xml = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.10.1"
	const val module_kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2"
	const val datatype_java8 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.2"
}
