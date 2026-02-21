package net.twisterrob.test

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.testing.TestApplication
import io.ktor.server.testing.TestApplicationEngine
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import kotlin.jvm.java

class KtorExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeEach(context: ExtensionContext) {
		val testApplication = TestApplication { }
		runBlocking { testApplication.start() }
		testApplication.application.log
			.trace("Ktor test starting {}.{}", context.requiredTestClass, context.requiredTestMethod)
		context.store.testApplication = testApplication
	}

	override fun afterEach(context: ExtensionContext) {
		val testApplication = context.store.testApplication
		testApplication.application.log
			.trace("Ktor test finishing {}.{}", context.requiredTestClass, context.requiredTestMethod)
		runBlocking { testApplication.stop() }
		context.store.remove<TestApplication>()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type in SUPPORTED_PARAMETER_TYPES

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
		when (parameterContext.parameter.type) {
			TestApplication::class.java ->
				extensionContext.store.testApplication

			Application::class.java ->
				extensionContext.store.testApplication.application

			ApplicationEngine::class.java ->
				extensionContext.store.testApplication.application.engine

			TestApplicationEngine::class.java ->
				extensionContext.store.testApplication.application.engine

			else ->
				error("Unsupported $parameterContext")
		}

	companion object {

		@Suppress("RemoveRedundantQualifierName", "detekt.UnnecessaryFullyQualifiedName")
		private val SUPPORTED_PARAMETER_TYPES = setOf(
			io.ktor.server.testing.TestApplication::class.java,
			io.ktor.server.testing.TestApplicationEngine::class.java,
			io.ktor.server.engine.ApplicationEngine::class.java,
			io.ktor.server.application.Application::class.java,
		)
	}
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("ktor"))

private var ExtensionContext.Store.testApplication: TestApplication
	get() = this.get<TestApplication>()
		?: error("Missing TestApplication in ${this}.")
	set(value) {
		this.put<TestApplication>(value)
	}
