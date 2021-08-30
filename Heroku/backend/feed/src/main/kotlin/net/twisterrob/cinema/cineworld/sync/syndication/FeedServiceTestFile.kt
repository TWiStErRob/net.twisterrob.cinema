package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import javax.inject.Inject

class FeedServiceTestFile(
	private val baseFolder: File
) : FeedService {

	@Inject constructor() : this(
		baseFolder = File("backend/sync/test/")
	)

	override fun getWeeklyFilmTimes(): Feed =
		getUKWeeklyFilmTimes() + getIrelandWeeklyFilmTimes()

	private fun getUKWeeklyFilmTimes(): Feed =
		feedReader().readValue(File(baseFolder, "weekly_film_times.xml"))

	private fun getIrelandWeeklyFilmTimes(): Feed =
		feedReader().readValue(File(baseFolder, "weekly_film_times_ie.xml"))
}
