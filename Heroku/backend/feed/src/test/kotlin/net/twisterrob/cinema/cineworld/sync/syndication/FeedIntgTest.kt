package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.GENRES
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.SCREENING_TYPES
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.get
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
			cinemas.forEach { it["performances"] = emptyList<Feed.Performance>() }
			films.forEach { it["performances"] = emptyList<Feed.Performance>() }
			this["_performances"] = (0..2).map {
				@Suppress("NestedScopeFunctions") // Ensure created Performance object is correct before escaping scope.
				Feed.Performance(
					film = films.random(),
					cinema = cinemas.random(),
					url = fixture.build(),
					attributes = SCREENING_TYPES.random().code,
					date = fixture.build<OffsetDateTime>().truncatedTo(ChronoUnit.SECONDS),
				).apply {
					film.add("performances", this)
					cinema.add("performances", this)
				}
			}
		}
		val serialized = feedReader().writeValueAsString(fixtFeed)
		val feed = feedReader().readValue<Feed>(serialized)
		assertEquals(fixtFeed, feed)
	}

	private fun <T> Any.add(fieldName: String, value: T) {
		val current: Iterable<T> = this[fieldName]
		this[fieldName] = current + value
	}
}
