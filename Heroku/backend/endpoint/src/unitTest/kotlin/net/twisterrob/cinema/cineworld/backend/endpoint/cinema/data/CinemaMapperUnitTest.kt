package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.test.build
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import net.twisterrob.cinema.database.model.Cinema as DBCinema

@ExtendWith(ModelFixtureExtension::class)
class CinemaMapperUnitTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: CinemaMapper

	@BeforeEach fun setUp() {
		sut = CinemaMapper()
	}

	@Test fun `map a cinema`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinema = sut.map(fixtDBCinema)

		assertThat(cinema.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinema.name, equalTo(fixtDBCinema.name))
		assertThat(cinema.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinema.address, equalTo(fixtDBCinema.address))
		assertThat(cinema.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinema.cinemaUrl, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinema.created, equalTo(fixtDBCinema._created))
		assertThat(cinema.updated, equalTo(fixtDBCinema._updated))
		assertThat(cinema.className, equalTo(fixtDBCinema.className))
		assertThat(cinema.isFavorited, equalTo(false))
	}

	@Test fun `map a cinema with favorite`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinema = sut.map(mapOf(fixtDBCinema to true).entries.single())

		assertThat(cinema.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinema.name, equalTo(fixtDBCinema.name))
		assertThat(cinema.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinema.address, equalTo(fixtDBCinema.address))
		assertThat(cinema.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinema.cinemaUrl, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinema.created, equalTo(fixtDBCinema._created))
		assertThat(cinema.updated, equalTo(fixtDBCinema._updated))
		assertThat(cinema.className, equalTo(fixtDBCinema.className))
		assertThat(cinema.isFavorited, equalTo(true))
	}

	@Test fun `map a cinema with not favorite`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinema = sut.map(mapOf(fixtDBCinema to false).entries.single())

		assertThat(cinema.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinema.name, equalTo(fixtDBCinema.name))
		assertThat(cinema.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinema.address, equalTo(fixtDBCinema.address))
		assertThat(cinema.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinema.cinemaUrl, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinema.created, equalTo(fixtDBCinema._created))
		assertThat(cinema.updated, equalTo(fixtDBCinema._updated))
		assertThat(cinema.className, equalTo(fixtDBCinema.className))
		assertThat(cinema.isFavorited, equalTo(false))
	}
}
