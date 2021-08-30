@file:Suppress("unused")

package net.twisterrob.neo4j.ogm

import org.neo4j.ogm.cypher.Filter
import org.neo4j.ogm.session.Session

/**
 * Type-safe Kotlin extension functions with inferred nullability based on docs and code.
 * All [Class] parameters are eliminated with `inline <reified T>`.
 * @see org.neo4j.ogm.session.Session
 * @see org.neo4j.ogm.session.SessionExtensionsKt
 * @see org.neo4j.ogm.session.loadAll
 */
private val DOCUMENTATION = Unit

/**
 * Delete all entities of type matching filter.
 * By default it behaves as [Session.deleteAll], except it returns data.
 *
 * @param T type of the entities to delete
 * @param filters filters to match entities to delete
 * @return count of deleted entities
 * @see Session.delete
 * @see Session.deleteAll
 */
inline fun <reified T> Session.deleteForCount(filters: Iterable<Filter> = emptyList()): Long =
	delete(T::class.java, filters, false) as Long

/**
 * Delete all entities of type matching filter.
 * By default it behaves as [Session.deleteAll], except it returns data.
 *
 * @param T type of the entities to delete
 * @param filters filters to match entities to delete
 * @return ids of deleted entities
 * @see Session.delete
 * @see Session.deleteAll
 */
@Suppress("UNCHECKED_CAST") // necessary because of List's generics
inline fun <reified T> Session.deleteForIds(filters: Iterable<Filter> = emptyList()): List<Long> =
	delete(T::class.java, filters, true) as List<Long>

/**
 * A cypher statement this method will return a collection of domain objects that is hydrated to
 * the default level or a collection of scalars (depending on the parametrized type).
 *
 * @param T The type that should be returned from the query.
 * @param cypher The parameterizable cypher to execute.
 * @param parameters Any parameters to attach to the cypher.
 * @param T A domain object or scalar.
 * @return A collection of domain objects or scalars as prescribed by the parametrized type.
 * @see Session.query
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T> Session.query(
	cypher: String,
	parameters: Map<String, *> = emptyMap<String, Any>()
): Iterable<T> =
	query(T::class.java, cypher, parameters)
