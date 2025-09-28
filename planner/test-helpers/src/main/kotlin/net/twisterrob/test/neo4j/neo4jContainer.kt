package net.twisterrob.test.neo4j

import net.twisterrob.test.readManifestEntry
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName
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
