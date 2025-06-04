package net.twisterrob.test

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.serverConfig
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestEngine
import io.ktor.server.testing.createTestEnvironment
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class KtorExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		val embeddedServer = EmbeddedServer(
			rootConfig = serverConfig(createTestEnvironment()) {
				watchPaths = emptyList()
			},
			engineFactory = TestEngine,
		)
		embeddedServer.start()
		embeddedServer.environment.log
			.trace("Ktor test starting {}.{}", context.requiredTestClass, context.requiredTestMethod)
		context.store.embeddedServer = embeddedServer
	}

	override fun afterEach(context: ExtensionContext) {
		val embeddedServer = context.store.embeddedServer
		embeddedServer.environment.log
			.trace("Ktor test finishing {}.{}", context.requiredTestClass, context.requiredTestMethod)
		embeddedServer.stop(0, 0)
		context.store.remove<EmbeddedServer<*, *>>()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type in SUPPORTED_PARAMETER_TYPES

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
		when (parameterContext.parameter.type) {
			EmbeddedServer::class.java ->
				extensionContext.store.embeddedServer

			TestApplicationEngine::class.java ->
				extensionContext.store.embeddedServer.engine as TestApplicationEngine

			ApplicationEngine::class.java ->
				extensionContext.store.embeddedServer.engine

			Application::class.java ->
				extensionContext.store.embeddedServer.application

			ApplicationEnvironment::class.java ->
				extensionContext.store.embeddedServer.environment

			else ->
				error("Unsupported $parameterContext")
		}

	companion object {

		@Suppress("RemoveRedundantQualifierName")
		private val SUPPORTED_PARAMETER_TYPES = setOf(
			io.ktor.server.engine.EmbeddedServer::class.java,
			io.ktor.server.testing.TestApplicationEngine::class.java,
			io.ktor.server.engine.ApplicationEngine::class.java,
			io.ktor.server.application.Application::class.java,
			io.ktor.server.application.ApplicationEnvironment::class.java,
		)
	}
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("ktor"))

private var ExtensionContext.Store.embeddedServer: EmbeddedServer<*, *>
	get() = this.get<EmbeddedServer<*, *>>()
		?: error("Missing ApplicationEngine in ${this}.")
	set(value) {
		this.put<EmbeddedServer<*, *>>(value)
	}
