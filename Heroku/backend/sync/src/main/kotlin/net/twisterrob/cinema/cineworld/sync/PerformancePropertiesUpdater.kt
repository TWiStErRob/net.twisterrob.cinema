package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Performance

fun Performance.copyPropertiesFrom(
	feedPerformance: Feed.Performance,
	@Suppress("UNUSED_PARAMETER") feed: Feed
) {
	this.booking_url = feedPerformance.url
	// Reinterpret in UK's timezone.
	this.time = feedPerformance.date.atZoneSimilarLocal(Feed.DEFAULT_TIMEZONE)
}
