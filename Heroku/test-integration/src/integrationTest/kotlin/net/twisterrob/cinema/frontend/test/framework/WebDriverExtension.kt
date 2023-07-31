package net.twisterrob.cinema.frontend.test.framework

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class BrowserExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		context.store.browser = Browser(Browser.createDriver())
	}

	override fun afterEach(context: ExtensionContext) {
		// In case Browser / Browser.createDriver() fails to initialize, there'll be no driver to quit.
		context.store.clearBrowser()?.driver?.quit()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == BROWSER_VALUE_TYPE

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		extensionContext.store.browser

	companion object {

		private val ExtensionContext.store: ExtensionContext.Store
			get() = this.getStore(NAMESPACE)

		private val NAMESPACE = ExtensionContext.Namespace.create(BrowserExtension::class.qualifiedName)

		private val BROWSER_KEY: Any = Browser::class
		private val BROWSER_VALUE_TYPE: Class<Browser> = Browser::class.java

		private var ExtensionContext.Store.browser: Browser?
			get() = this.get(BROWSER_KEY, BROWSER_VALUE_TYPE)
			set(value) = this.put(BROWSER_KEY, value)

		private fun ExtensionContext.Store.clearBrowser(): Browser? =
			this.remove(BROWSER_KEY, BROWSER_VALUE_TYPE)
	}
}
