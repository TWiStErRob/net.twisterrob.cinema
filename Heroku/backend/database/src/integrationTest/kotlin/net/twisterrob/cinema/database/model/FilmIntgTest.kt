package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.neo4j.ogm.TimestampConverter
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.emptyIterable
import net.twisterrob.test.neo4j.mockito.hasLabels
import net.twisterrob.test.that
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import java.time.ZoneOffset

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
@TagIntegration
class FilmIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new film can be loaded back and contains all data`(session: Session) {
		val fixtFilm: Film = fixture.build()
		val expected = fixtFilm.copy().apply {
			graphId = 0
			inUTC()
		}
		session.save(fixtFilm, -1)
		session.clear() // drop cached Film objects, start fresh

		val films = session.loadAll<Film>(depth = -1)

		assertThat(films, hasSize(1))
		assertThat(films.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `new film contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtFilm: Film = fixture.build()
		session.save(fixtFilm, -1)
		session.clear() // drop cached Film objects, start fresh

		graph.beginTx().use { tx ->
			val films = tx.allNodes.toList()

			assertThat(films, hasSize(1))
			val film = films.single()
			assertSameData(fixtFilm, film)
			assertThat(film.relationships, emptyIterable())
		}
	}

	@Test fun `film can be undeleted`(session: Session) {
		val fixtFilm: Film = fixture.build()
		val expected = fixtFilm.copy().apply {
			graphId = 0
			inUTC()
			_deleted = null
		}
		session.save(fixtFilm, -1)
		session.clear() // drop cached Film objects, start fresh

		fixtFilm._deleted = null
		session.save(fixtFilm, -1)
		session.clear() // drop cached Film objects, start fresh

		val films = session.loadAll<Film>(depth = -1)

		assertThat(films, hasSize(1))
		assertThat(films.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `empty film fails to save`(session: Session) {
		val film = Film()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(film, -1)
		}
	}
}

fun Film.inUTC() {
	_created = _created.withOffsetSameInstant(ZoneOffset.UTC)
	_updated = _updated?.withOffsetSameInstant(ZoneOffset.UTC)
	_deleted = _deleted?.withOffsetSameInstant(ZoneOffset.UTC)
	release = release.withOffsetSameInstant(ZoneOffset.UTC)
}

fun assertSameData(expected: Film, actual: Node) {
	assertAll {
		that("labels", actual, hasLabels("Film"))
		@Suppress("DEPRECATION", "removal") // TODEL https://github.com/neo4j/neo4j-ogm/issues/924
		that("id", actual.id, equalTo(expected.graphId))
		val expectedProperties = mapOf<String, Any?>(
			"_created" to TimestampConverter().toGraphProperty(expected._created),
			"_updated" to TimestampConverter().toGraphProperty(expected._updated),
			"_deleted" to TimestampConverter().toGraphProperty(expected._deleted),
			"class" to expected.className,
			"edi" to expected.edi,
			"cineworldID" to expected.cineworldID,
			"cineworldInternalID" to expected.cineworldInternalID,

			"title" to expected.title,
			"originalTitle" to expected.originalTitle,

			"advisory" to expected.advisory,
			"classification" to expected.classification,
			"cert" to expected.cert,
			"actors" to expected.actors,
			"director" to expected.director,
			"imax" to expected.isIMAX,
			"3D" to expected.is3D,

			"runtime" to expected.runtime,
			"weighted" to expected.weighted,

			"slug" to expected.slug,
			"group" to expected.group,
			"format" to expected.format,
			"still_url" to expected.still_url,
			"film_url" to expected.film_url,
			"poster_url" to expected.poster_url,
			"poster" to expected.poster,
			"trailer" to expected.trailer,

			"release" to TimestampConverter().toGraphProperty(expected.release),

			"categories" to expected.categories.toTypedArray()
		)
		that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
	}
}
