@file:Suppress("UnusedImport") // REPORT

package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.framework.assertThat
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.assertj.core.util.CheckReturnValue
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import kotlin.reflect.KFunction1

@Suppress("NOTHING_TO_INLINE")
@CheckReturnValue
inline fun assertThat(element: WebElement): WebElementAssert =
	WebElementAssert(element)

@Suppress("TooManyFunctions") // This is how AssertJ works.
class WebElementAssert(
	element: WebElement
) : AbstractAssert<WebElementAssert, WebElement>(element, WebElementAssert::class.java) {

	fun isSelected(): WebElementAssert =
		@Suppress("USELESS_CAST") // TODO Review in Kotlin 2.1
		check("selected", actual, WebElement::isSelected as KFunction1<WebElement, Boolean>, ::failWithMessage)

	fun isNotSelected(): WebElementAssert =
		@Suppress("USELESS_CAST") // TODO Review in Kotlin 2.1
		checkNot("selected", actual, WebElement::isSelected as KFunction1<WebElement, Boolean>, ::failWithMessage)

	fun isDisplayed(): WebElementAssert =
		@Suppress("USELESS_CAST") // TODO Review in Kotlin 2.1
		check("displayed", actual, WebElement::isDisplayed as KFunction1<WebElement, Boolean>, ::failWithMessage)

	fun isNotDisplayed(): WebElementAssert =
		@Suppress("USELESS_CAST") // TODO Review in Kotlin 2.1
		checkNot("displayed", actual, WebElement::isDisplayed as KFunction1<WebElement, Boolean>, ::failWithMessage)

	fun isEnabled(): WebElementAssert =
		@Suppress("USELESS_CAST") // TODO Review in Kotlin 2.1
		check("enabled", actual, WebElement::isEnabled as KFunction1<WebElement, Boolean>, ::failWithMessage)

	fun isNotEnabled(): WebElementAssert =
		@Suppress("USELESS_CAST") // TODO Review in Kotlin 2.1
		checkNot("enabled", actual, WebElement::isEnabled as KFunction1<WebElement, Boolean>, ::failWithMessage)

	fun isChecked(): WebElementAssert =
		check("checked", actual, WebElement::isChecked, ::failWithMessage)

	fun isNotChecked(): WebElementAssert =
		checkNot("checked", actual, WebElement::isChecked, ::failWithMessage)

	fun hasIcon(iconName: String): WebElementAssert =
		checkHas("icon", actual, WebElement::hasIcon, iconName, ::failWithMessage)

	fun doesNotHaveIcon(iconName: String): WebElementAssert =
		checkDoesNotHave("icon", actual, WebElement::hasIcon, iconName, ::failWithMessage)

	@CheckReturnValue
	fun text(): AbstractStringAssert<*> = assertThat(actual.textContent)

	@CheckReturnValue
	fun classes(): ListAssert<String> = assertThat(actual.classes)

	@CheckReturnValue
	fun descendant(locator: By): WebElementAssert = assertThat(actual.findElement(locator))

	@CheckReturnValue // TODO WebElementListAssert if actually used.
	fun descendants(locator: By): ListAssert<WebElement> = assertThat(actual.findElements(locator))
}
