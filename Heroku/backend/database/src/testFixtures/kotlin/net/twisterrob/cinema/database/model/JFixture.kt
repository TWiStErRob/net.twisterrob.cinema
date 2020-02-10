package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.customisation.Customisation
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.offsetDateTimeRealistic

fun validDBData(): Customisation = Customisation {
	it.applyCustomisation {
		add(offsetDateTimeRealistic())
		propertyOf(Cinema::class.java, "users", emptyList<User>())
		propertyOf(Cinema::class.java, "views", emptyList<View>())
		propertyOf(User::class.java, "cinemas", emptyList<Cinema>())
		propertyOf(User::class.java, "views", emptyList<View>())
	}
}
