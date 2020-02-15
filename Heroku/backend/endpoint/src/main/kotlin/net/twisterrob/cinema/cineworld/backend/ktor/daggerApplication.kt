package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.application.Application
import io.ktor.routing.Routing
import io.ktor.routing.RoutingApplicationCall
import io.ktor.util.AttributeKey
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.app.DaggerApplicationComponent
import net.twisterrob.cinema.cineworld.backend.ktor.ApplicationAttributes.dagger

fun Application.daggerApplication() = daggerApplication(DaggerApplicationComponent::builder)

internal fun <DaggerComponentBuilder : ApplicationComponent.Builder> Application.daggerApplication(
	createComponentBuilder: () -> DaggerComponentBuilder,
	componentReady: (ApplicationComponent) -> Unit = { },
	initComponent: (DaggerComponentBuilder) -> Unit = { }
) {
	val builder: DaggerComponentBuilder = createComponentBuilder()
	builder.application(this)
	builder.graphDBUri()
	initComponent(builder)
	val dagger = builder.build()
	componentReady(dagger)
	this.dagger = dagger

	dagger.controllers.register()
}

/**
 * Attributes available for the [Application] related this app.
 *
 * **Note**: For most use cases this shouldn't be necessary, as everything is Dagger [Injected][javax.inject.Inject] automatically.
 */
object ApplicationAttributes {

	private val DaggerComponent = AttributeKey<ApplicationComponent>("dagger")

	var Application.dagger: ApplicationComponent
		get() = attributes[DaggerComponent]
		set(value) = attributes.put(DaggerComponent, value)

	@Suppress("unused") // not needed yet, ApplicationComponent is alive for the whole Application lifecycle
	private fun Application.registerDaggerForEachCall(dagger: ApplicationComponent) {
		environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
			call.attributes.put(DaggerComponent, dagger)
		}
	}
}
