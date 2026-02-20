package net.twisterrob.test.neo4j

import org.neo4j.driver.Driver
import org.neo4j.driver.Session
import org.neo4j.driver.SimpleQueryRunner
import org.neo4j.driver.types.Node
import org.neo4j.driver.types.Relationship

fun <R> Driver.session(block: (Session).() -> R): R =
	this.session().use(block)

/**
 * @receiver org.neo4j.driver.Session
 */
val SimpleQueryRunner.allNodes: Iterable<Node>
	get() =
		this
			.run(
				// language=cypher
				"""
					MATCH (n)
					RETURN n
				""".trimIndent(),
			)
			.asSequence()
			.map { it["n"].asNode() }
			.asIterable()

/**
 * @receiver org.neo4j.driver.Session
 */
val SimpleQueryRunner.allRelationships: Iterable<Relationship>
	get() =
		this
			.run(
				// language=cypher
				"""
					MATCH ()-[r]->()
					RETURN r
				""".trimIndent(),
			)
			.asSequence()
			.map { it["r"].asRelationship() }
			.asIterable()

@Deprecated("id is deprecated, use elementId instead.", ReplaceWith("elementId"))
val Node.id: Long
	@Suppress(
		"DEPRECATION", "DEPRECATED_JAVA_ANNOTATION",
		"detekt.ForbiddenAnnotation", "detekt.UnnecessaryFullyQualifiedName",
	)
	@java.lang.Deprecated(forRemoval = true) // Replicating original harness behavior.
	get() = this.id()

val Node.allProperties: Map<String, Any?>
	get() = this.asMap()

/**
 * @receiver org.neo4j.driver.Session
 */
private fun SimpleQueryRunner.relationshipsOf(node: Node): Iterable<Relationship> =
	this
		.run(
			// language=cypher
			$$"""
				MATCH (n)-[r]-()
				WHERE elementId(n) = $elementId
				RETURN r
			""".trimIndent(),
			mapOf("elementId" to node.elementId()),
		)
		.asSequence()
		.map { it["r"].asRelationship() }
		.asIterable()

context(session: SimpleQueryRunner)
val Node.relationships: Iterable<Relationship>
	get() = session.relationshipsOf(this)
