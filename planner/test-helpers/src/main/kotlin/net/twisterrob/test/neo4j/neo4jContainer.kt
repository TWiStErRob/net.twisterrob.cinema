package net.twisterrob.test.neo4j

import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName
import java.net.URI

fun neo4jContainer(): Neo4jContainer<*> =
	Neo4jContainer(DockerImageName.parse("neo4j:2025.07.1"))
		.withoutAuthentication()

val Neo4jContainer<*>.boltURI: URI
	get() = URI.create(boltUrl)

fun Neo4jContainer<*>.createDriver(): Driver =
	GraphDatabase.driver(this.boltUrl)
