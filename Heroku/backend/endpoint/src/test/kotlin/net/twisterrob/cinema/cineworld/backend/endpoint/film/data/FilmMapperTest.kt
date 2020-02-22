package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.offsetDateTimeRealistic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.twisterrob.cinema.database.model.Film as DBFilm

class FilmMapperTest {

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
		add(offsetDateTimeRealistic())
	}
	private lateinit var sut: FilmMapper

	@BeforeEach fun setUp() {
		sut = FilmMapper()
	}

	@Test fun `map a film`() {
		val fixtDBFilm: DBFilm = fixture.build()

		val film = sut.map(fixtDBFilm)

		assertThat(film.cineworldID, equalTo(fixtDBFilm.edi))
		assertThat(film.title, equalTo(fixtDBFilm.title))
		assertThat(film._created, equalTo(fixtDBFilm._created))
		assertThat(film._updated, equalTo(fixtDBFilm._updated))
		assertThat(film.`class`, equalTo(fixtDBFilm.className))
	}
}
