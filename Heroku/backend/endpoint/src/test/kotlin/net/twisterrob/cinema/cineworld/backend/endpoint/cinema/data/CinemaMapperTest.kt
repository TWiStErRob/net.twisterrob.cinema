package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.offsetDateTimeRealistic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.twisterrob.cinema.database.model.Cinema as DBCinema

class CinemaMapperTest {

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
		add(offsetDateTimeRealistic())
	}
	private lateinit var sut: CinemaMapper

	@BeforeEach fun setUp() {
		sut = CinemaMapper()
	}

	@Test fun `map a cinema`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinemas = sut.map(fixtDBCinema)

		assertThat(cinemas.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinemas.name, equalTo(fixtDBCinema.name))
		assertThat(cinemas.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinemas.address, equalTo(fixtDBCinema.address))
		assertThat(cinemas.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinemas.cinema_url, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinemas._created, equalTo(fixtDBCinema._created))
		assertThat(cinemas._updated, equalTo(fixtDBCinema._updated))
		assertThat(cinemas.`class`, equalTo(fixtDBCinema.className))
		assertThat(cinemas.fav, equalTo(false))
	}

	@Test fun `map a cinema with favorite`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinemas = sut.map(mapOf(fixtDBCinema to true).entries.single())

		assertThat(cinemas.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinemas.name, equalTo(fixtDBCinema.name))
		assertThat(cinemas.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinemas.address, equalTo(fixtDBCinema.address))
		assertThat(cinemas.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinemas.cinema_url, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinemas._created, equalTo(fixtDBCinema._created))
		assertThat(cinemas._updated, equalTo(fixtDBCinema._updated))
		assertThat(cinemas.`class`, equalTo(fixtDBCinema.className))
		assertThat(cinemas.fav, equalTo(true))
	}

	@Test fun `map a cinema with not favorite`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinemas = sut.map(mapOf(fixtDBCinema to false).entries.single())

		assertThat(cinemas.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinemas.name, equalTo(fixtDBCinema.name))
		assertThat(cinemas.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinemas.address, equalTo(fixtDBCinema.address))
		assertThat(cinemas.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinemas.cinema_url, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinemas._created, equalTo(fixtDBCinema._created))
		assertThat(cinemas._updated, equalTo(fixtDBCinema._updated))
		assertThat(cinemas.`class`, equalTo(fixtDBCinema.className))
		assertThat(cinemas.fav, equalTo(false))
	}
}
