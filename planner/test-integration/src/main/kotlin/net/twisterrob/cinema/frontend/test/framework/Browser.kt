package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chromium.ChromiumDriverLogLevel
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.logging.Level

@Suppress("ClassOrdering") // In logical order.
class Browser {

	val driver: WebDriver by lazy { createDriver().apply(::replaceCommandExecutor) }

	fun get(relativeUrl: String) {
		navigateToAngularPage("${Options.baseUrl}${relativeUrl}")
	}

	val sessionId: String
		get() = (driver as RemoteWebDriver).sessionId.toString()

	val currentUrl: String?
		get() = driver.currentUrl?.removePrefix(Options.baseUrl)

	val title: String?
		get() = driver.title

	companion object {

		@Suppress("NestedScopeFunctions") // KISS the setup.
		// Automatically will use https://www.selenium.dev/documentation/selenium_manager/.
		private fun createDriver(): RemoteWebDriver {
			val options = ChromeOptions().apply {
				setCapability(ChromeOptions.LOGGING_PREFS, LoggingPreferences().apply {
					enable(LogType.BROWSER, Level.ALL)
				})
				// TODO is this faster?
				//addArguments("--disable-extensions")
				// TODO what does this mean?
				//addArguments("--no-sandbox")
				// TODO Not sure if necessary (Windows only?), keep it for future reference.
				//addArguments("--disable-gpu")
				if (Options.isHeadless) {
					addArguments("--headless=new")
					// Implicit via driver.manage() below, it doesn't work with new headless mode.
					//addArguments("--window-size=${Options.windowSize.width},${Options.windowSize.height)}")
				}
			}
			val service = ChromeDriverService.Builder()
				.withLogLevel(ChromiumDriverLogLevel.INFO)
				.withLogOutput(System.out)
				.withReadableTimestamp(true)
				.build()
			val driver = ChromeDriver(service, options)
			driver.manage().apply {
				//timeouts().implicitlyWait(Duration.ofSeconds(10))
				// Ensure there's a fixed size, so tests behave the same, this is necessary in headless too.
				window().size = Options.windowSize
				assertThat(logs().availableLogTypes).contains(LogType.BROWSER)
			}
			return driver
		}
	}
}
