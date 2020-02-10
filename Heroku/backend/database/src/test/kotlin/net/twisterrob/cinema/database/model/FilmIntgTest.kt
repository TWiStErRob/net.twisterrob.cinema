package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.test.build
import net.twisterrob.test.emptyIterable
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Label.label
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll

@ExtendWith(ModelIntgTestExtension::class)
class FilmIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new film can be loaded back and contains all data`(session: Session) {
		val fixtFilm: Film = fixture.build()
		session.save(fixtFilm, -1)
		session.clear() // drop cached Film objects, start fresh

		val films = session.loadAll<Film>(depth = -1)

		assertThat(films, hasSize(1))
		assertThat(films.elementAt(0), sameBeanAs(fixtFilm))
	}

	@Test fun `new film contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtFilm: Film = fixture.build()
		session.save(fixtFilm, -1)
		session.clear() // drop cached Film objects, start fresh

		graph.beginTx().use {
			val films = graph.allNodes.toList()

			assertThat(films, hasSize(1))
			val film = films.elementAt(0)
			assertThat(film.labels.toSet(), equalTo(setOf(label("Film"))))
			assertThat(film.id, equalTo(fixtFilm.graphId))
			val expectedProperties = mapOf<String, Any?>(
				"_created" to fixtFilm._created.toString(),
				"_updated" to fixtFilm._updated.toString(),
				"_deleted" to fixtFilm._deleted.toString(),
				"class" to fixtFilm.className,
				"edi" to fixtFilm.edi,
				"cineworldID" to fixtFilm.cineworldID,
				"cineworldInternalID" to fixtFilm.cineworldInternalID,

				"title" to fixtFilm.title,
				"originalTitle" to fixtFilm.originalTitle,

				"advisory" to fixtFilm.advisory,
				"classification" to fixtFilm.classification,
				"cert" to fixtFilm.cert,
				"actors" to fixtFilm.actors,
				"director" to fixtFilm.director,
				"imax" to fixtFilm.imax,
				"3D" to fixtFilm.`3D`,

				"runtime" to fixtFilm.runtime,
				"weighted" to fixtFilm.weighted,

				"slug" to fixtFilm.slug,
				"group" to fixtFilm.group,
				"format" to fixtFilm.format,
				"still_url" to fixtFilm.still_url,
				"film_url" to fixtFilm.film_url,
				"poster_url" to fixtFilm.poster_url,
				"poster" to fixtFilm.poster,
				"trailer" to fixtFilm.trailer,

				"release" to fixtFilm.release.toString(),

				"categories" to fixtFilm.categories.toTypedArray()
			)
			assertThat(film.allProperties.toSortedMap(), equalTo(expectedProperties.toSortedMap()))
			assertThat(film.relationships, emptyIterable())
		}
	}
}
