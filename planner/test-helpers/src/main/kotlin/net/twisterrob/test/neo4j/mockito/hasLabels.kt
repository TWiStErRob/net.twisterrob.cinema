@file:JvmName("Neo4jMatchers")
@file:JvmMultifileClass

package net.twisterrob.test.neo4j.mockito

import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.neo4j.driver.types.Node

fun hasLabels(vararg expectedLabels: String): Matcher<Node> =
	hasLabels(Matchers.containsInAnyOrder(*expectedLabels))

fun hasLabels(labelsMatcher: Matcher<in Iterable<String>>): Matcher<Node> =
	object : FeatureMatcher<Node, List<String>>(labelsMatcher, "Node labels", "labels") {
		override fun featureValueOf(actual: Node): List<String> =
			actual.labels().toList()
	}
