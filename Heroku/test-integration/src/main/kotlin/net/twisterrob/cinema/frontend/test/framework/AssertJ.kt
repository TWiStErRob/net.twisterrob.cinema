package net.twisterrob.cinema.frontend.test.framework

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
 * Has to use `(String, Array<out Any?>) -> Unit` instead of `(String, vararg Any?) -> Nothing` for `failWithMessage`.
 *  * vararg is not possible in function type
 *  * failWithMessage is defined in Java, so there's no Nothing type, and :: doesn't auto-convert it.
 */
private typealias ActuallyNothing = Unit

@CheckReturnValue
fun <A : Assert<out Assert<A, T>, T>, T> A.check(
	propertyName: String,
	actual: T,
	property: KProperty1<T, Boolean>,
	failWithMessage: (String, Array<out Any?>) -> ActuallyNothing,
): A = verify {
	if (!property(actual)) {
		failWithMessage("Expected element to be ${propertyName}. But was not!", emptyArray())
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : Assert<out Assert<A, T>, T>, T> A.checkNot(
	propertyName: String,
	actual: T,
	property: KProperty1<T, Boolean>,
	failWithMessage: (String, Array<out Any?>) -> ActuallyNothing,
): A = verify {
	if (property(actual)) {
		failWithMessage("Expected element to not be ${propertyName}. But was!", emptyArray())
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : Assert<out Assert<A, T>, T>, T> A.check(
	propertyName: String,
	actual: T,
	property: KFunction1<T, Boolean>,
	failWithMessage: (String, Array<out Any?>) -> ActuallyNothing,
): A = verify {
	if (!property(actual)) {
		failWithMessage("Expected element to be ${propertyName}. But was not!", emptyArray())
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : Assert<out Assert<A, T>, T>, T> A.checkNot(
	propertyName: String,
	actual: T,
	property: KFunction1<T, Boolean>,
	failWithMessage: (String, Array<out Any?>) -> ActuallyNothing,
): A = verify {
	if (property(actual)) {
		failWithMessage("Expected element to not be ${propertyName}. But was!", emptyArray())
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : Assert<out Assert<A, T>, T>, T, P> A.checkHas(
	propertyName: String,
	actual: T,
	property: KFunction2<T, P, Boolean>,
	arg: P,
	failWithMessage: (String, Array<out Any?>) -> ActuallyNothing,
): A = verify {
	if (!property(actual, arg)) {
		failWithMessage("Expected element to have ${propertyName} ${arg ?: "null"}. But did not have!", emptyArray())
		failWithMessageReturnsNothing()
	}
}

@CheckReturnValue
fun <A : Assert<out Assert<A, T>, T>, T, P> A.checkDoesNotHave(
	propertyName: String,
	actual: T,
	property: KFunction2<T, P, Boolean>,
	arg: P,
	failWithMessage: (String, Array<out Any?>) -> ActuallyNothing,
): A = verify {
	if (property(actual, arg)) {
		failWithMessage("Expected element to not have ${propertyName} ${arg ?: "null"}. But had!", emptyArray())
		failWithMessageReturnsNothing()
	}
}

private fun failWithMessageReturnsNothing(): Nothing {
	error("failWithMessage should have thrown")
}
