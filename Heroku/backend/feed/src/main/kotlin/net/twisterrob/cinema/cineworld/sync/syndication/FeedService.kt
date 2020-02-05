package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import javax.inject.Inject

class FeedService @Inject constructor(
) {

	fun getWeeklyFilmTimes() =
		getUKWeeklyFilmTimes() + getIrelandWeeklyFilmTimes()

	fun getUKWeeklyFilmTimes() =
		feedReader().readValue<Feed>(File("backend/sync/test/weekly_film_times.xml"))

	fun getIrelandWeeklyFilmTimes() =
		feedReader().readValue<Feed>(File("backend/sync/test/weekly_film_times_ie.xml"))
}

internal operator fun Feed.plus(other: Feed): Feed = Feed(
	// merge the attributes, keep only unique ones
	attributes = (this.attributes + other.attributes).distinctBy { it.code },
	// different country -> different cinemas
	cinemas = (this.cinemas + other.cinemas),
	// both countries play the same movies
	films = (this.films + other.films).distinctBy { it.id },
	// performances are separate by cinemas, see cinemas above
	performances = (this.performances + other.performances)
)
