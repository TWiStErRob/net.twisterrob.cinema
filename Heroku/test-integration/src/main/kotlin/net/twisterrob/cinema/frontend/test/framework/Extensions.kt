package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf

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

fun Browser.waitForElementToDisappear(element: WebElement) {
	check(driver.waitFor(invisibilityOf(element))) { "${element} did not disappear." }
}

/**
 * Based on [How to get selenium to wait for ajax response?](https://stackoverflow.com/a/46682394/253468).
 */
fun Browser.waitForJQuery() {
	check(driver.waitFor { isJQueryActive }) { "jQuery is still active" }
}

private val WebDriver.isJQueryActive: Boolean
	get() = executeScript("return jQuery.active !== 0")

fun Browser.delayedExecute(locator: By, action: (WebElement) -> Unit) {
	val element = driver.wait().until(ExpectedConditions.presenceOfElementLocated(locator))
	driver.wait().until(ExpectedConditions.visibilityOf(element))
	action(element)
}
