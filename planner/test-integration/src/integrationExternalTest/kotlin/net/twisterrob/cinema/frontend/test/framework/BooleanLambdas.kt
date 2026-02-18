@file:Suppress(
	"FunctionMinLength", // They're meant to be short and/or/not are traditional names.
)

package net.twisterrob.cinema.frontend.test.framework

import kotlin.reflect.KFunction1

/**
 * Kotlin version of [java.util.function.Predicate.negate] for functional types.
 *
 * Example:
 * ```
 * val isX: X -> Boolean = ...
 * val isNotX = !isX
 * val isNotX = isX.not()
 * ```
 */
operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean =
	{ !this(it) }

/**
 * Kotlin version of [java.util.function.Predicate.negate] for method references.
 *
 * Example:
 * ```
 * fun isX(x: X): Boolean = ...
 * val isNotX = !::isX
 * ```
 */
operator fun <T> KFunction1<T, Boolean>.not(): KFunction1<T, Boolean> =
	{ it: T -> !this(it) }::invoke

/**
 * Kotlin version of [java.util.function.Predicate.or] for functional types.
 */
infix fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean =
	{ this(it) || other(it) }

/**
 * Kotlin version of [java.util.function.Predicate.or] for method references.
 */
infix fun <T> KFunction1<T, Boolean>.or(other: KFunction1<T, Boolean>): KFunction1<T, Boolean> =
	{ it: T -> this(it) || other(it) }::invoke

/**
 * Kotlin version of [java.util.function.Predicate.and] for functional types.
 */
infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean =
	{ this(it) && other(it) }

/**
 * Kotlin version of [java.util.function.Predicate.and] for method references.
 */
infix fun <T> KFunction1<T, Boolean>.and(other: KFunction1<T, Boolean>): KFunction1<T, Boolean> =
	{ it: T -> this(it) && other(it) }::invoke
