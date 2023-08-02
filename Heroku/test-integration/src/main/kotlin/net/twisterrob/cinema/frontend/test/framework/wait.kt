package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.pages.jasmine
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.function.Function
import kotlin.time.Duration
import kotlin.time.toJavaDuration

fun WebDriver.wait(): WebDriverWait =
	WebDriverWait(this, jasmine.DEFAULT_TIMEOUT_INTERVAL.toJavaDuration())

/**
 * To be used with [org.openqa.selenium.support.ui.ExpectedConditions].
 */
fun <T : Any> WebDriver.waitFor(
	condition: Function<WebDriver, T>,
	timeout: Duration = jasmine.DEFAULT_TIMEOUT_INTERVAL,
): T =
	wait().withTimeout(timeout.toJavaDuration()).until(condition)

/**
 * To be used with Kotlin trailing lambdas.
 */
fun <T : Any> WebDriver.waitFor(
	timeout: Duration = jasmine.DEFAULT_TIMEOUT_INTERVAL,
	condition: WebDriver.() -> T,
): T =
	wait().withTimeout(timeout.toJavaDuration()).until { condition(this) }
