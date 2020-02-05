package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import net.twisterrob.test.get
import net.twisterrob.test.set
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import java.net.URI
import java.time.OffsetDateTime

class FeedIntgTest {

	private fun loadFeed(fileName: String) =
		feedReader().readValue<Feed>(FeedIntgTest::class.java.getResourceAsStream("/$fileName"))

	@Test fun `read UK weekly film times XML`() {
		val feed = loadFeed("weekly_film_times.xml")

		assertNotNull(feed)
		feed.verifyAllAttributesAreValid()
		feed.verifyHasAllAttributes(SCREENING_TYPES + GENRES)
	}

	@Test fun `read IE weekly film times XML`() {
		val feed = loadFeed("weekly_film_times_ie.xml")

		assertNotNull(feed)
		feed.verifyAllAttributesAreValid()
		feed.verifyHasAllAttributes(SCREENING_TYPES)
	}

	@Ignore("Too much to ask")
	@Test fun `serialization is reversible`() {
		val fixture = JFixture()
		val fixtFeed = fixture.create(Feed::class.java).apply {
			cinemas.forEach { it["performances"] = emptyList<Feed.Screening>() }
			films.forEach { it["performances"] = emptyList<Feed.Screening>() }
			this["performances"] = (1..3).map {
				val performance = Feed.Screening(
					film = films.random(),
					cinema = cinemas.random(),
					url = fixture.create(URI::class.java),
					attributes = SCREENING_TYPES.random().code,
					time = fixture.create(OffsetDateTime::class.java)
				)
				performance.film.add("performances", performance)
				performance.cinema.add("performances", performance)
				return@map performance
			}
		}
		val serialized = feedReader().writeValueAsString(fixtFeed)
		println(serialized)
		val feed = feedReader().readValue<Feed>(serialized)
		assertEquals(fixtFeed, feed)
	}

	private fun <T> Any.add(fieldName: String, value: T) {
		val current: Iterable<T> = this[fieldName]
		this[fieldName] = current + value
	}

	private fun Feed.verifyHasAllAttributes(attributes: Set<Feed.Attribute>) {
		attributes.forEach {
			assertTrue("$it not found in ${this.attributes}", this.attributes.contains(it))
		}
	}

	private fun Feed.verifyAllAttributesAreValid() {
		this.attributes.forEach {
			assertNotNull(it.code)
			assertNotNull(it.title)
		}
	}

	companion object {

		private val SCREENING_TYPES = setOf(
			Feed.Attribute("2D", "2D"),
			Feed.Attribute("3D", "3D"),
			Feed.Attribute("4DX", "4DX"),
			Feed.Attribute("AC", "AC"),
			Feed.Attribute("AD", "AD"),
			Feed.Attribute("AUT", "AUT"),
			Feed.Attribute("Box", "Box"),
			Feed.Attribute("CH", "CH"),
			Feed.Attribute("CINB", "CINB"),
			Feed.Attribute("DBOX", "DBOX"),
			Feed.Attribute("EDU", "EDU"),
			Feed.Attribute("FEV", "FEV"),
			Feed.Attribute("IMAX", "IMAX"),
			Feed.Attribute("M4J", "M4J"),
			Feed.Attribute("MID", "MID"),
			Feed.Attribute("PRE", "PRE"),
			Feed.Attribute("QA", "QA"),
			Feed.Attribute("SC", "SC"),
			Feed.Attribute("Sen", "Sen"),
			Feed.Attribute("SKY", "SKY"),
			Feed.Attribute("SS", "SS"),
			Feed.Attribute("ST", "ST"),
			Feed.Attribute("STAR", "STAR"),
			Feed.Attribute("Strobe", "Strobe"),
			Feed.Attribute("TS", "TS"),
			Feed.Attribute("ViP", "ViP"),
			Feed.Attribute("VIP", "VIP")
		)

		private val GENRES = setOf(
			Feed.Attribute("gn:action", "Action"),
			Feed.Attribute("gn:animation", "Animation"),
			Feed.Attribute("gn:bollywood", "Bollywood"),
			Feed.Attribute("gn:comedy", "Comedy"),
			Feed.Attribute("gn:documentary", "Documentary"),
			Feed.Attribute("gn:drama", "Drama"),
			Feed.Attribute("gn:event-cinema", "Event Cinema"),
			Feed.Attribute("gn:family", "Family"),
			Feed.Attribute("gn:fantasy", "Fantasy"),
			Feed.Attribute("gn:hindi", "Hindi"),
			Feed.Attribute("gn:horror", "Horror"),
			Feed.Attribute("gn:live", "Live"),
			Feed.Attribute("gn:malayalam", "Malayalam"),
			Feed.Attribute("gn:movies-for-juniors", "Movies for Juniors"),
			Feed.Attribute("gn:musical", "Musical"),
			Feed.Attribute("gn:national-theatre", "National Theatre"),
			Feed.Attribute("gn:polish", "Polish"),
			Feed.Attribute("gn:punjabi", "Punjabi"),
			Feed.Attribute("gn:romance", "Romance"),
			Feed.Attribute("gn:sci-fi", "Sci-Fi"),
			Feed.Attribute("gn:tamil", "Tamil"),
			Feed.Attribute("gn:telugu", "Telugu"),
			Feed.Attribute("gn:theatre", "Theatre"),
			Feed.Attribute("gn:thriller", "Thriller"),
			Feed.Attribute("gn:unlimited-screening", "Unlimited Screening")
		)
	}
}
