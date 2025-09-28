package net.twisterrob.cinema.database.model.test

import dagger.Component
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.test.get
import net.twisterrob.test.put
import net.twisterrob.test.remove
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Driver
import org.neo4j.ogm.session.Session
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName
import java.net.URI
import kotlin.jvm.java
import kotlin.jvm.optionals.getOrNull

/**
 * Injects parameter [Driver], only one graph will exist during a test.
 * Injects parameter [Session], supports multiple sessions in one method.
 *
 * Tip: it is recommended to use a separate [Session] for `sut` and [org.junit.jupiter.api.Test].
 * This prevents caching problems with changing data and getting data from cache instead of from DB.
 * ```kotlin
 * @ExtendWith(ModelIntgTestExtension::class)
 * class SomeIntgTest {
 *     private lateinit var sut: UserService
 *
 *     @BeforeEach fun setUp(session: Session) {
 *         sut = Something(session) // initialize with sut's session
 *     }
 *
 *     @Test fun test(session: Session) {
 *         session.save(data) // save using test's session (this will cache fixtData)
 *
 *         val result = sut.find(data.id) // read using sut's session (empty at this point)
 *
 *         assertThat(result, sameBeanAs(data)) // compare all fields are equal between sessions
 *     }
 * }
 * ```
 */
class ModelIntgTestExtension : BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	override fun beforeAll(context: ExtensionContext) {
		val testClass = context.testClass.getOrNull() ?: return
		require(testClass.name.endsWith("IntgTest")) {
			"Integration tests must end with 'IntgTest' to be run with ${ModelIntgTestExtension::class}."
		}
	}

	override fun beforeEach(extensionContext: ExtensionContext) {
		val testServer =
			Neo4jContainer(DockerImageName.parse("neo4j:2025.07.1"))
				.withoutAuthentication()
				.apply { start() }
		extensionContext.store.put(testServer)
		extensionContext.store.put(GraphDatabase.driver(testServer.boltUrl))
		val dagger = DaggerModelIntgTestExtensionComponent
			.builder()
			.graphDBUri(URI(testServer.boltUrl))
			.build()
		extensionContext.store.put(dagger)
	}

	override fun afterEach(extensionContext: ExtensionContext) {
		if (!extensionContext.executionException.isPresent) {
			// Don't try to close if there was an error during initialization.
			extensionContext.store.get<Driver>()?.close()
			extensionContext.store.get<Neo4jContainer<*>>()?.stop()
		} else {
			extensionContext.store.get<Driver>()!!.close()
			extensionContext.store.get<Neo4jContainer<*>>()!!.stop()
		}
		extensionContext.store.remove<Driver>()
		extensionContext.store.remove<Neo4jContainer<*>>()
		extensionContext.store.remove<ModelIntgTestExtensionComponent>()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
		parameterContext.parameter.type in SUPPORTED_PARAMTER_TYPES

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		when (parameterContext.parameter.type) {
			Driver::class.java ->
				extensionContext.store.get<Driver>()!!
			Neo4jContainer::class.java ->
				extensionContext.store.get<Neo4jContainer<*>>()!!
			Session::class.java ->
				extensionContext.store.get<ModelIntgTestExtensionComponent>()!!.session
			else -> error("Unsupported $parameterContext")
		}

	companion object {

		private val SUPPORTED_PARAMTER_TYPES = setOf(
			org.neo4j.driver.Driver::class.java,
			org.testcontainers.containers.Neo4jContainer::class.java,
			org.neo4j.ogm.session.Session::class.java,
		)
	}
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("intgTestModel"))

@Component(modules = [Neo4JModule::class])
@Neo4J
private interface ModelIntgTestExtensionComponent {

	val session: Session

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): ModelIntgTestExtensionComponent
	}
}
