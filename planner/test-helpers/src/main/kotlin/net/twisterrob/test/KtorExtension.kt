package net.twisterrob.test

import io.ktor.events.Events
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.MapApplicationConfig
import io.ktor.util.logging.Logger
import kotlinx.coroutines.EmptyCoroutineContext
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class KtorExtension : BeforeEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		// Create a minimal Application for testing
		val app = createTestApplication()
		context.store.application = app
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type == Application::class.java

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
		extensionContext.store.application

	companion object {
		private fun createTestApplication(): Application {
			val env = object : ApplicationEnvironment {
				override val classLoader: ClassLoader = this::class.java.classLoader
				override val log: Logger = object : Logger by org.slf4j.helpers.NOPLogger.NOP_LOGGER {}
				override val config = MapApplicationConfig()
				override val monitor = Events()
			}
			return Application(
				environment = env,
				developmentMode = false,
				rootPath = "/",
				monitor = Events(),
				parentCoroutineContext = EmptyCoroutineContext,
				engineProvider = { null }
			)
		}
	}
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("ktor"))

private var ExtensionContext.Store.application: Application
	get() = this.get<Application>()
		?: error("Missing Application in ${this}.")
	set(value) {
		this.put<Application>(value)
	}
