package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AssertionInfo

fun <A : AbstractAssert<A, T>, T> A.describedAs(info: AssertionInfo, message: String, vararg args: Any?): A =
	this.`as` { message.format(*args.map(info.representation()::toStringOf).toTypedArray()) }
