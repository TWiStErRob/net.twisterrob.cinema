package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.AbstractUriAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.assertj.core.util.CheckReturnValue
import org.openqa.selenium.WebElement
import java.net.URI
import kotlin.reflect.KFunction1

@Suppress("NOTHING_TO_INLINE")
@CheckReturnValue
inline fun assertThat(element: WebElement): WebElementAssert =
	WebElementAssert(element)

class WebElementAssert(
	element: WebElement
) : AbstractAssert<WebElementAssert, WebElement>(element, WebElementAssert::class.java) {

	fun isSelected(): WebElementAssert = check(WebElement::isSelected, "selected")
	fun isNotSelected(): WebElementAssert = checkNot(WebElement::isSelected, "selected")
	fun isDisplayed(): WebElementAssert = check(WebElement::isDisplayed, "displayed")
	fun isNotDisplayed(): WebElementAssert = checkNot(WebElement::isDisplayed, "displayed")
	fun isEnabled(): WebElementAssert = check(WebElement::isEnabled, "enabled")
	fun isNotEnabled(): WebElementAssert = checkNot(WebElement::isEnabled, "enabled")

	fun isButton(): WebElementAssert = apply {
		val isButton = actual.tagName.equals("button", ignoreCase = true)
		val isInputButton = actual.getAttribute("type").equals("button", ignoreCase = true)
		if (!(isButton || isInputButton)) {
			failWithMessage("Expected element to be a button. But was not!")
		}
	}

	fun isLink(): WebElementAssert = apply {
		if (!actual.tagName.equals("a", ignoreCase = true)) {
			failWithMessage("Expected element to be a link. But was not!")
		}
	}

	fun hasAttributeValue(attr: String, value: String): WebElementAssert = apply {
		if (actual.getAttribute(attr) != value) {
			failWithMessage("Expected element to have attr <%s> value as <%s>. But was not!", attr, value)
		}
	}

	fun hasIcon(iconName: String): WebElementAssert = apply {
		if (!actual.hasIcon(iconName)) {
			failWithMessage("Expected element to have icon <%s>.", iconName)
		}
	}

	@CheckReturnValue
	fun text(): AbstractStringAssert<*> {
		val text = when (actual.tagName) {
			"textarea" -> actual.getAttribute("value")
			else -> actual.text
		}
		return assertThat(text)
	}

	@CheckReturnValue
	fun classes(): ListAssert<String> =
		assertThat(actual.classes)

	@CheckReturnValue
	private fun check(property: KFunction1<WebElement, Boolean>, propertyName: String): WebElementAssert = apply {
		if (!property(actual)) {
			failWithMessage("Expected element to be $propertyName. But was not!")
		}
	}

	@CheckReturnValue
	private fun checkNot(property: KFunction1<WebElement, Boolean>, propertyName: String): WebElementAssert = apply {
		if (property(actual)) {
			failWithMessage("Expected element to not be $propertyName. But was!")
		}
	}

	@CheckReturnValue
	private fun apply(block: WebElementAssert.() -> Unit): WebElementAssert {
		isNotNull
		block()
		return this
	}
}

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

	@CheckReturnValue
	private fun apply(block: BrowserAssert.() -> Unit): BrowserAssert {
		isNotNull
		block()
		return this
	}
}
