package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestWatcher
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.logging.LogType
import java.util.Optional

class BrowserExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver, TestWatcher {

	override fun beforeEach(extensionContext: ExtensionContext) {
		val browser = Browser()
		extensionContext.store.browser = browser
		extensionContext.requiredTestInstances.allInstances.forEach { injectBrowser(it, browser) }
		extensionContext.requiredTestInstances.allInstances.forEach { injectPages(it, browser) }
	}

	override fun afterEach(extensionContext: ExtensionContext) {
		extensionContext.store.browser?.apply {
			driver.verifyLogs()
			// Note: we're not clearing the browser yet, because TestWatcher still needs it.
			// Therefore, we need to override each TestWatcher method to always make sure the browser is closed.
		}
	}

	override fun testFailed(extensionContext: ExtensionContext, cause: Throwable?) {
		try {
			extensionContext.takeScreenshot()
		} finally {
			extensionContext.tryQuitSession()
		}
	}

	private fun ExtensionContext.takeScreenshot() {
		val driver = this.store.browser!!.driver
		val screenshot = (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
		val relativePath = this.displayName.replace(Regex("""[^a-zA-Z0-9._-]"""), "_") + ".png"
		val file = Options.screenshotDir.resolve(relativePath)
		file.parentFile.mkdirs()
		file.writeBytes(screenshot)
		this.publishReportEntry("Screenshot of failure", file.absolutePath)
	}

	override fun testDisabled(extensionContext: ExtensionContext, reason: Optional<String>?) {
		extensionContext.tryQuitSession()
	}

	override fun testSuccessful(extensionContext: ExtensionContext) {
		extensionContext.tryQuitSession()
	}

	override fun testAborted(extensionContext: ExtensionContext, cause: Throwable?) {
		extensionContext.tryQuitSession()
	}

	private fun ExtensionContext.tryQuitSession() {
		// In case Browser / Browser.createDriver() fails to initialize, there'll be no driver to quit.
		this.store.clearBrowser()?.apply { driver.quit() }
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == BROWSER_VALUE_TYPE

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		extensionContext.store.browser

	companion object {

		private val NAMESPACE = ExtensionContext.Namespace.create(BrowserExtension::class.qualifiedName)

		private val ExtensionContext.store: ExtensionContext.Store
			get() = this.getStore(NAMESPACE)

		private val BROWSER_KEY: Any = Browser::class
		private val BROWSER_VALUE_TYPE: Class<Browser> = Browser::class.java

		private var ExtensionContext.Store.browser: Browser?
			get() = this.get(BROWSER_KEY, BROWSER_VALUE_TYPE)
			set(value) = this.put(BROWSER_KEY, value)

		private fun ExtensionContext.Store.clearBrowser(): Browser? =
			this.remove(BROWSER_KEY, BROWSER_VALUE_TYPE)

		private fun injectBrowser(instance: Any, browser: Browser) {
			instance::class.java
				.superHierarchy
				.flatMap { it.declaredFields.toList() }
				.filter { it.type == BROWSER_VALUE_TYPE }
				.onEach { it.isAccessible = true }
				.forEach { it.set(instance, browser) }
		}

		private fun injectPages(instance: Any, browser: Browser) {
			instance::class.java
				.superHierarchy
				.flatMap { it.declaredFields.toList() }
				.filter { BasePage::class.java.isAssignableFrom(it.type) }
				.onEach { it.isAccessible = true }
				.forEach { it.set(instance, it.type.getConstructor(BROWSER_VALUE_TYPE).newInstance(browser)) }
		}

		/**
		 * Grab logs from Chrome console at the end of the test, so we can make sure there are no problems:
		 *  * Chrome deprecations
		 *  * JavaScript errors
		 *  * JavaScript warnings
		 *  * Angular deprecations
		 *  * Leftover console.log() calls
		 *  * etc.
		 */
		private fun WebDriver.verifyLogs() {
			// JavaScript running in the browser: console.log("hello");
			// Results in the following output to driver logs:
			// > [1690997212.256][DEBUG]: DevTools WebSocket Event: Runtime.consoleAPICalled (session_id=...) ... {
			// >   "args": [ {
			// >     "type": "string",
			// >     "value": "hello"
			// >   } ],
			// >   "executionContextId": 1,
			// >   "stackTrace": { ... }
			val logs = manage().logs().get(LogType.BROWSER).all
			logs.forEach(LogPrinter()::print)
			assertThat(logs).isEmpty()
		}
	}
}

private val Class<*>.superHierarchy: List<Class<*>>
	get() = generateSequence(this) { it.superclass }.toList()
