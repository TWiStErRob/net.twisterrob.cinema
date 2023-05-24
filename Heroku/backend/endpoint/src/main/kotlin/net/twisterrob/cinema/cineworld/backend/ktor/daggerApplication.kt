package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.dagger
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.app.DaggerApplicationComponent
import net.twisterrob.cinema.cineworld.backend.app.FeatureToggles
import java.net.URI

fun Application.daggerApplication() {
	daggerApplication(DaggerApplicationComponent::builder)
}

internal fun <DaggerComponentBuilder : ApplicationComponent.Builder> Application.daggerApplication(
	createComponentBuilder: () -> DaggerComponentBuilder,
	initComponent: (DaggerComponentBuilder) -> Unit = { },
	componentReady: (ApplicationComponent) -> Unit = { }
) {
	val builder: DaggerComponentBuilder = createComponentBuilder()
	builder.application(this)
	builder.featureToggles(FeatureToggles(useQuickBook = false))
	builder.graphDBUri(getNeo4jUrl())
	builder.quickbookApiKey("9qfgpF7B")
	initComponent(builder)
	val dagger = builder.build()
	componentReady(dagger)
	this.attributes.dagger = dagger
	environment.monitor.subscribe(ApplicationStopped) { application ->
		application.attributes.dagger.httpClient.close()
	}

	dagger.controllers.register()
}

private fun getNeo4jUrl(): URI {
	val url = System.getenv()["NEO4J_URL"]
		?: error("NEO4J_URL environment variable must be defined (=neo4j+s://username:password@hostname:port).")
	return URI.create(url)
}
