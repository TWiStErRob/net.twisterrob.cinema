package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.GENRES
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.SCREENING_TYPES
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.set
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@TagIntegration
class FeedIntgTest {

	private fun loadFeed(fileName: String): Feed {
		val resource = "/$fileName"
		val stream = FeedIntgTest::class.java.getResourceAsStream(resource)
			?: error("Cannot find $resource near ${FeedIntgTest::class.java}")
		return feedReader().readValue(stream)
	}

	@Test fun `read UK weekly film times XML`() {
		val feed = loadFeed("weekly_film_times.xml")

		assertNotNull(feed)
		feed.sanityCheck()
		feed.verifyHasAllAttributes(SCREENING_TYPES + GENRES)
	}

	@Test fun `read IE weekly film times XML`() {
		val feed = loadFeed("weekly_film_times_ie.xml")

		assertNotNull(feed)
		feed.sanityCheck()
		feed.verifyHasAllAttributes(SCREENING_TYPES)
	}

	@Test fun `serialization is reversible`() {
		val fixture = JFixture()
		val fixtFeed = fixture.build<Feed>().apply {
			this["_films"] = fixture.buildList<Feed.Film>()
			this["_cinemas"] = fixture.buildList<Feed.Cinema>()
			this["_performances"] = (0..2).map {
				Feed.Performance(
					film = films.random(),
					cinema = cinemas.random(),
					url = fixture.build(),
					attributes = SCREENING_TYPES.random().code,
					date = fixture.build<OffsetDateTime>().truncatedTo(ChronoUnit.SECONDS),
				)
			}
			sanityCheck()
		}
		val serialized = feedReader().writeValueAsString(fixtFeed)
		val feed = feedReader().readValue<Feed>(serialized)
		assertEquals(fixtFeed, feed)
	}
}
