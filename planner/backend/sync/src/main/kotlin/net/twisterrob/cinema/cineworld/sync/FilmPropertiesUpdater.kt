package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Film
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

fun Film.copyPropertiesFrom(feedFilm: Feed.Film, feed: Feed) {
	this.edi = feedFilm.id
	this.title = formatTitle(feedFilm.title, feedFilm.attributeList)
	this.originalTitle = feedFilm.title
	this.director = feedFilm.director
	this.actors = feedFilm.cast
	this.film_url = feedFilm.url
	this.slug = feedFilm.url.path.substringAfterLast("/")
	this.is3D = "3D" in feedFilm.attributeList
	this.isIMAX = "IMAX" in feedFilm.attributeList
	this.format = findFormat(feedFilm.attributeList)
	this.poster_url = feedFilm.posterUrl
	this.runtime = feedFilm.runningTime.toLong()
	this.trailer = feedFilm.trailerUrl
	this.cert = feedFilm.classification
	this.classification = feedFilm.classification
	this.release = ukMidnight(feedFilm.releaseDate)
	this.categories = feedFilm.attributeList
		.asSequence()
		.filter { it.startsWith("gn:") }
		.map { attr -> feed.attributes.single { it.code == attr } }
		.map { it.title }
		.toList()
}

private fun ukMidnight(date: LocalDate): OffsetDateTime {
	val ukZone = Feed.DEFAULT_TIMEZONE.rules.getOffset(date.atStartOfDay())
	return date.atTime(LocalTime.of(0, 0).atOffset(ukZone))
}

private fun formatTitle(title: String, attributeList: List<String>): String {
	val format = attributeList
		.asSequence()
		.filterNot { it.startsWith("gn:") }
		.filterNot { it == "2D" }
		.sorted()
		.joinToString(separator = ", ")

	// Ignore (2D) in front coming from feed, and [IMAX, 3D, AD] in the end coming from generator so import is idempotent.
	// https://regex101.com/r/NWTzLC/1
	@Suppress("RegExpRedundantEscape")
	val noFormatTitle = Regex("""^(?:\(.*?\) )?(.*?)( \[[^\[\]]*\])?${'$'}""")
		.find(title)
		?.let { it.groupValues[1] }
		?: title

	// Make sure we don't get "Title []"
	return if (format.isNotEmpty()) "${noFormatTitle} [${format}]" else noFormatTitle
}

private fun findFormat(attributeList: List<String>): String =
	when {
		"IMAX" in attributeList ->
			when {
				"3D" in attributeList -> "IMAX3D"
				"2D" in attributeList -> "IMAX2D"
				else -> "IMAX"
			}
		"3D" in attributeList -> "3D" // could be 4DX too
		"2D" in attributeList -> "2D"
		else -> ""
	}
