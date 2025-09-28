package net.twisterrob.test.neo4j

import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName
import java.net.JarURLConnection
import java.net.URI

private val NEO4J_VERSION: String by lazy {
	readManifestEntry("Neo4j-Version")
}

fun neo4jContainer(): Neo4jContainer<*> =
	Neo4jContainer(DockerImageName.parse("neo4j:${NEO4J_VERSION}"))
		.withoutAuthentication()

val Neo4jContainer<*>.boltURI: URI
	get() = URI.create(boltUrl)

fun Neo4jContainer<*>.createDriver(): Driver =
	GraphDatabase.driver(this.boltUrl)

fun <R> Neo4jContainer<*>.session(block: (Session).() -> R): R =
	this.createDriver().use { driver ->
		driver.session().use(block)
	}

private fun readManifestEntry(manifestEntry: String): String {
	val aClassInJar = object {}.javaClass
	val aClassName = aClassInJar.enclosingClass.simpleName + ".class"
	val res = aClassInJar.getResource(aClassName) ?: error("Cannot find class file ${aClassName}")
	val url = res.openConnection() ?: error("Cannot open ${res}")
	when {
		url is JarURLConnection -> {
			val mf = url.manifest ?: error("Cannot find manifest in ${url.jarFileURL}")
			val version = mf.mainAttributes.getValue(manifestEntry)
				?: error("${manifestEntry} attribute not present in manifest\n${url.manifest.mainAttributes.toMap()}")
			return version
		}

		else -> error("Unsupported packaging mechanism, no JAR file to get manifest from.")
	}
}
