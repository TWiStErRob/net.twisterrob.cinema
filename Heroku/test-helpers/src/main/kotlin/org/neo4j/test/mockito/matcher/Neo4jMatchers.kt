/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package org.neo4j.test.mockito.matcher

import org.hamcrest.Description
import org.hamcrest.DiagnosingMatcher
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.neo4j.collection.PrimitiveLongResourceIterator
import org.neo4j.graphdb.Entity
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Label
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.schema.ConstraintDefinition
import org.neo4j.graphdb.schema.IndexCreator
import org.neo4j.graphdb.schema.IndexDefinition
import org.neo4j.graphdb.schema.Schema.IndexState
import java.util.concurrent.TimeUnit

object Neo4jMatchers {

	fun <T> inTx(db: GraphDatabaseService, inner: Matcher<T>): Matcher<in T> =
		inTx(db, inner, false)

	fun <T> inTx(
		db: GraphDatabaseService, inner: Matcher<T>,
		successful: Boolean
	): Matcher<in T> =
		object : DiagnosingMatcher<T>() {
			override fun matches(item: Any, mismatchDescription: Description): Boolean {
				db.beginTx().use { tx ->
					if (inner.matches(item)) {
						if (successful) {
							tx.commit()
						} else {
							tx.rollback()
						}
						return true
					}
					inner.describeMismatch(item, mismatchDescription)
					if (successful) {
						tx.commit()
					} else {
						tx.rollback()
					}
					return false
				}
			}

			override fun describeTo(description: Description) {
				inner.describeTo(description)
			}
		}

	fun hasLabel(myLabel: Label?): TypeSafeDiagnosingMatcher<Node> =
		object : TypeSafeDiagnosingMatcher<Node>() {
			override fun describeTo(description: Description) {
				description.appendValue(myLabel)
			}

			override fun matchesSafely(item: Node, mismatchDescription: Description): Boolean {
				val result = item.hasLabel(myLabel)
				if (!result) {
					val labels = asLabelNameSet(item.labels)
					mismatchDescription.appendText(labels.toString())
				}
				return result
			}
		}

	fun hasLabels(vararg expectedLabels: String): TypeSafeDiagnosingMatcher<Node> =
		hasLabels(expectedLabels.toSet())

	fun hasLabels(vararg expectedLabels: Label): TypeSafeDiagnosingMatcher<Node> =
		hasLabels(expectedLabels.map { it.name() }.toSet())

	fun hasNoLabels(): TypeSafeDiagnosingMatcher<Node> =
		hasLabels(emptySet())

	fun hasLabels(expectedLabels: Set<String>): TypeSafeDiagnosingMatcher<Node> =
		object : TypeSafeDiagnosingMatcher<Node>() {
			private var foundLabels: Set<String>? = null
			override fun describeTo(description: Description) {
				description.appendText(expectedLabels.toString())
			}

			override fun matchesSafely(item: Node, mismatchDescription: Description): Boolean {
				foundLabels = asLabelNameSet(item.labels)
				if (foundLabels!!.size == expectedLabels.size && foundLabels!!.containsAll(expectedLabels)) {
					return true
				}
				mismatchDescription.appendText("was " + foundLabels.toString())
				return false
			}
		}

	fun hasNoNodes(withLabel: Label): TypeSafeDiagnosingMatcher<GraphDatabaseService> =
		object : TypeSafeDiagnosingMatcher<GraphDatabaseService>() {
			override fun matchesSafely(db: GraphDatabaseService, mismatchDescription: Description): Boolean {
				val found: Set<Node> = db.beginTx().use { tx -> tx.findNodes(withLabel).asSequence().toSet() }
				if (found.isNotEmpty()) {
					mismatchDescription.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("no nodes with label $withLabel")
			}
		}

	fun hasNodes(withLabel: Label, vararg expectedNodes: Node): TypeSafeDiagnosingMatcher<GraphDatabaseService> =
		object : TypeSafeDiagnosingMatcher<GraphDatabaseService>() {
			override fun matchesSafely(db: GraphDatabaseService, mismatchDescription: Description): Boolean {
				val expected: Set<Node> = expectedNodes.toSet()
				val found: Set<Node> = db.beginTx().use { tx -> tx.findNodes(withLabel).asSequence().toSet() }
				if (expected != found) {
					mismatchDescription.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText(expectedNodes.toSet().toString() + " with label " + withLabel)
			}
		}

	fun asLabelNameSet(enums: Iterable<Label>): Set<String> =
		enums.map { it.name() }.toSet()

	fun hasSamePrimitiveItems(actual: PrimitiveLongResourceIterator): Matcher<in Iterator<Long>> =
		object : TypeSafeDiagnosingMatcher<Iterator<Long>>() {
			var len = 0
			var actualText: String? = null
			var expectedText: String? = null
			override fun matchesSafely(expected: Iterator<Long>, actualDescription: Description): Boolean {
				if (actualText != null) {
					actualDescription.appendText(actualText)
				}
				// compare iterators element-wise
				while (expected.hasNext() && actual.hasNext()) {
					len++
					val expectedNext = expected.next()
					val actualNext: Long = actual.next()
					if (expectedNext != actualNext) {
						actualText = String.format("Element %d at position %d", actualNext, len)
						expectedText = String.format("Element %d at position %d", expectedNext, len)
						return false
					}
				}

				// check that the iterators do not have a different length
				if (expected.hasNext()) {
					actualText = String.format("Length %d", len)
					expectedText = String.format("Length %d", len + 1)
					return false
				}
				if (actual.hasNext()) {
					actualText = String.format("Length %d", len + 1)
					expectedText = String.format("Length %d", len)
					return false
				}
				return true
			}

			override fun describeTo(expectedDescription: Description) {
				if (expectedText != null) {
					expectedDescription.appendText(expectedText)
				}
			}
		}

	fun hasProperty(propertyName: String): Matcher<Entity> =
		PropertyMatcher(propertyName)

	fun findNodesByLabelAndProperty(
		label: Label,
		propertyName: String,
		propertyValue: Any?,
		db: GraphDatabaseService
	): Deferred<Node> =
		object : Deferred<Node>(db) {
			override fun manifest(tx: Transaction): Iterable<Node> =
				tx.findNodes(label, propertyName, propertyValue).asSequence().asIterable()
		}

	fun getIndexes(db: GraphDatabaseService, label: Label): Deferred<IndexDefinition> =
		object : Deferred<IndexDefinition>(db) {
			override fun manifest(tx: Transaction): Iterable<IndexDefinition> =
				tx.schema().getIndexes(label)
		}

	fun getPropertyKeys(db: GraphDatabaseService, propertyContainer: Entity): Deferred<String> =
		object : Deferred<String>(db) {
			override fun manifest(tx: Transaction): Iterable<String> =
				propertyContainer.propertyKeys
		}

	fun getConstraints(db: GraphDatabaseService, label: Label): Deferred<ConstraintDefinition> =
		object : Deferred<ConstraintDefinition>(db) {
			override fun manifest(tx: Transaction): Iterable<ConstraintDefinition> =
				tx.schema().getConstraints(label)
		}

	fun getConstraints(db: GraphDatabaseService, type: RelationshipType): Deferred<ConstraintDefinition> =
		object : Deferred<ConstraintDefinition>(db) {
			override fun manifest(tx: Transaction): Iterable<ConstraintDefinition> =
				tx.schema().getConstraints(type)
		}

	fun getConstraints(db: GraphDatabaseService): Deferred<ConstraintDefinition> =
		object : Deferred<ConstraintDefinition>(db) {
			override fun manifest(tx: Transaction): Iterable<ConstraintDefinition> =
				tx.schema().constraints
		}

	@SafeVarargs
	fun <T> containsOnly(vararg expectedObjects: T): TypeSafeDiagnosingMatcher<Deferred<T>> =
		object : TypeSafeDiagnosingMatcher<Deferred<T>>() {
			override fun matchesSafely(nodes: Deferred<T>, description: Description): Boolean {
				val expected: Set<T> = expectedObjects.toSet()
				val found: Set<T> = nodes.collection().toSet()
				if (expected != found) {
					description.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("exactly " + expectedObjects.toSet())
			}
		}

	fun hasSize(expectedSize: Int): TypeSafeDiagnosingMatcher<Deferred<*>> =
		object : TypeSafeDiagnosingMatcher<Deferred<*>>() {
			override fun matchesSafely(nodes: Deferred<*>, description: Description): Boolean {
				val foundSize = nodes.collection().size
				if (foundSize != expectedSize) {
					description.appendText("found " + nodes.collection().toString())
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("collection of size $expectedSize")
			}
		}

	fun haveState(
		db: GraphDatabaseService, expectedState: IndexState
	): TypeSafeDiagnosingMatcher<Deferred<IndexDefinition>> =
		object : TypeSafeDiagnosingMatcher<Deferred<IndexDefinition>>() {
			override fun matchesSafely(indexes: Deferred<IndexDefinition>, description: Description): Boolean {
				for (current in indexes.collection()) {
					val currentState: IndexState = db.beginTx().use { tx -> tx.schema().getIndexState(current) }
					if (currentState != expectedState) {
						description.appendValue(current).appendText(" has state ").appendValue(currentState)
						return false
					}
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("all indexes have state $expectedState")
			}
		}

	@SafeVarargs
	fun <T> contains(vararg expectedObjects: T): TypeSafeDiagnosingMatcher<Deferred<T>> =
		object : TypeSafeDiagnosingMatcher<Deferred<T>>() {
			override fun matchesSafely(nodes: Deferred<T>, description: Description): Boolean {
				val expected: Set<T> = expectedObjects.toSet()
				val found: Set<T> = nodes.collection().toSet()
				if (!found.containsAll(expected)) {
					description.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("contains " + expectedObjects.toSet())
			}
		}

	val isEmpty: TypeSafeDiagnosingMatcher<Deferred<*>>
		get() = object : TypeSafeDiagnosingMatcher<Deferred<*>>() {
			override fun matchesSafely(deferred: Deferred<*>, description: Description): Boolean {
				val collection = deferred.collection()
				if (!collection.isEmpty()) {
					description.appendText("was $collection")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("empty collection")
			}
		}

	fun createIndex(beansAPI: GraphDatabaseService, label: Label?, vararg properties: String): IndexDefinition {
		val indexDef = createIndexNoWait(beansAPI, label, *properties)
		waitForIndex(beansAPI, indexDef)
		return indexDef
	}

	fun createIndexNoWait(beansAPI: GraphDatabaseService, label: Label?, vararg properties: String): IndexDefinition {
		var indexDef: IndexDefinition
		beansAPI.beginTx().use { tx ->
			var indexCreator: IndexCreator = tx.schema().indexFor(label)
			for (property in properties) {
				indexCreator = indexCreator.on(property)
			}
			indexDef = indexCreator.create()
			tx.commit()
		}
		return indexDef
	}

	fun waitForIndex(beansAPI: GraphDatabaseService, indexDef: IndexDefinition) {
		beansAPI.beginTx().use { tx ->
			tx.schema().awaitIndexOnline(indexDef, 30, TimeUnit.SECONDS)
		}
	}

	fun waitForIndexes(beansAPI: GraphDatabaseService) {
		beansAPI.beginTx().use { tx ->
			tx.schema().awaitIndexesOnline(30, TimeUnit.SECONDS)
		}
	}

	fun getIndexState(beansAPI: GraphDatabaseService, indexDef: IndexDefinition): Any =
		beansAPI.beginTx().use { tx -> tx.schema().getIndexState(indexDef) }

	fun createConstraint(db: GraphDatabaseService, label: Label, propertyKey: String): ConstraintDefinition {
		db.beginTx().use { tx ->
			val constraint: ConstraintDefinition =
				tx.schema().constraintFor(label).assertPropertyIsUnique(propertyKey).create()
			tx.commit()
			return constraint
		}
	}

	fun arrayAsCollection(arrayValue: Any): Collection<Any> {
		assert(arrayValue.javaClass.isArray)
		val length = java.lang.reflect.Array.getLength(arrayValue)
		return (0 until length).map { java.lang.reflect.Array.get(arrayValue, it) }
	}

	private class PropertyValueMatcher(
		private val propertyMatcher: PropertyMatcher,
		private val propertyName: String,
		private val expectedValue: Any
	) : TypeSafeDiagnosingMatcher<Entity>() {

		override fun matchesSafely(propertyContainer: Entity, mismatchDescription: Description): Boolean {
			if (!propertyMatcher.matchesSafely(propertyContainer, mismatchDescription)) {
				return false
			}
			val foundValue: Any = propertyContainer.getProperty(propertyName)
			if (!propertyValuesEqual(expectedValue, foundValue)) {
				mismatchDescription.appendText("found value " + formatValue(foundValue))
				return false
			}
			return true
		}

		override fun describeTo(description: Description) {
			propertyMatcher.describeTo(description)
			description.appendText(String.format("having value %s", formatValue(expectedValue)))
		}

		private fun propertyValuesEqual(expected: Any, readValue: Any): Boolean =
			if (expected.javaClass.isArray) {
				arrayAsCollection(expected) == arrayAsCollection(readValue)
			} else {
				expected == readValue
			}

		private fun formatValue(v: Any): String =
			if (v is String) {
				"'%s'".format(v)
			} else {
				v.toString()
			}
	}

	private class PropertyMatcher constructor(
		private val propertyName: String
	) : TypeSafeDiagnosingMatcher<Entity>() {

		public override fun matchesSafely(
			propertyContainer: Entity,
			mismatchDescription: Description
		): Boolean {
			if (!propertyContainer.hasProperty(propertyName)) {
				mismatchDescription.appendText(
					"found property container with property keys: %s".format(
						propertyContainer.propertyKeys.toSet()
					)
				)
				return false
			}
			return true
		}

		override fun describeTo(description: Description) {
			description.appendText(String.format("property container with property name '%s' ", propertyName))
		}

		fun withValue(value: Any): PropertyValueMatcher =
			PropertyValueMatcher(this, propertyName, value)
	}

	/**
	 * Represents test data that can at assertion time produce a collection
	 *
	 * Useful to defer actually doing operations until context has been prepared (such as a transaction created)
	 *
	 * @param <T> The type of objects the collection will contain
	 */
	abstract class Deferred<T>(
		private val db: GraphDatabaseService
	) {

		protected abstract fun manifest(tx: Transaction): Iterable<T>

		fun collection(): Collection<T> =
			db.beginTx().use { tx -> manifest(tx).toList() }
	}
}
