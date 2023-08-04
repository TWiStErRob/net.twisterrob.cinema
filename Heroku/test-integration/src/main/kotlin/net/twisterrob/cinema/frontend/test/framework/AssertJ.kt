package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assert
import org.assertj.core.util.CheckReturnValue
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KProperty1

@CheckReturnValue
fun <A : Assert<*, *>> A.verify(block: A.() -> Unit): A {
	this.isNotNull
	block(this)
	return this
}

/**
 * Has to return [Unit] instead of [Nothing] for `failWithMessage`, because
 * `failWithMessage` is defined in Java, so there's no Nothing type, and :: doesn't auto-convert it.
 */
private typealias ActuallyNothing = Unit

@CheckReturnValue
fun <A : AbstractAssert<out Assert<A, T>, T>, T> A.check(
	propertyName: String,
	actual: T,
	property: KProperty1<T, Boolean>,
	failWithMessage: (String) -> ActuallyNothing,
): A = verify {
	if (!property(actual)) {
		val actualAsString = info.representation().toStringOf(actual)
		failWithMessage("Expected ${actualAsString} to be ${propertyName}. But was not!")
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : AbstractAssert<out Assert<A, T>, T>, T> A.checkNot(
	propertyName: String,
	actual: T,
	property: KProperty1<T, Boolean>,
	failWithMessage: (String) -> ActuallyNothing,
): A = verify {
	if (property(actual)) {
		val actualAsString = info.representation().toStringOf(actual)
		failWithMessage("Expected ${actualAsString} to not be ${propertyName}. But was!")
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : AbstractAssert<out Assert<A, T>, T>, T> A.check(
	propertyName: String,
	actual: T,
	property: KFunction1<T, Boolean>,
	failWithMessage: (String) -> ActuallyNothing,
): A = verify {
	if (!property(actual)) {
		val actualAsString = info.representation().toStringOf(actual)
		failWithMessage("Expected ${actualAsString} to be ${propertyName}. But was not!")
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : AbstractAssert<out Assert<A, T>, T>, T> A.checkNot(
	propertyName: String,
	actual: T,
	property: KFunction1<T, Boolean>,
	failWithMessage: (String) -> ActuallyNothing,
): A = verify {
	if (property(actual)) {
		val actualAsString = info.representation().toStringOf(actual)
		failWithMessage("Expected ${actualAsString} to not be ${propertyName}. But was!")
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : AbstractAssert<out Assert<A, T>, T>, T, P> A.checkHas(
	propertyName: String,
	actual: T,
	property: KFunction2<T, P, Boolean>,
	arg: P,
	failWithMessage: (String) -> ActuallyNothing,
): A = verify {
	if (!property(actual, arg)) {
		val actualAsString = info.representation().toStringOf(actual)
		val argAsString = info.representation().toStringOf(arg)
		failWithMessage("Expected ${actualAsString} to have ${propertyName} ${argAsString}. But did not have!")
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : AbstractAssert<out Assert<A, T>, T>, T, P> A.checkDoesNotHave(
	propertyName: String,
	actual: T,
	property: KFunction2<T, P, Boolean>,
	arg: P,
	failWithMessage: (String) -> ActuallyNothing,
): A = verify {
	if (property(actual, arg)) {
		val actualAsString = info.representation().toStringOf(actual)
		val argAsString = info.representation().toStringOf(arg)
		failWithMessage("Expected ${actualAsString} to not have ${propertyName} ${argAsString}. But had!")
		failWithMessageReturnsNothing()
	}
}

private fun failWithMessageReturnsNothing(): Nothing {
	error("failWithMessage should have thrown")
}
