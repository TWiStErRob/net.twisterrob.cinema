package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.pages.jasmine
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.Wait
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

fun Browser.initElements(page: Any) {
	PageFactory.initElements(driver, page)
}

operator fun WebElement.get(attribute: String): String =
	this.getAttribute(attribute)

/**
 * Creates and initializes the Page Object. To skip initialization just create the Page Object manually.
 */
inline fun <reified T> Browser.createPage(): T =
	PageFactory.initElements(driver, T::class.java)

fun <F> Wait<F>.whilst(isTrue: (F) -> Boolean): Boolean =
	until { !isTrue(it) }

/**
 * Based on [How to get selenium to wait for ajax response?](https://stackoverflow.com/a/46682394/253468).
 */
fun Browser.waitForJQuery(timeout: Duration = Duration.ofSeconds(15)) {
	val result = WebDriverWait(driver, timeout).whilst { this.isJQueryActive }
	check(result) { "jQuery is still active, something is wrong with until" }
}

val Browser.isJQueryActive: Boolean
	get() = (driver as JavascriptExecutor).isJQueryActive

val JavascriptExecutor.isJQueryActive: Boolean
	get() = executeScript("return jQuery.active !== 0") as Boolean

fun Browser.waitForElementToDisappear(
	element: WebElement,
	timeout: Duration = jasmine.DEFAULT_TIMEOUT_INTERVAL,
) {
	val result = WebDriverWait(driver, timeout).whilst { element.isDisplayed }
	check(result) { "${element} did not disappear." }
}
