/*
 * Copyright (c) 2002-2020 "Neo4j,"
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
@file:Suppress("UseExpressionBody", "MemberVisibilityCanBePrivate", "unused", "NAME_SHADOWING")

package org.neo4j.test.mockito.matcher

import org.hamcrest.Description
import org.hamcrest.DiagnosingMatcher
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.neo4j.graphdb.Entity
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Label
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.schema.ConstraintDefinition
import org.neo4j.graphdb.schema.IndexDefinition
import org.neo4j.graphdb.schema.Schema.IndexState
import org.neo4j.internal.helpers.collection.Iterables
import org.neo4j.internal.helpers.collection.Iterators
import java.lang.reflect.Array
import java.util.concurrent.TimeUnit

object Neo4jMatchers {

	fun <T> inTx(db: GraphDatabaseService, inner: Matcher<T>): Matcher<in T> {
		return inTx(db, inner, false)
	}

	fun <T> inTx(
		db: GraphDatabaseService, inner: Matcher<T>,
		successful: Boolean
	): Matcher<in T> {
		return object : DiagnosingMatcher<T>() {
			override fun matches(item: Any, mismatchDescription: Description): Boolean {
				db.beginTx().use { tx ->
					if (inner is TransactionalMatcher) {
						(inner as TransactionalMatcher).setTransaction(tx)
					}
					if (inner.matches(item)) {
						if (successful) {
							tx.commit()
						}
						return true
					}
					inner.describeMismatch(item, mismatchDescription)
					if (successful) {
						tx.commit()
					}
					return false
				}
			}

			override fun describeTo(description: Description) {
				inner.describeTo(description)
			}
		}
	}

	fun hasLabel(myLabel: Label): TypeSafeDiagnosingMatcher<Node> {
		return LabelMatcher(myLabel)
	}

	fun hasLabels(vararg expectedLabels: String): TypeSafeDiagnosingMatcher<Node> {
		return hasLabels(Iterators.asSet(*expectedLabels))
	}

	fun hasLabels(vararg expectedLabels: Label): TypeSafeDiagnosingMatcher<Node> {
		val labelNames: MutableSet<String> = HashSet(expectedLabels.size)
		expectedLabels.mapTo(labelNames) { it.name() }
		return hasLabels(labelNames)
	}

	fun hasNoLabels(): TypeSafeDiagnosingMatcher<Node> {
		return hasLabels(emptySet<String>())
	}

	fun hasLabels(expectedLabels: Set<String>): TypeSafeDiagnosingMatcher<Node> {
		return LabelsMatcher(expectedLabels)
	}

	fun hasNoNodes(withLabel: Label): TypeSafeDiagnosingMatcher<Transaction> {
		return object : TypeSafeDiagnosingMatcher<Transaction>() {
			override fun matchesSafely(tx: Transaction, mismatchDescription: Description): Boolean {
				val found = Iterators.asSet(tx.findNodes(withLabel))
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
	}

	fun hasNodes(withLabel: Label, vararg expectedNodes: Node): TypeSafeDiagnosingMatcher<Transaction> {
		return object : TypeSafeDiagnosingMatcher<Transaction>() {
			override fun matchesSafely(tx: Transaction, mismatchDescription: Description): Boolean {
				val expected = Iterators.asSet<Node>(*expectedNodes)
				val found = Iterators.asSet(tx.findNodes(withLabel))
				if (expected != found) {
					mismatchDescription.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText(Iterators.asSet<Node>(*expectedNodes).toString() + " with label " + withLabel)
			}
		}
	}

	fun asLabelNameSet(enums: Iterable<Label>): Set<String> {
		return Iterables.asSet(
			Iterables.map(
				{ obj: Label -> obj.name() }, enums
			)
		)
	}

	fun hasProperty(propertyName: String): Matcher<Entity> {
		return PropertyMatcher(propertyName)
	}

	fun findNodesByLabelAndProperty(
		label: Label, propertyName: String, propertyValue: Any?,
		@Suppress("UNUSED_PARAMETER") db: GraphDatabaseService, transaction: Transaction
	): Deferred<Node> {
		return object : Deferred<Node>() {
			override fun manifest(): Iterable<Node>? {
				return Iterators.loop(transaction.findNodes(label, propertyName, propertyValue))
			}
		}
	}

	fun getIndexes(transaction: Transaction, label: Label): Deferred<IndexDefinition> {
		return object : Deferred<IndexDefinition>() {
			override fun manifest(): Iterable<IndexDefinition>? {
				return transaction.schema().getIndexes(label)
			}
		}
	}

	fun getPropertyKeys(
		@Suppress("UNUSED_PARAMETER") transaction: Transaction,
		entity: Entity
	): Deferred<String> {
		return object : Deferred<String>() {
			override fun manifest(): Iterable<String>? {
				return entity.propertyKeys
			}
		}
	}

	fun getConstraints(transaction: Transaction, label: Label): Deferred<ConstraintDefinition> {
		return object : Deferred<ConstraintDefinition>() {
			override fun manifest(): Iterable<ConstraintDefinition>? {
				return transaction.schema().getConstraints(label)
			}
		}
	}

	fun getConstraints(
		transaction: Transaction,
		type: RelationshipType
	): Deferred<ConstraintDefinition> {
		return object : Deferred<ConstraintDefinition>() {
			override fun manifest(): Iterable<ConstraintDefinition>? {
				return transaction.schema().getConstraints(type)
			}
		}
	}

	fun getConstraints(transaction: Transaction): Deferred<ConstraintDefinition> {
		return object : Deferred<ConstraintDefinition>() {
			override fun manifest(): Iterable<ConstraintDefinition>? {
				return transaction.schema().constraints
			}
		}
	}

	@SafeVarargs fun <T> containsOnly(vararg expectedObjects: T): TypeSafeDiagnosingMatcher<Deferred<T>> {
		return object : TypeSafeDiagnosingMatcher<Deferred<T>>() {
			override fun matchesSafely(nodes: Deferred<T>, description: Description): Boolean {
				val expected = Iterators.asSet(*expectedObjects)
				val found = Iterables.asSet(nodes.collection())
				if (expected != found) {
					description.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("exactly " + Iterators.asSet(*expectedObjects))
			}
		}
	}

	fun hasSize(expectedSize: Int): TypeSafeDiagnosingMatcher<Deferred<*>> {
		return object : TypeSafeDiagnosingMatcher<Deferred<*>>() {
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
	}

	fun haveState(
		transaction: Transaction, expectedState: IndexState
	): TypeSafeDiagnosingMatcher<Deferred<IndexDefinition>> {
		return object : TypeSafeDiagnosingMatcher<Deferred<IndexDefinition>>() {
			override fun matchesSafely(indexes: Deferred<IndexDefinition>, description: Description): Boolean {
				for (current in indexes.collection()) {
					val currentState = transaction.schema().getIndexState(current)
					if (currentState != expectedState) {
						description.appendValue(current).appendText(" has state ").appendValue(currentState)
						if (currentState == IndexState.FAILED) {
							val indexFailure = transaction.schema().getIndexFailure(current)
							description.appendText(" has failure message: ").appendText(indexFailure)
						}
						return false
					}
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("all indexes have state $expectedState")
			}
		}
	}

	@SafeVarargs fun <T> contains(vararg expectedObjects: T): TypeSafeDiagnosingMatcher<Deferred<T>> {
		return object : TypeSafeDiagnosingMatcher<Deferred<T>>() {
			override fun matchesSafely(nodes: Deferred<T>, description: Description): Boolean {
				val expected = Iterators.asSet(*expectedObjects)
				val found = Iterables.asSet(nodes.collection())
				if (!found.containsAll(expected)) {
					description.appendText("found $found")
					return false
				}
				return true
			}

			override fun describeTo(description: Description) {
				description.appendText("contains " + Iterators.asSet(*expectedObjects))
			}
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

	fun createIndex(beansAPI: GraphDatabaseService, label: Label, vararg properties: String): IndexDefinition {
		return createIndex(beansAPI, null, label, *properties)
	}

	fun createIndex(
		beansAPI: GraphDatabaseService,
		name: String?,
		label: Label,
		vararg properties: String
	): IndexDefinition {
		val indexDef = createIndexNoWait(beansAPI, name, label, *properties)
		waitForIndex(beansAPI, indexDef)
		return indexDef
	}

	fun createIndexNoWait(beansAPI: GraphDatabaseService, label: Label, vararg properties: String): IndexDefinition {
		return createIndexNoWait(beansAPI, null, label, *properties)
	}

	fun createIndexNoWait(
		db: GraphDatabaseService,
		name: String?,
		label: Label,
		vararg properties: String
	): IndexDefinition {
		lateinit var indexDef: IndexDefinition
		db.beginTx().use { tx ->
			var indexCreator = tx.schema().indexFor(label)
			for (property in properties) {
				indexCreator = indexCreator.on(property)
			}
			if (name != null) {
				indexCreator = indexCreator.withName(name)
			}
			indexDef = indexCreator.create()
			tx.commit()
		}
		return indexDef
	}

	fun waitForIndex(beansAPI: GraphDatabaseService, indexDef: IndexDefinition) {
		beansAPI.beginTx().use { tx -> tx.schema().awaitIndexOnline(indexDef, 30, TimeUnit.SECONDS) }
	}

	fun waitForIndexes(beansAPI: GraphDatabaseService) {
		beansAPI.beginTx().use { tx -> tx.schema().awaitIndexesOnline(30, TimeUnit.SECONDS) }
	}

	fun getIndexState(tx: Transaction, indexDef: IndexDefinition?): Any {
		return tx.schema().getIndexState(indexDef)
	}

	fun createConstraint(db: GraphDatabaseService, label: Label?, propertyKey: String?): ConstraintDefinition {
		db.beginTx().use { tx ->
			val constraint = tx.schema().constraintFor(label).assertPropertyIsUnique(propertyKey).create()
			tx.commit()
			return constraint
		}
	}

	fun arrayAsCollection(arrayValue: Any): Collection<Any> {
		assert(arrayValue.javaClass.isArray)
		val result: MutableCollection<Any> = ArrayList()
		val length = Array.getLength(arrayValue)
		(0 until length).mapTo(result) { Array.get(arrayValue, it) }
		return result
	}

	private class PropertyValueMatcher(
		private val propertyMatcher: PropertyMatcher,
		private val propertyName: String,
		private val expectedValue: Any
	) : TypeSafeDiagnosingMatcher<Entity>(), TransactionalMatcher {

		private var transaction: Transaction? = null
		override fun matchesSafely(entity: Entity, mismatchDescription: Description): Boolean {
			var entity = entity
			if (!propertyMatcher.matchesSafely(entity, mismatchDescription)) {
				return false
			}
			if (transaction != null) {
				entity = if (entity is Node) {
					transaction!!.getNodeById(entity.getId())
				} else {
					transaction!!.getRelationshipById(entity.id)
				}
			}
			val foundValue = entity.getProperty(propertyName)
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

		private fun propertyValuesEqual(expected: Any, readValue: Any): Boolean {
			return if (expected.javaClass.isArray) {
				arrayAsCollection(expected) == arrayAsCollection(readValue)
			} else expected == readValue
		}

		private fun formatValue(v: Any): String {
			return if (v is String) {
				String.format("'%s'", v.toString())
			} else v.toString()
		}

		override fun setTransaction(transaction: Transaction) {
			this.transaction = transaction
			propertyMatcher.setTransaction(transaction)
		}
	}

	private class PropertyMatcher constructor(
		val propertyName: String
	) : TypeSafeDiagnosingMatcher<Entity>(), TransactionalMatcher {

		private var transaction: Transaction? = null
		public override fun matchesSafely(entity: Entity, mismatchDescription: Description): Boolean {
			var entity = entity
			if (transaction != null) {
				entity = if (entity is Node) {
					transaction!!.getNodeById(entity.getId())
				} else {
					transaction!!.getRelationshipById(entity.id)
				}
			}
			if (!entity.hasProperty(propertyName)) {
				mismatchDescription.appendText(
					String.format(
						"found entity with property keys: %s",
						Iterables.asSet(entity.propertyKeys)
					)
				)
				return false
			}
			return true
		}

		override fun describeTo(description: Description) {
			description.appendText(String.format("entity with property name '%s' ", propertyName))
		}

		fun withValue(value: Any): PropertyValueMatcher {
			return PropertyValueMatcher(this, propertyName, value)
		}

		override fun setTransaction(transaction: Transaction) {
			this.transaction = transaction
		}
	}

	/**
	 * Represents test data that can at assertion time produce a collection
	 *
	 * Useful to defer actually doing operations until context has been prepared (such as a transaction created)
	 *
	 * @param <T> The type of objects the collection will contain
	 */
	abstract class Deferred<T> {

		protected abstract fun manifest(): Iterable<T>?
		fun collection(): Collection<T> {
			return Iterables.asCollection(manifest())
		}
	}

	@FunctionalInterface
	private interface TransactionalMatcher {

		fun setTransaction(transaction: Transaction)
	}

	private class LabelMatcher constructor(
		private val myLabel: Label
	) : TypeSafeDiagnosingMatcher<Node>(), TransactionalMatcher {

		private var transaction: Transaction? = null
		override fun describeTo(description: Description) {
			description.appendValue(myLabel)
		}

		override fun matchesSafely(item: Node, mismatchDescription: Description): Boolean {
			val node = transaction!!.getNodeById(item.id)
			val result = node.hasLabel(myLabel)
			if (!result) {
				val labels = asLabelNameSet(node.labels)
				mismatchDescription.appendText(labels.toString())
			}
			return result
		}

		override fun setTransaction(transaction: Transaction) {
			this.transaction = transaction
		}
	}

	private class LabelsMatcher constructor(
		private val expectedLabels: Set<String>
	) : TypeSafeDiagnosingMatcher<Node>(), TransactionalMatcher {

		private var transaction: Transaction? = null
		override fun describeTo(description: Description) {
			description.appendText(expectedLabels.toString())
		}

		override fun matchesSafely(item: Node, mismatchDescription: Description): Boolean {
			val node = transaction!!.getNodeById(item.id)
			val foundLabels = asLabelNameSet(node.labels)
			if (foundLabels.size == expectedLabels.size && foundLabels.containsAll(expectedLabels)) {
				return true
			}
			mismatchDescription.appendText("was $foundLabels")
			return false
		}

		override fun setTransaction(transaction: Transaction) {
			this.transaction = transaction
		}
	}
}
