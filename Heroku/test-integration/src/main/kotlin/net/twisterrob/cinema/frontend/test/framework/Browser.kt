package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chromium.ChromiumDriverLogLevel
import org.openqa.selenium.remote.Command
import org.openqa.selenium.remote.CommandExecutor
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.Response

class Browser(
	driver: WebDriver? = null
) : SearchContext {

	val driver: WebDriver by lazy { (driver ?: createDriver()).let { AngularDriver(it, it as JavascriptExecutor) } }

	fun get(relativeUrl: String) {
		navigateToAngularPage("${Options.host}${relativeUrl}")
	}

	val currentUrl: String
		get() = driver.currentUrl.removePrefix(Options.host)

	val title: String
		get() = driver.title

	override fun findElements(by: By): List<WebElement> = driver.findElements(by)
	override fun findElement(by: By): WebElement = driver.findElement(by)

	companion object {

		// Automatically will use https://www.selenium.dev/documentation/selenium_manager/.
		fun createDriver(): WebDriver {
			val options = ChromeOptions().apply {
				if (Options.headless) {
					addArguments("--headless=new")
					// Implicit via driver.manage() below, it doesn't work with new headless mode.
					//addArguments("--window-size=1920,1080")
				}
			}
			val service = ChromeDriverService.Builder()
				.withLogLevel(ChromiumDriverLogLevel.INFO)
				.withLogOutput(System.out)
				.build()
			// https://www.selenium.dev/blog/2022/using-java11-httpclient/
			System.setProperty("webdriver.http.factory", "jdk-http-client")
			val driver = ChromeDriver(service, options)
			driver.manage().apply {
				//timeouts().implicitlyWait(Duration.ofSeconds(10))
				// Ensure there's a fixed size, so tests behave the same, this is necessary in headless too.
				window().size = Dimension(1920, 1080)
			}
			return driver
		}
	}
}

/**
 * Wrapper to call [waitForAngular] before data retrieval.
 * Based on `ProtractorBrowser` in node_modules\protractor\built\browser.js.
 * Other waits are handled by [AngularCommandExecutor].
 */
private class AngularDriver(
	private val driver: WebDriver,
	private val executor: JavascriptExecutor,
) : WebDriver by driver, JavascriptExecutor by executor {

	init {
		val executorField = RemoteWebDriver::class.java
			.getDeclaredField("executor")
			.apply { isAccessible = true }
		val executor = executorField.get(driver) as CommandExecutor
		executorField.set(driver, AngularCommandExecutor(executor) { waitForAngular() })
	}

	/**
	 * ProtractorBrowser 7.0.0 explicitly listed this method to be synchronized.
	 */
	override fun getTitle(): String {
		waitForAngular()
		return driver.title
	}

	/**
	 * ProtractorBrowser 7.0.0 explicitly listed this method to be synchronized.
	 */
	override fun getCurrentUrl(): String {
		waitForAngular()
		return driver.currentUrl
	}

	/**
	 * ProtractorBrowser 7.0.0 explicitly listed this method to be synchronized.
	 */
	override fun getPageSource(): String {
		waitForAngular()
		return driver.pageSource
	}
}

/**
 * Wrapper to call [waitForAngular] before actions.
 * Data retrieval is handled by [AngularDriver].
 * based on `sendRequestToStabilize` in `blocking-proxy\built\lib\angular_wait_barrier.js
 */
private class AngularCommandExecutor(
	private val executor: CommandExecutor,
	private val stabilize: () -> Unit,
) : CommandExecutor {

	override fun execute(command: Command): Response {
		if (command.name in commandsToWaitFor) {
			stabilize()
		}
		return executor.execute(command)
	}

	companion object {

		/**
		 * https://www.selenium.dev/documentation/legacy/json_wire_protocol/#command-reference
		 */
		private val commandsToWaitFor = setOf(
//			"get"
//			"executeScript",
//			"executeAsyncScript",
			"findElement",
			"findChildElement",
			"findChildElements",
//			"getElementText",
//			"getElementAttribute",
//			"isElementSelected",
			"clickElement",
//			"quit",
			
			//"executeScript", "screenshot", "source", "title", "element", "elements", "execute", "keys",
			//"moveto", "click", "buttondown", "buttonup", "doubleclick", "touch", "get"
		)
	}
}
