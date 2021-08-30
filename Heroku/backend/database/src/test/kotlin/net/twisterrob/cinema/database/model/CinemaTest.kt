package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

class CinemaTest {

	private val fixture: JFixture = JFixture().applyCustomisation { add(validDBData()) }

	@Test fun `copy creates a full copy`() {
		val fixtCinema: Cinema = fixture.build()
		fixtCinema.views = fixture.buildList()
		fixtCinema.users = fixture.buildList()

		val result = fixtCinema.copy()

		assertThat(result, sameBeanAs(fixtCinema))
	}

	@Test fun `copy creates a shallow copy`() {
		val fixtCinema: Cinema = fixture.build()
		fixtCinema.views = fixture.buildList()
		fixtCinema.users = fixture.buildList()

		val result = fixtCinema.copy()

		result.views.forEachIndexed { index, view ->
			assertThat(view, sameInstance(fixtCinema.views.elementAt(index)))
		}
		result.users.forEachIndexed { index, user ->
			assertThat(user, sameInstance(fixtCinema.users.elementAt(index)))
		}
	}
}
