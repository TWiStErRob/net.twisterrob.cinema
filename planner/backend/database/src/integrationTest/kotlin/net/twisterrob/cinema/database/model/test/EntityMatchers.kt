package net.twisterrob.cinema.database.model.test

import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.neo4j.driver.types.Node
import org.neo4j.driver.types.Relationship

/**
 * @sample `assertThat(node.relationships, containsInAnyOrder(hasRelationship(node, "name", node)))`
 */
fun hasRelationship(start: Node, name: String, end: Node): Matcher<Relationship> = Matchers.allOf(
	object : FeatureMatcher<Relationship, String>(Matchers.equalTo(start.elementId()), "relationship start node elementId", "startNodeElementId") {
		override fun featureValueOf(actual: Relationship): String =
			actual.startNodeElementId()
	},
	object : FeatureMatcher<Relationship, String>(Matchers.equalTo(end.elementId()), "relationship end node elementId", "endNodeElementId") {
		override fun featureValueOf(actual: Relationship): String =
			actual.endNodeElementId()
	},
	object : FeatureMatcher<Relationship, String>(Matchers.equalTo(name), "relationship type", "type") {
		override fun featureValueOf(actual: Relationship): String =
			actual.type()
	}
)
