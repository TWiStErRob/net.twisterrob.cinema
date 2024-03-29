package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ModelFixtureExtension::class)
class UserUnitTest {

	private lateinit var fixture: JFixture

	@Test fun `copy creates a full copy`() {
		val fixtFilm: Film = fixture.build()
		fixtFilm.views = fixture.buildList()

		val result = fixtFilm.copy()

		assertThat(result, sameBeanAs(fixtFilm))
	}

	@Test fun `copy creates a shallow copy`() {
		val fixtFilm: Film = fixture.build()
		fixtFilm.views = fixture.buildList()

		val result = fixtFilm.copy()

		result.views.forEachIndexed { index, view ->
			assertThat(view, sameInstance(fixtFilm.views.elementAt(index)))
		}
	}
}
