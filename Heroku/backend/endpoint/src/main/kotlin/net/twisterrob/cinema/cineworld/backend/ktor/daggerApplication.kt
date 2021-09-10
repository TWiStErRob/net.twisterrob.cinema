package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.application.Application
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.dagger
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.app.DaggerApplicationComponent
import java.net.URI

fun Application.daggerApplication() = daggerApplication(DaggerApplicationComponent::builder)

internal fun <DaggerComponentBuilder : ApplicationComponent.Builder> Application.daggerApplication(
	createComponentBuilder: () -> DaggerComponentBuilder,
	initComponent: (DaggerComponentBuilder) -> Unit = { },
	componentReady: (ApplicationComponent) -> Unit = { }
) {
	val builder: DaggerComponentBuilder = createComponentBuilder()
	builder.application(this)
	builder.graphDBUri(getNeo4jUrl())
	builder.quickbookApiKey()
	initComponent(builder)
	val dagger = builder.build()
	componentReady(dagger)
	this.attributes.dagger = dagger

	dagger.controllers.register()
}

private fun getNeo4jUrl(): URI {
	val url = System.getenv()["NEO4J_URL"]
		?: error("NEO4J_URL environment variable must be defined (=neo4j+s://username:password@hostname:port).")
	return URI.create(url)
}
