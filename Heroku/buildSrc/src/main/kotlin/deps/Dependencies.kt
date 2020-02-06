@file:Suppress("unused", "RemoveCurlyBracesFromTemplate", "MemberVisibilityCanBePrivate")

package deps

import org.gradle.api.artifacts.dsl.DependencyHandler

private object Versions {
	const val okhttp3 = "3.10.0"
}

object OkHttp3 {
	const val version = "3.10.0"

	const val core = "com.squareup.okhttp3:okhttp:${version}"
	const val interceptor_logging = "com.squareup.okhttp3:logging-interceptor:${version}"
	/** `exclude module: 'junit'` */
	const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${version}"
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

	fun junit5(dependencies: DependencyHandler) {
		dependencies.add("testImplementation", jupiter)
		dependencies.add("testRuntimeOnly", platform)
		dependencies.add("testRuntimeOnly", jupiterEngine)
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
	const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${versionKotlin}"
}

object Hamcrest {
	const val version = "2.2"

	const val core = "org.hamcrest:hamcrest-core:${version}"
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
}

/**
 * http://ktor.io/quickstart/quickstart/intellij-idea/gradle.html
 * `maven { name = "ktor"; url = "https://dl.bintray.com/kotlin/ktor" }`
 */
object Ktor {
	const val version = "0.9.3"

	const val core = "io.ktor:ktor-server-core:${version}"
	const val netty = "io.ktor:ktor-server-netty:${version}"
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

	const val version = "3.1.0" // https://neo4j.com/developer/neo4j-ogm/#reference:getting-started:versions
	const val version_neo4j = "3.3.6"
	/**
	 * Warning "The following annotation processors were detected on the compile classpath:"
	 *  * 'org.neo4j.kernel.impl.annotations.ServiceProcessor'
	 *  * 'org.neo4j.kernel.impl.annotations.DocumentationProcessor'
	 * Solution: `exclude module: 'neo4j-kernel'`
	 * @see [https://neo4j.com/docs/ogm-manual/current/]
	 */
	const val core = "org.neo4j:neo4j-ogm-core:${version}"
	const val driver_bolt = "org.neo4j:neo4j-ogm-bolt-driver:${version}"
	const val driver_http = "org.neo4j:neo4j-ogm-http-driver:${version}"

	/**
	 * Warning: Includes an old version of [harness], so needs:
	 * ```
	 * exclude module: 'neo4j-security-enterprise' // to prevent logger conflicts
	 * exclude module: 'neo4j-kernel' // see neo4j-ogm-core
	 * exclude module: 'logback-classic'
	 * ```
	 */
	const val test = "org.neo4j:neo4j-ogm-test:${version}"
	const val harness = "org.neo4j.test:neo4j-harness:${version_neo4j}"
	/**
	 * TODO `{ transitive = true }` why?
	 */
	const val neo4j_kernel = "org.neo4j:neo4j-kernel:${version_neo4j}"
}

object Jackson {
	const val version = "2.10.2"

	const val databind = "com.fasterxml.jackson.core:jackson-databind:2.10.1"
	const val dataformat_xml = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.10.1"
	const val module_kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2"
	const val datatype_java8 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.2"
}
