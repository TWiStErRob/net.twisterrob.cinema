package net.twisterrob.cinema.database.services

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.database.model.View
import net.twisterrob.cinema.database.model.assertSameData
import net.twisterrob.cinema.database.model.inUTC
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.cinema.database.model.test.hasRelationship
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Label
import org.neo4j.ogm.session.Session
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
@TagIntegration
class ViewServiceIntgTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: ViewService

	@BeforeEach fun setUp(session: Session) {
		sut = ViewService(session)
	}

	@Test fun `addView returns each piece of the View`(session: Session) {
		val fixtCinema: Cinema = fixture.build()
		fixtCinema.inUTC()
		val fixtFilm: Film = fixture.build()
		fixtFilm.inUTC()
		val fixtUser: User = fixture.build()
		fixtUser.inUTC()
		val fixtTime: OffsetDateTime = fixture.build()
		session.save(fixtCinema)
		session.save(fixtFilm)
		session.save(fixtUser)

		val result =
			sut.addView(user = fixtUser.id, film = fixtFilm.edi, cinema = fixtCinema.cineworldID, time = fixtTime)

		assertNotNull(result); result!!
		@Suppress("LabeledExpression") // https://github.com/detekt/detekt/issues/5131
		// Wire up a View that is like the expected one and connect relationships.
		View().apply view@{
			graphId = result.graphId
			date = fixtTime.toInstant()
			atCinema = fixtCinema.apply { views = mutableSetOf(this@view) }
			watchedFilm = fixtFilm.apply { views = mutableSetOf(this@view) }
			userRef = fixtUser.apply { views = mutableSetOf(this@view) }
		}
		assertThat(result.atCinema, sameBeanAs(fixtCinema))
		assertThat(result.watchedFilm, sameBeanAs(fixtFilm))
		assertThat(result.userRef, sameBeanAs(fixtUser))
	}

	@Test fun `addView saves the right graph`(session: Session, graph: GraphDatabaseService) {
		val fixtCinema: Cinema = fixture.build()
		val fixtFilm: Film = fixture.build()
		val fixtUser: User = fixture.build()
		val fixtTime: OffsetDateTime = fixture.build()
		session.save(fixtCinema)
		session.save(fixtFilm)
		session.save(fixtUser)

		sut.addView(user = fixtUser.id, film = fixtFilm.edi, cinema = fixtCinema.cineworldID, time = fixtTime)

		graph.beginTx().use { tx ->
			val nodes = tx.allNodes.toList()
			assertThat(nodes, hasSize(4))
			val cinema = nodes.single { it.hasLabel(Label.label("Cinema")) }
			assertSameData(fixtCinema, cinema)
			val film = nodes.single { it.hasLabel(Label.label("Film")) }
			assertSameData(fixtFilm, film)
			val user = nodes.single { it.hasLabel(Label.label("User")) }
			assertSameData(fixtUser, user)
			val view = nodes.single { it.hasLabel(Label.label("View")) }
			assertThat(
				tx.allRelationships, containsInAnyOrder(
					hasRelationship(view, "AT", cinema),
					hasRelationship(view, "WATCHED", film),
					hasRelationship(user, "ATTENDED", view)
				)
			)
		}
	}

	@Test fun `addView fails if data is missing`() {
		val result = sut.addView(user = "missing", film = -1, cinema = -1, time = fixture.build())

		assertNull(result)
	}

	@Test fun `addView fails if cinema is missing`(session: Session) {
		val fixtFilm: Film = fixture.build()
		val fixtUser: User = fixture.build()
		val fixtTime: OffsetDateTime = fixture.build()
		session.save(fixtFilm)
		session.save(fixtUser)

		val result = sut.addView(user = fixtUser.id, film = fixtFilm.edi, cinema = -1, time = fixtTime)

		assertNull(result)
	}

	@Test fun `addView fails if film is missing`(session: Session) {
		val fixtCinema: Cinema = fixture.build()
		val fixtUser: User = fixture.build()
		val fixtTime: OffsetDateTime = fixture.build()
		session.save(fixtCinema)
		session.save(fixtUser)

		val result = sut.addView(user = fixtUser.id, film = -1, cinema = fixtCinema.cineworldID, time = fixtTime)

		assertNull(result)
	}

	@Test fun `addView fails if user is missing`(session: Session) {
		val fixtCinema: Cinema = fixture.build()
		val fixtFilm: Film = fixture.build()
		val fixtTime: OffsetDateTime = fixture.build()
		session.save(fixtFilm)
		session.save(fixtCinema)

		val result =
			sut.addView(user = "missing", film = fixtFilm.edi, cinema = fixtCinema.cineworldID, time = fixtTime)

		assertNull(result)
	}

	@Test fun `removeView deletes node and relationships`(session: Session, graph: GraphDatabaseService) {
		val fixtView: View = fixture.build()
		session.save(fixtView)

		sut.removeView(
			user = fixtView.userRef.id,
			film = fixtView.watchedFilm.edi,
			cinema = fixtView.atCinema.cineworldID,
			time = fixtView.date.atOffset(ZoneOffset.UTC)
		)

		graph.beginTx().use { tx ->
			val nodes = tx.allNodes.toList()
			assertThat(nodes, hasSize(3))
			val cinema = nodes.single { it.hasLabel(Label.label("Cinema")) }
			assertSameData(fixtView.atCinema, cinema)
			val film = nodes.single { it.hasLabel(Label.label("Film")) }
			assertSameData(fixtView.watchedFilm, film)
			val user = nodes.single { it.hasLabel(Label.label("User")) }
			assertSameData(fixtView.userRef, user)
			assertThat(tx.allRelationships.toList(), empty())
		}
	}
}
