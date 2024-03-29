package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.customisation.Customisation
import net.twisterrob.cinema.database.model.Historical
import net.twisterrob.test.build
import net.twisterrob.test.buildList

/**
 * Helper to let [JFixture] create an instance of [SyncResults].
 * For some reason it cannot resolve the generic instantiation of the class.
 * It is also required to set properties correctly according to [SyncResults.validate].
 */
inline fun <reified T : Historical> syncResults(): Customisation = Customisation { fixture ->
	fixture.customise().lazyInstance(SyncResults::class.java) {
		SyncResults(
			insert = fixture.buildList<T>().onEach { hist ->
				hist._updated = null
				hist._deleted = null
			},
			restore = fixture.buildList<T>().onEach { hist ->
				hist._deleted = null
			},
			delete = fixture.buildList<T>().onEach { hist ->
				if (fixture.build()) {
					hist._updated = null
				}
			},
			alreadyDeleted = fixture.buildList<T>().onEach { hist ->
				if (fixture.build()) {
					hist._updated = null
				}
			},
			update = fixture.buildList<T>().onEach { hist ->
				hist._deleted = null
			}
		)
	}
}
