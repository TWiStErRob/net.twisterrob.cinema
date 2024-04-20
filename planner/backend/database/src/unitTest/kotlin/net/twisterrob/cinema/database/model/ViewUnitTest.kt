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
class ViewUnitTest {

	private lateinit var fixture: JFixture

	@Test fun `copy creates a full copy`() {
		val fixtView: View = fixture.build()
		fixtView.userRef.views = fixture.buildList()
		fixtView.watchedFilm.views = fixture.buildList()
		fixtView.atCinema.views = fixture.buildList()

		val result = fixtView.copy()

		assertThat(result, sameBeanAs(fixtView))
	}

	@Test fun `copy creates a shallow copy`() {
		val fixtView: View = fixture.build()

		val result = fixtView.copy()

		assertThat(result.userRef, sameInstance(fixtView.userRef))
		assertThat(result.watchedFilm, sameInstance(fixtView.watchedFilm))
		assertThat(result.atCinema, sameInstance(fixtView.atCinema))
	}
}
