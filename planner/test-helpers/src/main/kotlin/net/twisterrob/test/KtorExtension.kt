package net.twisterrob.test

import io.ktor.server.application.Application
import io.ktor.server.testing.TestApplication
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class KtorExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		// Create a TestApplication to get a real Application instance
		val testApp = TestApplication {
			// Empty test application - just need the Application instance for unit tests
		}
		context.store.testApplication = testApp
		context.store.application = testApp.application
	}

	override fun afterEach(context: ExtensionContext) {
		// Clean up the test application properly
		val testApp = context.store.testApplication
		// Note: TestApplication in Ktor 3.0 doesn't expose stop() method directly
		// and is designed to be lightweight for unit tests, so explicit cleanup is not needed
		context.store.remove<TestApplication>()
		context.store.remove<Application>()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == Application::class.java

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
		extensionContext.store.application
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("ktor"))

private var ExtensionContext.Store.testApplication: TestApplication
	get() = this.get<TestApplication>()
		?: error("Missing TestApplication in ${this}.")
	set(value) {
		this.put<TestApplication>(value)
	}

private var ExtensionContext.Store.application: Application
	get() = this.get<Application>()
		?: error("Missing Application in ${this}.")
	set(value) {
		this.put<Application>(value)
	}
