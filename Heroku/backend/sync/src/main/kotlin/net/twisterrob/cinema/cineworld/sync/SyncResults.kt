package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.database.model.Historical

@Suppress("DataClassContainsFunctions")
data class SyncResults<DB : Historical>(

	/**
	 * New items that need to be created in the database.
	 * @see Historical._updated stays the default `null`
	 * @see Historical._deleted stays the default `null`
	 */
	val insert: Collection<@JvmSuppressWildcards DB>,

	/**
	 * Items that have been deleted in the database, but now become active again.
	 * Their information has potentially changed and need saving in the database.
	 * @see Historical._updated has changed to "now"
	 * @see Historical._deleted has changed to `null`
	 */
	val restore: Collection<@JvmSuppressWildcards DB>,

	/**
	 * Items that were active in the database, but now they need to be deleted.
	 * @see Historical._updated has not changed, still existing value
	 *                          (could be `null` if deleted immediately after creation)
	 * @see Historical._deleted has changed to "now"
	 */
	val delete: Collection<@JvmSuppressWildcards DB>,

	/**
	 * Items that were already deleted in the database, and still are.
	 * No updates are made to these nodes.
	 * @see Historical._updated has not changed, still existing value
	 * @see Historical._deleted has not changed, still `null`
	 */
	val alreadyDeleted: Collection<@JvmSuppressWildcards DB>,

	/**
	 * Items that were and are active in the database.
	 * Their information has potentially changed and need saving in the database.
	 * @see Historical._updated has changed to "now"
	 * @see Historical._deleted has not changed, still `null`
	 */
	val update: Collection<@JvmSuppressWildcards DB>
) {

	init {
		validate()
	}

	fun validate() {
		insert.forEach(::checkCreated)
		insert.forEach(::checkNotUpdated)
		insert.forEach(::checkNotDeleted)
		restore.forEach(::checkCreated)
		restore.forEach(::checkUpdated)
		restore.forEach(::checkNotDeleted)
		delete.forEach(::checkCreated)
		delete.forEach(::checkMaybeUpdated)
		delete.forEach(::checkDeleted)
		alreadyDeleted.forEach(::checkCreated)
		alreadyDeleted.forEach(::checkMaybeUpdated)
		alreadyDeleted.forEach(::checkDeleted)
		update.forEach(::checkCreated)
		update.forEach(::checkUpdated)
		update.forEach(::checkNotDeleted)
	}

	private fun identity(it: DB) = "${it::class.java}[${it.graphId}]"

	/**
	 * External (to [it]/[DB]) version of `it::_created.isInitialized`.
	 */
	private fun checkCreated(it: DB) {
		@Suppress("SwallowedException") // We know exactly why and how the exception happens.
		try {
			it._created // lateinit, so need to access to check if it has been set
		} catch (ex: UninitializedPropertyAccessException) {
			check(false) { "${identity(it)} should be created." }
		}
	}

	private fun checkMaybeUpdated(@Suppress("UNUSED_PARAMETER") it: DB) {
		// Nothing to check null/non-null both valid.
	}

	private fun checkUpdated(it: DB) =
		check(it._updated != null) { "${identity(it)} should be updated." }

	private fun checkNotUpdated(it: DB) =
		check(it._updated == null) { "${identity(it)} should not be updated, but was updated at ${it._updated}." }

	private fun checkDeleted(it: DB) =
		check(it._deleted != null) { "${identity(it)} should be deleted." }

	private fun checkNotDeleted(it: DB) =
		check(it._deleted == null) { "${identity(it)} should not be deleted, but was deleted at ${it._deleted}." }
}
