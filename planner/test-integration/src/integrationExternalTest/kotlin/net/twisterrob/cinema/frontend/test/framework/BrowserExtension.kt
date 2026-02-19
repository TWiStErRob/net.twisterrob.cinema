package net.twisterrob.cinema.frontend.test.framework

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import java.io.File

// TODO rewrite using https://bonigarcia.dev/selenium-jupiter/
class BrowserExtension : BeforeEachCallback, AfterEachCallback, AfterTestExecutionCallback, ParameterResolver {

	override fun beforeEach(extensionContext: ExtensionContext) {
		val browser = Browser()
		extensionContext.store.browser = browser
		extensionContext.requiredTestInstances.allInstances.forEach { injectBrowser(it, browser) }
		extensionContext.requiredTestInstances.allInstances.forEach { injectPages(it, browser) }
	}

	override fun afterTestExecution(extensionContext: ExtensionContext) {
		extensionContext.executionException.ifPresent {
			extensionContext.store.browser?.apply {
				val file = driver.takeScreenshot(
					extensionContext.hierarchy
						.asSequence()
						.drop(1) // EngineExecutionContext (i.e. junit-jupiter)
						.map { it.displayName }
						.plus(sessionId)
						.toList()
				)
				extensionContext.publishReportEntry("Screenshot of failure", file.absolutePath)
			}
		}
	}

	override fun afterEach(extensionContext: ExtensionContext) {
		// In case Browser / Browser.createDriver() fails to initialize, there'll be no driver to quit.
		extensionContext.store.clearBrowser()?.apply {
			try {
				driver.verifyLogs()
			} finally {
				driver.quit()
			}
		}
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == BROWSER_VALUE_TYPE

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		extensionContext.store.browser

	companion object {

		private val NAMESPACE = ExtensionContext.Namespace.create(BrowserExtension::class.qualifiedName!!)

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
				.asSequence()
				.flatMap { it.declaredFields.toList() }
				.filter { it.type == BROWSER_VALUE_TYPE }
				.onEach { it.isAccessible = true }
				.forEach { it.set(instance, browser) }
		}

		private fun injectPages(instance: Any, browser: Browser) {
			instance::class.java
				.superHierarchy
				.asSequence()
				.flatMap { it.declaredFields.toList() }
				.filter { BasePage::class.java.isAssignableFrom(it.type) }
				.onEach { it.isAccessible = true }
				.forEach { it.set(instance, it.type.getConstructor(BROWSER_VALUE_TYPE).newInstance(browser)) }
		}

		private fun WebDriver.takeScreenshot(testHierarchy: List<String>): File {
			val relativePath = testHierarchy
				.joinToString(separator = File.separator, postfix = ".png") { it.toFileNameSafe() }
			val file = Options.screenshotDir.resolve(relativePath)
			file.parentFile.mkdirs()
			System.err.println("Saving screenshot to ${file}")
			val screenshot = (this as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
			file.writeBytes(screenshot)
			return file
		}
	}
}

private val Class<*>.superHierarchy: List<Class<*>>
	get() = generateSequence(this) { it.superclass }.toList()

private val ExtensionContext.hierarchy: List<ExtensionContext>
	get() = generateSequence(this) { it.parent.orElse(null) }.toList().reversed()

private fun String.toFileNameSafe(): String =
	this.replace(Regex("""[\u0000-\u001f"*/:<>?\\|\u007f]"""), "_")
