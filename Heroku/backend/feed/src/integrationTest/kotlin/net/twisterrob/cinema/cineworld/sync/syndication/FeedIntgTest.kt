package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.GENRES
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.SCREENING_TYPES
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.set
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class FeedIntgTest {

	private fun loadFeed(fileName: String): Feed {
		val resource = "/$fileName"
		val stream = FeedIntgTest::class.java.getResourceAsStream(resource)
			?: error("Cannot find $resource near ${FeedIntgTest::class.java}")
		return feedMapper().readValue(stream)
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

	@Suppress("LongMethod") // THe constant shouldn't matter, but it needs all the local variables.
	@Test fun `write feed XML`() {
		val fixture = JFixture()
		val fixtCinema = fixture.build<Feed.Cinema>()
		val fixtFilm = fixture.build<Feed.Film>()
		val fixtPerformance = Feed.Performance(
			film = fixtFilm,
			cinema = fixtCinema,
			url = fixture.build(),
			date = fixture.build<OffsetDateTime>().truncatedTo(ChronoUnit.SECONDS),
			attributes = fixture.build(),
		)

		val fixtAttribute = Feed.Attribute(fixtPerformance.attributes, fixture.build())
		val fixtFeed = Feed(
			attributes = listOf(fixtAttribute),
			performances = listOf(fixtPerformance),
		)
		val serialized = feedMapper().writeValueAsString(fixtFeed)
		@Language("xml")
		val expected = """
			<feed>
			  <attributes>
			    <attribute code="${fixtAttribute.code}">${fixtAttribute.title}</attribute>
			  </attributes>
			  <cinemas>
			    <cinema id="${fixtCinema.id}">
			      <url>${fixtCinema.url}</url>
			      <name>${fixtCinema.name}</name>
			      <address>${fixtCinema.address}</address>
			      <postcode>${fixtCinema.postcode}</postcode>
			      <phone>${fixtCinema.phone}</phone>
			      <services>${fixtCinema.services}</services>
			    </cinema>
			  </cinemas>
			  <films>
			    <film id="${fixtFilm.id}">
			      <title>${fixtFilm.title}</title>
			      <url>${fixtFilm.url}</url>
			      <classification>${fixtFilm.classification}</classification>
			      <releaseDate>${fixtFilm.releaseDate}T00:00:00Z</releaseDate>
			      <runningTime>${fixtFilm.runningTime}</runningTime>
			      <director>${fixtFilm.director}</director>
			      <cast>${fixtFilm.cast}</cast>
			      <synopsis>${fixtFilm.synopsis}</synopsis>
			      <posterUrl>${fixtFilm.posterUrl}</posterUrl>
			      <reasonToSee>${fixtFilm.reasonToSee}</reasonToSee>
			      <attributes>${fixtFilm.attributes}</attributes>
			      <trailerUrl>${fixtFilm.trailerUrl}</trailerUrl>
			    </film>
			  </films>
			  <performances>
			    <screening film="${fixtFilm.id}" cinema="${fixtCinema.id}">
			      <url>${fixtPerformance.url}</url>
			      <date>${fixtPerformance.date}</date>
			      <attributes>${fixtPerformance.attributes}</attributes>
			    </screening>
			  </performances>
			</feed>
		""".trimIndent()
		assertEquals(expected + "\n", serialized.replace(System.lineSeparator(), "\n"))
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
		val serialized = feedMapper().writeValueAsString(fixtFeed)
		val feed = feedMapper().readValue<Feed>(serialized)
		val expected = fixtFeed.copy(
			_performances = fixtFeed.performances.map {
				it.copy(
					// If test is executed in a different timezone or during DST, it fails.
					date = it.date.withOffsetSameInstant(ZoneOffset.UTC)
				)
			}
		)
		assertEquals(expected, feed)
	}
}
