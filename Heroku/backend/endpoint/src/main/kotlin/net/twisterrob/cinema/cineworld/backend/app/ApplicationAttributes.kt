package net.twisterrob.cinema.cineworld.backend.app

import io.ktor.application.Application
import io.ktor.routing.Routing
import io.ktor.routing.RoutingApplicationCall
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes
import java.io.File
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Attributes available for the [Application] related this app.
 *
 * **Note**: For most use cases this shouldn't be necessary, as everything is Dagger [Injected][javax.inject.Inject] automatically.
 */
object ApplicationAttributes {

	private val DaggerComponentAttribute = AttributeKey<ApplicationComponent>("dagger")

	var Attributes.dagger: ApplicationComponent by key(DaggerComponentAttribute)

	private val StaticRootFolderAttribute = AttributeKey<File>("staticRootFolder")

	var Attributes.staticRootFolder: File by key(StaticRootFolderAttribute)

	@Suppress("unused") // not needed yet, ApplicationComponent is alive for the whole Application lifecycle
	private fun Application.registerDaggerForEachCall(dagger: ApplicationComponent) {
		environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
			call.attributes.dagger = dagger
		}
	}
}

private fun <T : Any> key(attributeKey: AttributeKey<T>) =
	object : ReadWriteProperty<Attributes, T> {
		override fun getValue(thisRef: Attributes, property: KProperty<*>): T =
			thisRef[attributeKey]

		override fun setValue(thisRef: Attributes, property: KProperty<*>, value: T) {
			thisRef.put(attributeKey, value)
		}
	}
