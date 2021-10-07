package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.customisation.Customisation
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.java8TimeRealistic
import net.twisterrob.test.javaURIRealistic

fun validDBData(): Customisation = Customisation { fixture ->
	fixture.applyCustomisation {
		// Generate proper random OffsetDateTime, otherwise all instances will be now().
		add(java8TimeRealistic())
		add(javaURIRealistic())

		// Stub out relationship collections otherwise JFixture runs into recursion.
		// Set everything to immutable empty list
		// to prevent sharing state between entities (propertyOf() is not lazy).
		// Then replace the empty list with a mutable list, so that it can be used as normal.
		// Then wire up anything that was created during fixturing.

		propertyOf(Cinema::class.java, "views", emptyList<View>())
		propertyOf(Cinema::class.java, "users", emptyList<User>())
		intercept(Cinema::class.java) { cinema -> cinema.graphId = null }
		intercept(Cinema::class.java) { cinema -> cinema.views = mutableListOf() }
		intercept(Cinema::class.java) { cinema -> cinema.users = mutableListOf() }

		propertyOf(Film::class.java, "views", emptyList<View>())
		intercept(Film::class.java) { film -> film.graphId = null }
		intercept(Film::class.java) { film -> film.views = mutableListOf() }

		propertyOf(User::class.java, "views", emptyList<View>())
		propertyOf(User::class.java, "cinemas", emptyList<Cinema>())
		intercept(User::class.java) { user -> user.graphId = null }
		intercept(User::class.java) { user -> user.views = mutableListOf() }
		intercept(User::class.java) { user -> user.cinemas = mutableListOf() }

		intercept(View::class.java) { view -> view.graphId = null }
		intercept(View::class.java) { view -> view.watchedFilm.views = mutableListOf(view) }
		intercept(View::class.java) { view -> view.userRef.views = mutableListOf(view) }
		intercept(View::class.java) { view -> view.atCinema.views = mutableListOf(view) }

		intercept(Performance::class.java) { view -> view.graphId = null }
	}
}
