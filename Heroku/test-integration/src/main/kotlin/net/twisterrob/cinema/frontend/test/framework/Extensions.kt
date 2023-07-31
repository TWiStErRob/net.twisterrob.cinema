package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.Wait
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

fun WebDriver.initElements(page: Any) {
	PageFactory.initElements(this, page)
}

operator fun WebElement.get(attribute: String): String =
	this.getAttribute(attribute)

/**
 * Creates and initializes the Page Object. To skip initialization just create the Page Object manually.
 */
@Suppress("RedundantNotNullExtensionReceiverOfInline")
inline fun <reified T> WebDriver.createPage(): T =
	PageFactory.initElements(this, T::class.java)

fun <F> Wait<F>.whilst(isTrue: (F) -> Boolean): Boolean =
	until { !isTrue(it) }

/**
 * Based on [How to get selenium to wait for ajax response?](https://stackoverflow.com/a/46682394/253468).
 */
fun WebDriver.waitForJQuery() {
	val result = WebDriverWait(this, Duration.ofSeconds(15)).whilst { it.isJQueryActive }
	check(result) { "jQuery is still active, something is wrong with until" }
}

val WebDriver.isJQueryActive: Boolean
	get() = (this as JavascriptExecutor).isJQueryActive

val JavascriptExecutor.isJQueryActive: Boolean
	get() = executeScript("return jQuery.active !== 0") as Boolean
