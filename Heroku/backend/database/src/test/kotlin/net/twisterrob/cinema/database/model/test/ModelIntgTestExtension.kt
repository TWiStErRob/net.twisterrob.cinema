package net.twisterrob.cinema.database.model.test

import com.flextrade.jfixture.JFixture
import dagger.Component
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.offsetDateTimeRealistic
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.harness.ServerControls
import org.neo4j.harness.TestServerBuilders
import org.neo4j.ogm.session.Session

/**
 * Injects parameter [GraphDatabaseService], only one graph will exist during a test.
 * Injects parameter [Session], supports multiple sessions in one method.
 * Injects parameter and field [JFixture] with a customised fixture for Database entities.
 *
 * Tip: it is recommended to use a separate [Session] for `sut` and [org.junit.jupiter.api.Test].
 * This prevents caching problems with changing data and getting data from cache instead of from DB.
 * ```kotlin
 * @ExtendWith(ModelIntgTestExtension::class)
 * @TagIntegration
 * class SomeIntgTest {
 *     private lateinit var fixture: JFixture
 *     private lateinit var sut: UserService
 *
 *     @BeforeEach fun setUp(session: Session) {
 *         sut = Something(session) // initialize with sut's session
 *     }
 *
 *     @Test fun test(session: Session) {
 *         val fixtData = fixture.build()
 *         session.save(fixtData) // save using test's session (this will cache fixtData)
 *
 *         val result = sut.find(fixtData.id) // read using sut's session (empty at this point)
 *
 *         assertThat(result, sameBeanAs(fixtData)) // compare all fields are equal between sessions
 *     }
 * }
 * ```
 */
class ModelIntgTestExtension : TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private lateinit var testServer: ServerControls
	private lateinit var dagger: ModelIntgTestExtensionComponent
	private val fixture: JFixture by lazy {
		JFixture().applyCustomisation {
			add(validDBData())
			add(offsetDateTimeRealistic())
		}
	}

	override fun postProcessTestInstance(testInstance: Any, extensionContext: ExtensionContext) {
		testInstance::class.java.declaredFields.filter { it.type == JFixture::class.java }
			.onEach { it.isAccessible = true }
			.forEach { it.set(testInstance, fixture) }
	}

	override fun beforeEach(extensionContext: ExtensionContext) {
		testServer = TestServerBuilders.newInProcessBuilder().newServer()
		dagger = DaggerModelIntgTestExtensionComponent
			.builder()
			.graphDBUri(testServer.boltURI())
			.build()
	}

	override fun afterEach(extensionContext: ExtensionContext) {
		testServer.graph().shutdown()
	}

	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
		return parameterContext.parameter.type in SUPPORTED_PARAMTER_TYPES
	}

	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
		when (parameterContext.parameter.type) {
			GraphDatabaseService::class.java -> testServer.graph()
			Session::class.java -> dagger.session
			JFixture::class.java -> fixture
			else -> error("Unsupported $parameterContext")
		}

	companion object {
		private val SUPPORTED_PARAMTER_TYPES = setOf(
			org.neo4j.graphdb.GraphDatabaseService::class.java,
			org.neo4j.ogm.session.Session::class.java,
			com.flextrade.jfixture.JFixture::class.java
		)
	}
}

@Component(modules = [Neo4JModule::class])
@Neo4J
private interface ModelIntgTestExtensionComponent {

	val session: Session

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): ModelIntgTestExtensionComponent
	}
}
