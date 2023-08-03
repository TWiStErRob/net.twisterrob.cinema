package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chromium.ChromiumDriverLogLevel
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.Command
import org.openqa.selenium.remote.CommandExecutor
import org.openqa.selenium.remote.DriverCommand
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.Response
import java.util.logging.Level

class Browser(
	driver: WebDriver? = null
) : SearchContext {

	val driver: WebDriver by lazy { (driver ?: createDriver()).also { replaceCommandExecutor(it) } }

	fun get(relativeUrl: String) {
		navigateToAngularPage("${Options.baseUrl}${relativeUrl}")
	}

	val currentUrl: String
		get() = driver.currentUrl.removePrefix(Options.baseUrl)

	val title: String
		get() = driver.title

	override fun findElements(by: By): List<WebElement> = driver.findElements(by)
	override fun findElement(by: By): WebElement = driver.findElement(by)

	companion object {

		// Automatically will use https://www.selenium.dev/documentation/selenium_manager/.
		fun createDriver(): WebDriver {
			val options = ChromeOptions().apply {
				setCapability(ChromeOptions.LOGGING_PREFS, LoggingPreferences().apply {
					enable(LogType.BROWSER, Level.ALL)
				})
				// Try to fix error on GitHub Actions CI (based on https://stackoverflow.com/a/50642913/253468)
				// > org.openqa.selenium.SessionNotCreatedException:
				// > Could not start a new session. Response code 500.
				// > Message: unknown error: Chrome failed to start: exited abnormally.
				// > (unknown error: DevToolsActivePort file doesn't exist)
				addArguments("--disable-dev-shm-usage")
				// TODO is this faster?
				//addArguments("--disable-extensions")
				// TODO what does this mean?
				//addArguments("--no-sandbox")
				if (Options.headless) {
					addArguments("--headless=new")
					// Implicit via driver.manage() below, it doesn't work with new headless mode.
					//addArguments("--window-size=1920,1080")
					// Not sure if necessary, keep it for future reference.
					//addArguments("--disable-gpu")
				}
			}
			val service = ChromeDriverService.Builder()
				.withLogLevel(ChromiumDriverLogLevel.INFO)
				.withLogOutput(System.out)
				.withReadableTimestamp(true)
				.build()
			// https://www.selenium.dev/blog/2022/using-java11-httpclient/
			System.setProperty("webdriver.http.factory", "jdk-http-client")
			val driver = ChromeDriver(service, options)
			driver.manage().apply {
				//timeouts().implicitlyWait(Duration.ofSeconds(10))
				// Ensure there's a fixed size, so tests behave the same, this is necessary in headless too.
				window().size = Options.windowSize
				assertThat(logs().availableLogTypes).contains(LogType.BROWSER)
			}
			return driver
		}

		/**
		 * Wrapper to call [waitForAngular] before data retrieval. STOPSHIP revise
		 * Based on `ProtractorBrowser` in node_modules\protractor\built\browser.js.
		 * Other waits are handled by [AngularCommandExecutor].
		 */
		fun replaceCommandExecutor(driver: WebDriver) {
			val executorField = RemoteWebDriver::class.java
				.getDeclaredField("executor")
				.apply { isAccessible = true }
			val executor = executorField.get(driver) as CommandExecutor
			executorField.set(driver, AngularCommandExecutor(executor) { driver.waitForAngular() })
		}

	}
}

/**
 * Wrapper to call [waitForAngular] before actions.
 * Data retrieval is handled by [Browser.replaceCommandExecutor].
 * based on `sendRequestToStabilize` in `blocking-proxy\built\lib\angular_wait_barrier.js
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
	 * Full list:
	 * https://www.selenium.dev/documentation/legacy/json_wire_protocol/#command-reference
	 *
	 * @see DriverCommand
	 */
	companion object {

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
