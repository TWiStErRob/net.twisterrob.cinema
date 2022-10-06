package net.twisterrob.test

import org.junit.jupiter.api.Tag

/**
 * Standard Unit tests for a single class or function are untagged.
 * Most/all dependencies are mocked, stubbed or faked out.
 */
@Suppress("unused")
private annotation class Untagged

/**
 * Functional test test multiple classes in tandem.
 * Building a dependency graph, but still mocking/stubbing out partially.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("functional")
annotation class TagFunctional

/**
 * Integration test uses an internal third party to simulate real behavior.
 * For example using a full embedded database.
 *
 * @see TagExternal for an optional tag next to this for signaling external third party
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("integration")
annotation class TagIntegration

/**
 * Integration test uses an external third party to execute real behavior.
 * This means that test status depends on something external to the test.
 * For example hitting a network endpoint.
 *
 * @see TagIntegration should be always added next to this
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("external")
annotation class TagExternal
