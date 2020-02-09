package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.customisation.Customisation

fun validDBData(): Customisation = Customisation {
	it.customise().propertyOf(Cinema::class.java, "views", emptyList<View>())
	it.customise().propertyOf(Cinema::class.java, "users", emptyList<View>())
}
