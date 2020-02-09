package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.customisation.Customisation
import net.twisterrob.test.buildList

/**
 * Helper to let [JFixture] create an instance of [SyncResults].
 * For some reason it cannot resolve the generic instantiation of the class.
 */
inline fun <reified T> syncResults(): Customisation = Customisation {
	it.customise().lazyInstance(SyncResults::class.java) {
		SyncResults<T>(it.buildList(), it.buildList(), it.buildList(), it.buildList(), it.buildList())
	}
}
