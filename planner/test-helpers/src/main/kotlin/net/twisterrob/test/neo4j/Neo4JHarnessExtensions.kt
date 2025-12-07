package net.twisterrob.test.neo4j

import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.harness.Neo4j
import java.net.URI

val Neo4j.boltURI: URI
	get() = this.boltURI()

fun Neo4j.createDriver(): Driver =
	GraphDatabase.driver(this.boltURI())

fun <R> Neo4j.session(block: (Session).() -> R): R =
	this.createDriver().use { it.session().use(block) }
