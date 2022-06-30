package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.routing.RoutingResolveTrace
import javax.inject.Inject

class RouteResolveTraceFormatter @Inject constructor() {

	/**
	 * Example:
	 * ```
	 * Trace for [film]
	 * /, segment:0 -> SUCCESS @ /
	 *   /favicon.ico, segment:0 -> FAILURE "Selector didn't match" @ /favicon.ico
	 *   /cinema, segment:0 -> FAILURE "Selector didn't match" @ /cinema
	 *   /film, segment:1 -> SUCCESS @ /film
	 *   /performance, segment:0 -> FAILURE "Selector didn't match" @ /performance
	 * Matched routes:
	 *   "" -> "<slash>" -> "{...}" -> "(method:GET)"
	 *   "" -> "film" -> "[cinemaIDs]" -> "[date]" -> "(method:GET)"
	 * Route resolve result:
	 *   SUCCESS; Parameters [cinemaIDs=[1], date=[20210902]] @ /film/[cinemaIDs]/[date]/(method:GET)
	 * ```
	 */
	fun formatFull(trace: RoutingResolveTrace): String =
		trace.buildText()

	/**
	 * Example:
	 * ```
	 * Trace for [film]: [hidden, enable trace logging]; Route resolve result: SUCCESS; Parameters [cinemaIDs=[1], date=[20210902]] @ /film/[cinemaIDs]/[date]/(method:GET)
	 * ```
	 */
	fun formatSimple(trace: RoutingResolveTrace): String =
		buildString {
			val text = trace.buildText()
			val traceHeader = text.lineSequence().first()
			val resolveResult = text.substringAfterLast("Route resolve result:\n  ").trim()
			append(traceHeader).append(": [hidden, enable trace logging];")
			append(" ")
			append("Route resolve result: ").append(resolveResult)
		}
}
