package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.remote.Command
import org.openqa.selenium.remote.CommandExecutor
import org.openqa.selenium.remote.DriverCommand
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.Response

/**
 * Replaces the executor to call [waitForAngular] at points where things change in the web view,
 * so the test code doesn't have to know about it.
 * This mimics what Protractor/BlockingProxy does in JavaScript.
 */
fun replaceCommandExecutor(driver: RemoteWebDriver) {
	val executor = AngularCommandExecutor(driver.commandExecutor) {
		driver.waitForAngular()
	}
	driver.setCommandExecutor(executor)
}

/**
 * Hacks the WebDriver instance to replace the command executor.
 * [org.openqa.selenium.chrome.ChromeDriver.generateExecutor] doesn't allow for customization,
 * and there's no public way to replace it after construction as of Selenium 4.11.
 */
private fun RemoteWebDriver.setCommandExecutor(executor: CommandExecutor) {
	RemoteWebDriver::class.java
		.getDeclaredMethod("setCommandExecutor", CommandExecutor::class.java)
		.apply { isAccessible = true }
		.invoke(this, executor)
}

/**
 * Wrapper to call [waitForAngular] before/after remote actions.
 * Based on NPM package [protractor](https://www.npmjs.com/package/protractor)
 *  * `ProtractorBrowser` constructor in `protractor\built\browser.js`
 *  * `sendRequestToStabilize` in `blocking-proxy\built\lib\angular_wait_barrier.js`
 */
private class AngularCommandExecutor(
	private val executor: CommandExecutor,
	private val stabilize: () -> Unit,
) : CommandExecutor {

	override fun execute(command: Command): Response {
		if (command.name in stabilizeBefore) {
			stabilize()
		}
		val response = executor.execute(command)
		if (command.name in stabilizeAfter) {
			stabilize()
		}
		return response
	}

	/**
	 * List of commands that might need special handling.
	 *
	 * See [Full list of commands](https://www.selenium.dev/documentation/legacy/json_wire_protocol/#command-reference).
	 *
	 * @see DriverCommand
	 */
	private companion object Commands {

		@Suppress("unused") // For documentation.
		private val noStabilize: Set<String> = setOf(
			// Handled by [navigateToAngularPage].
			DriverCommand.GET,
			// Watch out for recursion.
			DriverCommand.EXECUTE_SCRIPT,
			// Watch out for recursion.
			DriverCommand.EXECUTE_ASYNC_SCRIPT,
			// Already stopping, no need to wait for the app.
			DriverCommand.QUIT,
			// These are queries, don't synchronize to improve performance.
			DriverCommand.FIND_ELEMENT,
			DriverCommand.FIND_CHILD_ELEMENT,
			DriverCommand.FIND_CHILD_ELEMENTS,
			DriverCommand.GET_ELEMENT_TEXT,
			DriverCommand.GET_ELEMENT_ATTRIBUTE,
			DriverCommand.IS_ELEMENT_SELECTED,
		)
		private val stabilizeBefore: Set<String> = setOf(
			// ProtractorBrowser 7.0.0 explicitly listed WebDriver.getPageSource method to be synchronized.
			DriverCommand.GET_PAGE_SOURCE,
			// ProtractorBrowser 7.0.0 explicitly listed WebDriver.getTitle method to be synchronized.
			DriverCommand.GET_TITLE,
			// ProtractorBrowser 7.0.0 explicitly listed WebDriver.getCurrentUrl method to be synchronized.
			DriverCommand.GET_CURRENT_URL,
			DriverCommand.SCREENSHOT,
			DriverCommand.ELEMENT_SCREENSHOT,
		)

		/**
		 * https://www.selenium.dev/documentation/legacy/json_wire_protocol/#command-reference
		 */
		private val stabilizeAfter: Set<String> = setOf(
			DriverCommand.CLICK_ELEMENT,
			DriverCommand.SEND_KEYS_TO_ELEMENT,
		)
	}
}
