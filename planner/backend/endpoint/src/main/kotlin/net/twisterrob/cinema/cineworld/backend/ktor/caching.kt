package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.http.CacheControl.MaxAge
import io.ktor.http.CacheControl.Visibility
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.plugins.cachingheaders.caching
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
