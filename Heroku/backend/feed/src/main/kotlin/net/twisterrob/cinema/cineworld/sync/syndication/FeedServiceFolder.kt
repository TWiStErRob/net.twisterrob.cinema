package net.twisterrob.cinema.cineworld.sync.syndication

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class FeedServiceFolder @AssistedInject constructor(
	@Assisted
	private val baseFolder: File,
	private val feedMapper: FeedMapper,
) : FeedService {

	override fun getWeeklyFilmTimes(): Feed =
		getUKWeeklyFilmTimes() + getIrelandWeeklyFilmTimes()

	private fun getUKWeeklyFilmTimes(): Feed =
		feedMapper.read(File(baseFolder, "weekly_film_times.xml"))

	private fun getIrelandWeeklyFilmTimes(): Feed =
		feedMapper.read(File(baseFolder, "weekly_film_times_ie.xml"))

	@AssistedFactory
	interface FeedServiceFolderFactory {

		fun create(baseFolder: File): FeedServiceFolder
	}
}
