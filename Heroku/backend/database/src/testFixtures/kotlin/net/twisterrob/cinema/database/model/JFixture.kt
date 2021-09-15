package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.customisation.Customisation
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.offsetDateTimeRealistic

fun validDBData(): Customisation = Customisation { fixture ->
	fixture.applyCustomisation {
		// Generate proper random OffsetDateTime, otherwise all instances will be now().
		add(offsetDateTimeRealistic())

		// Stub out relationship collections otherwise JFixture runs into recursion.
		// Set everything to immutable empty list
		// to prevent sharing state between entities (propertyOf() is not lazy).
		// Then wire up anything that was created during fixturing.

		intercept(Cinema::class.java) { cinema -> cinema.graphId = null }
		propertyOf(Cinema::class.java, "views", emptyList<View>())
		intercept(Cinema::class.java) { cinema -> cinema.views.forEach { it.atCinema = cinema } }
		intercept(View::class.java) { view -> view.atCinema.views = mutableListOf(view) }

		intercept(Film::class.java) { film -> film.graphId = null }
		propertyOf(Film::class.java, "views", emptyList<View>())
		intercept(Film::class.java) { film -> film.views.forEach { it.watchedFilm = film } }
		intercept(View::class.java) { view -> view.watchedFilm.views = mutableListOf(view) }

		intercept(User::class.java) { user -> user.graphId = null }
		propertyOf(User::class.java, "views", emptyList<View>())
		intercept(User::class.java) { user -> user.views.forEach { it.userRef = user } }
		intercept(View::class.java) { view -> view.userRef.views = mutableListOf(view) }

		propertyOf(Cinema::class.java, "users", emptyList<User>())
		intercept(Cinema::class.java) { cinema -> cinema.users.forEach { it.cinemas = mutableListOf(cinema) } }
		propertyOf(User::class.java, "cinemas", emptyList<Cinema>())
		intercept(User::class.java) { user -> user.cinemas.forEach { it.users = mutableListOf(user) } }

		intercept(View::class.java) { view -> view.graphId = null }

		intercept(Performance::class.java) { view -> view.graphId = null }
	}
}
