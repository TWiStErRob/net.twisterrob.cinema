package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.function.Function
import kotlin.time.Duration
import kotlin.time.toJavaDuration

fun WebDriver.wait(): WebDriverWait =
	WebDriverWait(this, Options.defaultWaitTimeout.toJavaDuration())

/**
 * To be used with [org.openqa.selenium.support.ui.ExpectedConditions].
 */
fun <T : Any> WebDriver.waitFor(
	condition: Function<WebDriver, T>,
	timeout: Duration = Options.defaultWaitTimeout,
): T =
	wait().withTimeout(timeout.toJavaDuration()).until(condition)

/**
 * To be used with Kotlin trailing lambdas.
 */
fun <T : Any> WebDriver.waitFor(
	timeout: Duration = Options.defaultWaitTimeout,
	condition: WebDriver.() -> T,
): T =
	wait().withTimeout(timeout.toJavaDuration()).until { condition(this) }

fun Browser.waitForElementToDisappear(element: WebElement) {
	check(driver.waitFor(ExpectedConditions.invisibilityOf(element))) { "${element} did not disappear." }
}

fun Browser.delayedExecute(locator: By, action: (WebElement) -> Unit) {
	val element = driver.wait().until(ExpectedConditions.presenceOfElementLocated(locator))
	driver.wait().until(ExpectedConditions.visibilityOf(element))
	action(element)
}
