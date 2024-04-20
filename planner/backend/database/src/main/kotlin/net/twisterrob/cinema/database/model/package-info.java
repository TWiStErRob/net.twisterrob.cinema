/**
 * <p>
 * This rules out using immutable classes:
 * <blockquote>
 *     Property fields to be persisted to the graph must not be declared final.
 *     https://neo4j.com/docs/ogm-manual/current/reference/#reference:annotating-entities:property
 * </blockquote>
 * <p>
 * Property conversion is done after instantiation so {@link org.neo4j.ogm.annotation.typeconversion.Convert}
 * can only be used on class body {@code vars}, not on primary constructor ones.
 * {@link net.twisterrob.neo4j.ogm.KotlinReflectionEntityInstantiator} will fail with type mismatch otherwise.
 */
package net.twisterrob.cinema.database.model;
