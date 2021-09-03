package net.twisterrob.test

import io.ktor.application.Application
import io.ktor.application.log
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
		context.getKtorStore().put<ApplicationEngine>(engine)
	}

	override fun afterEach(context: ExtensionContext) {
		val engine = context.getKtorStore().get<ApplicationEngine>()!!
		engine.application.log.trace("Ktor test finishing {}.{}", context.requiredTestClass, context.requiredTestMethod)
		engine.stop(0L, 0L)
		context.getKtorStore().remove<ApplicationEngine>()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type in SUPPORTED_PARAMETER_TYPES

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		when (parameterContext.parameter.type) {
			TestApplicationEngine::class.java ->
				extensionContext.getKtorStore().get<ApplicationEngine>() as TestApplicationEngine
			ApplicationEngine::class.java ->
				extensionContext.getKtorStore().get<ApplicationEngine>()
			Application::class.java ->
				extensionContext.getKtorStore().get<ApplicationEngine>()!!.application
			else -> error("Unsupported $parameterContext")
		}

	companion object {

		@Suppress("RemoveRedundantQualifierName")
		private val SUPPORTED_PARAMETER_TYPES = setOf(
			io.ktor.server.testing.TestApplicationEngine::class.java,
			io.ktor.server.engine.ApplicationEngine::class.java,
			io.ktor.application.Application::class.java,
		)
	}
}

private fun ExtensionContext.getKtorStore(): ExtensionContext.Store =
	getStore(ExtensionContext.Namespace.create("ktor"))

private inline fun <reified T : Any> ExtensionContext.Store.put(server: T) {
	this.put(T::class, server)
}

private inline fun <reified T : Any> ExtensionContext.Store.get(): T? =
	this.get(T::class, T::class.java)

private inline fun <reified T : Any> ExtensionContext.Store.remove() {
	this.remove(T::class)
}
