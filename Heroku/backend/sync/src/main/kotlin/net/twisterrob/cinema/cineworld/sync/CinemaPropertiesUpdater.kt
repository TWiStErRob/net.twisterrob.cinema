package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Cinema

fun Cinema.copyPropertiesFrom(feedCinema: Feed.Cinema, @Suppress("UNUSED_PARAMETER") feed: Feed) {
	this.cineworldID = feedCinema.id
	this.name = feedCinema.name.replace("""^Cineworld """.toRegex(), "")
	this.postcode = feedCinema.postcode
	this.address = feedCinema.address
	this.telephone = feedCinema.phone
	this.cinema_url = feedCinema.url
	// TODO feed.serviceList
}
