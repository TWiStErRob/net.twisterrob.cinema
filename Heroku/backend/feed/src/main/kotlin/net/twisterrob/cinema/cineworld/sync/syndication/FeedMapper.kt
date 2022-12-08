package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import javax.inject.Inject

class FeedMapper @Inject constructor() {

	fun read(file: File): Feed =
		feedMapper().readValue(file)

	fun write(file: File, feed: Feed) {
		feedMapper().writeValue(file, feed)
	}
}
