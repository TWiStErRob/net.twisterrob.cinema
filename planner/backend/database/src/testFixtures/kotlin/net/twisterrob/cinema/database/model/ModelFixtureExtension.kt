package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import net.twisterrob.test.applyCustomisation
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor

/**
 * Injects parameter and field [JFixture] with a customised fixture for Database model entities.
 *
 * ```kotlin
 * @ExtendWith(ModelFixtureExtension::class)
 * class SomeTest {
 *     private lateinit var fixture: JFixture
 *
 *     @Test fun test() {
 *         val fixtData = fixture.build()
 *     }
 * }
 * ```
 */
class ModelFixtureExtension : TestInstancePostProcessor {

	override fun postProcessTestInstance(testInstance: Any, extensionContext: ExtensionContext) {
		val fixture: JFixture = JFixture().applyCustomisation {
			add(validDBData())
		}
		extensionContext.store.put(fixture)

		testInstance::class.java.declaredFields.filter { it.type == JFixture::class.java }
			.onEach { it.isAccessible = true }
			.forEach { it.set(testInstance, fixture) }
	}
}

private val ExtensionContext.store: ExtensionContext.Store
	get() = this.getStore(ExtensionContext.Namespace.create("modelFixture"))
