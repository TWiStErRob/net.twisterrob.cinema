package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AssertionInfo
import java.util.Locale

fun <A : AbstractAssert<A, T>, T> A.describedAs(info: AssertionInfo, message: String, vararg args: Any?): A =
	this.`as` { message.format(Locale.ROOT, *args.map(info.representation()::toStringOf).toTypedArray()) }
