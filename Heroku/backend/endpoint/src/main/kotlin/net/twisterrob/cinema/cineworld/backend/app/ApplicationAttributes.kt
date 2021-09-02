package net.twisterrob.cinema.cineworld.backend.app

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
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

	var Attributes.dagger: ApplicationComponent by requiredKey(DaggerComponentAttribute)

	private val StaticRootFolderAttribute = AttributeKey<File>("staticRootFolder")

	var Attributes.staticRootFolder: File by requiredKey(StaticRootFolderAttribute)

	private val FakeRootFolderAttribute = AttributeKey<File>("fakeRootFolder")

	var Attributes.fakeRootFolder: File by requiredKey(FakeRootFolderAttribute)

	data class CurrentUser(val id: String, val email: String)

	private val CurrentUserAttribute = AttributeKey<CurrentUser>("currentUser")

	/**
	 * Do not use directly. Prefer using shorthands on [ApplicationCall].
	 *
	 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.hasUser
	 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.userId
	 */
	var Attributes.currentUser: CurrentUser? by optionalKey(CurrentUserAttribute)

	@Suppress("unused") // not needed yet, ApplicationComponent is alive for the whole Application lifecycle
	private fun Application.registerDaggerForEachCall(dagger: ApplicationComponent) {
		environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
			call.attributes.put(DaggerComponentAttribute, dagger)
		}
	}
}

private fun <T : Any> requiredKey(attributeKey: AttributeKey<T>) =
	object : ReadWriteProperty<Attributes, T> {
		override fun getValue(thisRef: Attributes, property: KProperty<*>): T =
			thisRef[attributeKey]

		override fun setValue(thisRef: Attributes, property: KProperty<*>, value: T) {
			thisRef.put(attributeKey, value)
		}
	}

private fun <T : Any> optionalKey(attributeKey: AttributeKey<T>) =
	object : ReadWriteProperty<Attributes, T?> {
		override fun getValue(thisRef: Attributes, property: KProperty<*>): T? =
			thisRef.getOrNull(attributeKey)

		override fun setValue(thisRef: Attributes, property: KProperty<*>, value: T?) {
			if (value == null) {
				thisRef.remove(attributeKey)
			} else {
				thisRef.put(attributeKey, value)
			}
		}
	}
