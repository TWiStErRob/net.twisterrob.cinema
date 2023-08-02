package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.function.Function
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

fun WebDriver.wait(): WebDriverWait =
	WebDriverWait(this, null)

/**
 * To be used with [org.openqa.selenium.support.ui.ExpectedConditions].
 */
fun <T : Any> WebDriver.waitFor(
	condition: Function<WebDriver, T>,
	timeout: Duration = 10.seconds,
): T =
	wait().withTimeout(timeout.toJavaDuration()).until(condition)

/**
 * To be used with Kotlin trailing lambdas.
 */
fun <T : Any> WebDriver.waitFor(
	timeout: Duration = 10.seconds,
	condition: WebDriver.() -> T,
): T =
	wait().withTimeout(timeout.toJavaDuration()).until { condition(this) }
