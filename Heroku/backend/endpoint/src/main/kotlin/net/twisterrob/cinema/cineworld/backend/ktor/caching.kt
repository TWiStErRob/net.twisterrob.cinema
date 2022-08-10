package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.http.CacheControl.MaxAge
import io.ktor.http.CacheControl.Visibility
import io.ktor.http.content.CachingOptions
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.caching
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.response.ApplicationSendPipeline
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.ktor.util.pipeline.PipelineContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

inline fun PipelineContext<Unit, ApplicationCall>.cached(
	caching: CachingOptions = defaultCacheOptions(application.environment.config.environment),
	block: PipelineContext<Unit, ApplicationCall>.() -> Unit
) {
	this.call.caching = caching
	block()
}

// TODO https://youtrack.jetbrains.com/issue/KTOR-457 (https://github.com/TWiStErRob/net.twisterrob.cinema/issues/190)
var ApplicationCall.caching: CachingOptions
	get() = error("Not possible to get current caching setup.")
	set(value) {
		this.response.pipeline.intercept(ApplicationSendPipeline.Render) {
			// This will be read by the default `install(CachingHeaders)` options: `options { it.caching }`,
			// and put among the response headers.
			// By definition of Render phase, it can be only OutgoingContent here.
			(subject as OutgoingContent).caching = value
		}
	}

fun defaultCacheOptions(environment: Env): CachingOptions {
	val cacheLength = if (environment == Env.PRODUCTION) 10.hours.inWholeSeconds else 0
	return CachingOptions(
		cacheControl = MaxAge(
			visibility = Visibility.Public,
			maxAgeSeconds = cacheLength.toInt()
		),
		expires = GMTDate() + cacheLength.seconds
	)
}
