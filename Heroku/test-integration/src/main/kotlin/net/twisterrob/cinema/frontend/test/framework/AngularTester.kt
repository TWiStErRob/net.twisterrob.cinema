package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

fun Browser.navigateToAngularPage(url: String) {
	driver.get("data:text/html,<html></html>")
	deferAngularBootstrap()
	driver.get(url)
	waitForAngularToExist()
	disableAngularAnimations()
	resumeAngular()
	waitForAngular()
}

private fun Browser.deferAngularBootstrap() {
	executeScript<Unit>(
		"""
			window.name = "NG_DEFER_BOOTSTRAP!" + window.name;
		""".trimIndent()
	)
}

fun Browser.waitForAngularToExist(timeout: Duration = Duration.ofSeconds(15)) {
	val result = WebDriverWait(driver, timeout).until { hasAngular }
	check(result) { "Page has no angular." }
}

private val Browser.hasAngular: Boolean
	get() = executeScript(
		"""
			/* global angular: false // Comes from the opened page. */
			return window.angular !== undefined;
		""".trimIndent()
	)

private fun Browser.disableAngularAnimations() {
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

private fun Browser.resumeAngular() {
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
			const callback = arguments[0]; // executeAsyncScrip's contract.
			const injector = window.__TESTABILITY__NG1_APP_ROOT_INJECTOR__;
			const testability = injector.get('${'$'}${'$'}testability');
			testability.whenStable(callback);
		""".trimIndent()
	)
}
