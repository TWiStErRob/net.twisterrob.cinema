package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Performance

fun Performance.copyPropertiesFrom(feedPerformance: Feed.Performance, @Suppress("UNUSED_PARAMETER") feed: Feed) {
	this.booking_url = feedPerformance.url
	this.time = feedPerformance.time.toZonedDateTime()
}
