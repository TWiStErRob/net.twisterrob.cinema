package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.WebDriver
import kotlin.time.Duration.Companion.seconds

fun Browser.navigateToAngularPage(url: String) {
	driver.get("data:text/html,<html></html>")
	driver.deferAngularBootstrap()
	driver.get(url)
	driver.waitForAngularToExist()
	driver.disableAngularAnimations()
	driver.resumeAngular()
	driver.waitForAngular()
}

private fun WebDriver.deferAngularBootstrap() {
	executeScript<Unit>(
		"""
			window.name = "NG_DEFER_BOOTSTRAP!" + window.name;
		""".trimIndent()
	)
}

private fun WebDriver.waitForAngularToExist() {
	check(waitFor(10.seconds) { hasAngular }) { "Page has no angular." }
}

private val WebDriver.hasAngular: Boolean
	get() = executeScript(
		"""
			/* global angular: false // Comes from the opened page. */
			return window.angular !== undefined;
		""".trimIndent()
	)

private fun WebDriver.disableAngularAnimations() {
	val animate = """${'$'}animate"""
	executeScript<Unit>(
		"""
			/* global angular: false // Comes from the opened page. */
			angular.module('disableNgAnimate', []).run(['$animate', function ($animate) {
				$animate.enabled(false);
			}]);
		""".trimIndent()
	)
	// TODO consider also https://declara.com/content/J1J2Gkk1
	//findElement(By.tagName("body")).allowAnimations(false);
}

private fun WebDriver.resumeAngular() {
	executeScript<Unit>(
		"""
			/* global angular: false // Comes from the opened page. */
			window.__TESTABILITY__NG1_APP_ROOT_INJECTOR__ = angular.resumeBootstrap(["disableNgAnimate"]);
		""".trimIndent(),
	)
}

fun Browser.waitForAngular() {
	driver.waitForAngular()
}

fun WebDriver.waitForAngular() {
	executeAsyncScript<Unit>(
		"""
			const callback = arguments[arguments.length - 1]; // executeAsyncScrip's contract.
			const injector = window.__TESTABILITY__NG1_APP_ROOT_INJECTOR__;
			const testability = injector.get("${'$'}${'$'}testability");
			testability.whenStable(callback);
			//injector.get("${'$'}browser").notifyWhenNoOutstandingRequests(callback);
		""".trimIndent()
	)
}

/**
 * There are no automatic waits in the current system, but keeping this in case there'll be.
 */
inline fun <T> Browser.nonAngular(action: () -> T): T {
	//waitForAngularEnabled(false)
	try {
		return action()
	} finally {
		//waitForAngularEnabled(true)
	}
}
