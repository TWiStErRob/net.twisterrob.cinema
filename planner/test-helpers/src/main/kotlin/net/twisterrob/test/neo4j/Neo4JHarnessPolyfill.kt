package net.twisterrob.test.neo4j

import org.neo4j.driver.Driver
import org.neo4j.driver.SimpleQueryRunner
import org.neo4j.driver.types.Node
import org.neo4j.driver.types.Relationship

fun Driver.session(block: (session: org.neo4j.driver.Session) -> Unit) {
	this.session().use { session ->
		block(session)
	}
}

/**
 * @receiver org.neo4j.driver.Session
 */
val SimpleQueryRunner.allNodes: Iterable<Node>
	get() =
		this
			.run(
				"MATCH (n) RETURN n",
			)
			.asSequence()
			.map { it["n"].asNode() }
			.asIterable()

@Deprecated("id is deprecated, use elementId instead.", ReplaceWith("elementId"))
val Node.id: Long
	@Suppress("DEPRECATION", "DEPRECATED_JAVA_ANNOTATION") // Replicating original harness behavior.
	@java.lang.Deprecated(forRemoval = true)
	get() = this.id()

val Node.allProperties: Map<String, Any?>
	get() = this.asMap()

/**
 * @receiver org.neo4j.driver.Session
 */
fun SimpleQueryRunner.relationshipOf(node: Node): Iterable<Relationship> =
	this
		.run(
			$$"MATCH (n)-[r]->() WHERE elementId(n) = $elementId RETURN r",
			mapOf("elementId" to node.elementId()),
		)
		.asSequence()
		.map { it["r"].asRelationship() }
		.asIterable()
