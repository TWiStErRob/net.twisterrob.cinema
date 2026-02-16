package net.twisterrob.test

import io.ktor.events.Events
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.MapApplicationConfig
import io.ktor.util.logging.Logger
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.mockito.Mockito

class KtorExtension : BeforeEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		// Create a minimal mock Application for testing
		// Since the Application constructor is internal in Ktor 3.0, we use a mock
		val app = Mockito.mock(Application::class.java)
		context.store.application = app
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == Application::class.java

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
		extensionContext.store.application
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("ktor"))

private var ExtensionContext.Store.application: Application
	get() = this.get<Application>()
		?: error("Missing Application in ${this}.")
	set(value) {
		this.put<Application>(value)
	}
