@file:Suppress(
	"UnusedImport", // TODEL // https://youtrack.jetbrains.com/issue/KT-60939
	"UnusedImports", // TODEL https://github.com/detekt/detekt/issues/6363
)

package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractIterableAssert
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.AssertFactory
import org.assertj.core.api.BooleanAssert
import org.assertj.core.api.ListAssert
import org.assertj.core.api.StringAssert
import org.assertj.core.util.CheckReturnValue
import org.checkerframework.checker.units.qual.A
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@Suppress("NOTHING_TO_INLINE")
@CheckReturnValue
inline fun assertThat(element: WebElement): WebElementAssert =
	WebElementAssert(element)

fun <A : AbstractIterableAssert<*, *, WebElement, *>> A.anyMeet(assertions: WebElementAssert.() -> Unit) {
	this.anySatisfy { assertThat(it).assertions() }
}

fun <A : AbstractIterableAssert<*, *, WebElement, *>> A.allMeet(assertions: WebElementAssert.() -> Unit) {
	this.allSatisfy { assertThat(it).assertions() }
}

fun <A : AbstractIterableAssert<*, *, WebElement, *>> A.noneMeet(assertions: WebElementAssert.() -> Unit) {
	this.noneSatisfy { assertThat(it).assertions() }
}

@Suppress("TooManyFunctions") // This is how AssertJ works.
class WebElementAssert(
	element: WebElement
) : AbstractObjectAssert<WebElementAssert, WebElement>(element, WebElementAssert::class.java) {

	@CheckReturnValue
	fun displayed(): BooleanAssert = extracting(WebElement::isDisplayed, ::BooleanAssert).asProp("isDisplayed")
	fun isDisplayed(): WebElementAssert = apply { displayed().isTrue() }
	fun isNotDisplayed(): WebElementAssert = apply { displayed().isFalse() }

	@CheckReturnValue
	fun selected(): BooleanAssert = extracting(WebElement::isSelected, ::BooleanAssert).asProp("isSelected")
	fun isSelected(): WebElementAssert = apply { selected().isTrue() }
	fun isNotSelected(): WebElementAssert = apply { selected().isFalse() }

	@CheckReturnValue
	fun enabled(): BooleanAssert = extracting(WebElement::isEnabled, ::BooleanAssert).asProp("isEnabled")
	fun isEnabled(): WebElementAssert = apply { enabled().isTrue() }
	fun isNotEnabled(): WebElementAssert = apply { enabled().isFalse() }

	@CheckReturnValue
	fun checked(): BooleanAssert = extracting(WebElement::isChecked, ::BooleanAssert).asProp("isChecked")
	fun isChecked(): WebElementAssert = apply { checked().isTrue() }
	fun isNotChecked(): WebElementAssert = apply { checked().isFalse() }

	@CheckReturnValue
	fun icon(): StringAssert = extracting(WebElement::glyphicon, ::StringAssert).asProp("icon")
	fun hasIcon(name: String): WebElementAssert = apply { icon().isEqualTo(name) }

	@CheckReturnValue
	fun text(): AbstractStringAssert<*> = extracting(WebElement::textContent, ::StringAssert).asProp("text")
	fun hasText(text: String): WebElementAssert = apply { text().isEqualTo(text) }

	@CheckReturnValue
	fun classes(): ListAssert<String> = extracting(WebElement::classes, ::ListAssert).asProp("classes")
	fun hasClass(name: String): WebElementAssert = apply { classes().contains(name) }

	@CheckReturnValue
	fun descendant(locator: By): WebElementAssert =
		@Suppress("UNCHECKED_CAST")
		extracting({ it.findElement(locator) }, ::WebElementAssert as AssertFactory<WebElement, WebElementAssert>)

	@CheckReturnValue // TODO WebElementListAssert? if actually used.
	fun descendants(locator: By): ListAssert<WebElement> =
		extracting({ it.findElements(locator) }, ::ListAssert)

	private fun <A : AbstractAssert<A, *>> A.asProp(name: String): A = apply {
		describedAs(info, "%s.${name}", this@WebElementAssert.actual)
	}
}
