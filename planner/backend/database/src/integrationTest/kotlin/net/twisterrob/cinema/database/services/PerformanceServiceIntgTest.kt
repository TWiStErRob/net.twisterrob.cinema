package net.twisterrob.cinema.database.services

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.model.Performance
import net.twisterrob.cinema.database.model.inUTC
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.ogm.session.Session
import java.time.LocalDate
import java.time.LocalTime

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
class PerformanceServiceIntgTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: PerformanceService

	@BeforeEach fun setUp(session: Session) {
		sut = PerformanceService(session)
	}

	@Test fun `getActiveCinemas() ignores deleted cinemas and returns others`(session: Session) {
		val fixtToday: LocalDate = fixture.build()
		val fixtTomorrow: LocalDate = fixture.build()
		assertNotEquals(fixtToday, fixtTomorrow)
		val fixtCinemasFound: List<Cinema> = fixture.buildList(size = 3)
		val fixtCinemasIgnored: List<Cinema> = fixture.buildList(size = 3)
		val fixtFilmsFound: List<Film> = fixture.buildList(size = 3)
		val fixtFilmsIgnored: List<Film> = fixture.buildList(size = 3)
		fixtCinemasFound.forEach { it.inUTC() }
		fixtCinemasIgnored.forEach { it.inUTC() }
		fixtFilmsFound.forEach { it.inUTC() }
		fixtFilmsIgnored.forEach { it.inUTC() }
		fixtCinemasFound.forEach(session::save)
		fixtCinemasIgnored.forEach(session::save)
		fixtFilmsFound.forEach(session::save)
		fixtFilmsIgnored.forEach(session::save)
		session.clear()

		val performances = listOf(fixtToday, fixtTomorrow).flatMap { date ->
			(fixtCinemasFound + fixtCinemasIgnored).flatMap { cinema ->
				(fixtFilmsFound + fixtFilmsIgnored).map { film ->
					val fixtPerformance: Performance = fixture.build {
						this.screensFilm = film
						this.inCinema = cinema
						this.time = date.atTime(fixture.build<LocalTime>()).atZone(fixture.build())
					}
					session.save(fixtPerformance)
					fixtPerformance
				}
			}
		}
		session.clear()

		val expected = fixtCinemasFound.flatMap { cinema ->
			fixtFilmsFound.map { film ->
				performances.single {
					it.time.toLocalDate() == fixtToday && it.inCinema == cinema && it.screensFilm == film
				}
			}
		}

		val result =
			sut.getPerformances(fixtToday, fixtCinemasFound.map { it.cineworldID }, fixtFilmsFound.map { it.edi })
				.toList()

		assertThat(result.sortedBy { it.toString() }, sameBeanAs(expected.sortedBy { it.toString() }))
	}
}
