@file:Suppress("MatchingDeclarationName")
package net.twisterrob.test

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.UnrecoverableExceptions
import org.opentest4j.MultipleFailuresError
import kotlin.DeprecationLevel.ERROR

/**
 * Kotlin-friendly version of [org.junit.jupiter.api.AssertAll.assertAll].
 * Kotlin-friendly in a sense that it totally eliminates from the compiled binary.
 * The Jupiter version results in an inner class with captured fields and two methods each.
 * The `vararg` triggers this behavior.
 *
 * Syntax changes from
 * ```kotlin
 * assertAll (
 *     { assert1() },
 *     { assert2() }
 * )
 * ```
 * to
 * ```kotlin
 * assertAll {
 *     o { assert1() }
 *     o { assert2() }
 * }
 * ```
 * Note the `{}` placement around `assertAll` and the `o` listing of lambdas instead of `,` separated enumeration.
 */
@Suppress("DEPRECATION_ERROR") // Deprecated members are meant to be used internally by this class only.
// Cannot use an interface to hide implementation details if we want to allow inlining [o] function.
class AllAsserter
@Deprecated("Internal constructor, only visible because of inlining, do not use!", level = ERROR)
constructor(
	@get:Deprecated("Internal property, only visible because of inlining, do not use!", level = ERROR)
	val heading: String?
) {

	@Deprecated("Internal property, only visible because of inlining, do not use!", level = ERROR)
	val failures: MutableList<Throwable> = mutableListOf()

	@Suppress("FunctionMinLength")
	inline fun o(crossinline function: () -> Unit) {
		try {
			function.invoke()
		} catch (t: Throwable) {
			UnrecoverableExceptions.rethrowIfUnrecoverable(t)

			failures.add(t)
		}
	}

	@Deprecated(
		"Internal function, only visible because of inlining, do not use!",
		level = ERROR,
		replaceWith = ReplaceWith("assertAll { this }")
	)
	fun verify() {
		if (failures.isNotEmpty()) {
			throw MultipleFailuresError(heading, failures)
				.apply { failures.forEach(::addSuppressed) }
		}
	}
}

/**
 * @see AllAsserter for documentation
 */
inline fun assertAll(crossinline block: AllAsserter.() -> Unit) {
	@Suppress("DEPRECATION_ERROR")
	AllAsserter(null).apply(block).verify()
}

/**
 * @see AllAsserter for documentation
 */
inline fun assertAll(heading: String? = null, crossinline block: AllAsserter.() -> Unit) {
	@Suppress("DEPRECATION_ERROR")
	AllAsserter(heading).apply(block).verify()
}

/**
 * Extension to support safe case of [MatcherAssert.assertThat] calls.
 * Note that if the [reason]/[actual]/[matcher] expressions errors, that won't be part of the [assertAll].
 * However, most of the time it's the [matcher]'s evaluation that fails the test, so this shorthand is still useful.
 */
fun <T> AllAsserter.that(reason: String, actual: T, matcher: Matcher<in T>) {
	o { MatcherAssert.assertThat(reason, actual, matcher) }
}

@Deprecated(
	message = "Always provide a reason for failure, otherwise it's hard to trace which of assertAll has failed.",
	replaceWith = ReplaceWith("that(\"\${TODO()}\", actual, matcher)")
)
fun <T> AllAsserter.that(actual: T, matcher: Matcher<in T>) {
	o { MatcherAssert.assertThat(actual, matcher) }
}

inline fun <reified T : Any> ExtensionContext.Store.put(value: T?) {
	this.put(T::class, value)
}

inline fun <reified T : Any> ExtensionContext.Store.get(): T? =
	this.get(T::class, T::class.java)

inline fun <reified T : Any> ExtensionContext.Store.remove() {
	this.remove(T::class)
}
