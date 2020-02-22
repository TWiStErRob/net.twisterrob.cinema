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

		val cinema = sut.map(fixtDBCinema)

		assertThat(cinema.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinema.name, equalTo(fixtDBCinema.name))
		assertThat(cinema.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinema.address, equalTo(fixtDBCinema.address))
		assertThat(cinema.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinema.cinema_url, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinema._created, equalTo(fixtDBCinema._created))
		assertThat(cinema._updated, equalTo(fixtDBCinema._updated))
		assertThat(cinema.`class`, equalTo(fixtDBCinema.className))
		assertThat(cinema.fav, equalTo(false))
	}

	@Test fun `map a cinema with favorite`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinema = sut.map(mapOf(fixtDBCinema to true).entries.single())

		assertThat(cinema.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinema.name, equalTo(fixtDBCinema.name))
		assertThat(cinema.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinema.address, equalTo(fixtDBCinema.address))
		assertThat(cinema.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinema.cinema_url, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinema._created, equalTo(fixtDBCinema._created))
		assertThat(cinema._updated, equalTo(fixtDBCinema._updated))
		assertThat(cinema.`class`, equalTo(fixtDBCinema.className))
		assertThat(cinema.fav, equalTo(true))
	}

	@Test fun `map a cinema with not favorite`() {
		val fixtDBCinema: DBCinema = fixture.build()

		val cinema = sut.map(mapOf(fixtDBCinema to false).entries.single())

		assertThat(cinema.cineworldID, equalTo(fixtDBCinema.cineworldID))
		assertThat(cinema.name, equalTo(fixtDBCinema.name))
		assertThat(cinema.postcode, equalTo(fixtDBCinema.postcode))
		assertThat(cinema.address, equalTo(fixtDBCinema.address))
		assertThat(cinema.telephone, equalTo(fixtDBCinema.telephone))
		assertThat(cinema.cinema_url, equalTo(fixtDBCinema.cinema_url.toString()))
		assertThat(cinema._created, equalTo(fixtDBCinema._created))
		assertThat(cinema._updated, equalTo(fixtDBCinema._updated))
		assertThat(cinema.`class`, equalTo(fixtDBCinema.className))
		assertThat(cinema.fav, equalTo(false))
	}
}
