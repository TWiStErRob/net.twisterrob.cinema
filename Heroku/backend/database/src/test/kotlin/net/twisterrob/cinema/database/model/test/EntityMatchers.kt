package net.twisterrob.cinema.database.model.test

import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship

/**
 * @sample `assertThat(node.relationships, containsInAnyOrder(hasRelationship(node, "name", node)))`
 */
fun hasRelationship(start: Node, name: String, end: Node): Matcher<Relationship> = Matchers.allOf(
	object : FeatureMatcher<Relationship, Node>(Matchers.equalTo(start), "relationship start node", "startNode") {
		override fun featureValueOf(actual: Relationship): Node =
			actual.startNode
	},
	object : FeatureMatcher<Relationship, Node>(Matchers.equalTo(end), "relationship end node", "endNode") {
		override fun featureValueOf(actual: Relationship): Node =
			actual.endNode
	},
	object : FeatureMatcher<Relationship, String>(Matchers.equalTo(name), "relationship type", "type") {
		override fun featureValueOf(actual: Relationship): String =
			actual.type.name()
	}
)
