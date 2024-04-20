package net.twisterrob.test

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class KtorExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		val engine = TestApplicationEngine(createTestEnvironment()) {}
		engine.start()
		engine.application.log.trace("Ktor test starting {}.{}", context.requiredTestClass, context.requiredTestMethod)
		context.store.applicationEngine = engine
	}

	override fun afterEach(context: ExtensionContext) {
		val engine = context.store.applicationEngine
		engine.application.log.trace("Ktor test finishing {}.{}", context.requiredTestClass, context.requiredTestMethod)
		engine.stop(0L, 0L)
		context.store.remove<ApplicationEngine>()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type in SUPPORTED_PARAMETER_TYPES

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
		when (parameterContext.parameter.type) {
			TestApplicationEngine::class.java ->
				extensionContext.store.applicationEngine as TestApplicationEngine
			ApplicationEngine::class.java ->
				extensionContext.store.applicationEngine
			Application::class.java ->
				extensionContext.store.applicationEngine.application
			else ->
				error("Unsupported $parameterContext")
		}

	companion object {

		@Suppress("RemoveRedundantQualifierName")
		private val SUPPORTED_PARAMETER_TYPES = setOf(
			io.ktor.server.testing.TestApplicationEngine::class.java,
			io.ktor.server.engine.ApplicationEngine::class.java,
			io.ktor.server.application.Application::class.java,
		)
	}
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("ktor"))

private var ExtensionContext.Store.applicationEngine: ApplicationEngine
	get() = this.get<ApplicationEngine>()
		?: error("Missing ApplicationEngine in ${this}.")
	set(value) {
		this.put<ApplicationEngine>(value)
	}
