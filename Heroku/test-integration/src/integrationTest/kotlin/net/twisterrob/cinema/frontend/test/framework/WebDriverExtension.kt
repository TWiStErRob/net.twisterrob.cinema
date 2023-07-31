package net.twisterrob.cinema.frontend.test.framework

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.openqa.selenium.WebDriver

class WebDriverExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		context.store.webDriver = Browser.createDriver()
	}

	override fun afterEach(context: ExtensionContext) {
		// In case Browser / Browser.createDriver() fails to initialize, there'll be no driver to quit.
		context.store.clearWebDriver()?.quit()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == WEB_DRIVER_VALUE_TYPE

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		extensionContext.store.webDriver

	companion object {

		private val ExtensionContext.store: ExtensionContext.Store
			get() = this.getStore(NAMESPACE)

		private val NAMESPACE = ExtensionContext.Namespace.create(WebDriverExtension::class.qualifiedName)

		private val WEB_DRIVER_KEY: Any = WebDriver::class
		private val WEB_DRIVER_VALUE_TYPE: Class<WebDriver> = WebDriver::class.java

		private var ExtensionContext.Store.webDriver: WebDriver?
			get() = this.get(WEB_DRIVER_KEY, WEB_DRIVER_VALUE_TYPE)
			set(value) = this.put(WEB_DRIVER_KEY, value)

		private fun ExtensionContext.Store.clearWebDriver(): WebDriver? =
			this.remove(WEB_DRIVER_KEY, WEB_DRIVER_VALUE_TYPE)
	}
}
