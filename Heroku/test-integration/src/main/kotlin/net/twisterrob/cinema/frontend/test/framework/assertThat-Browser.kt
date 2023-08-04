package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractUriAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.CheckReturnValue
import java.net.URI

@Suppress("NOTHING_TO_INLINE")
@CheckReturnValue
inline fun assertThat(element: Browser): BrowserAssert =
	BrowserAssert(element)

class BrowserAssert(
	element: Browser
) : AbstractAssert<BrowserAssert, Browser>(element, BrowserAssert::class.java) {

	@CheckReturnValue
	fun url(): AbstractUriAssert<*> =
		assertThat(URI.create(actual.currentUrl))
}
