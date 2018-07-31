package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.database.model.Historical
import java.time.OffsetDateTime
import javax.inject.Inject

data class SyncResults<DB>(
	val insert: Collection<DB>,
	val restore: Collection<DB>,
	val delete: Collection<DB>,
	val alreadyDeleted: Collection<DB>,
	val update: Collection<DB>
)

typealias Creator<Feed, DB> = (@JvmSuppressWildcards Feed) -> @JvmSuppressWildcards DB
typealias Updater<DB, Feed> = (@JvmSuppressWildcards DB, @JvmSuppressWildcards Feed) -> Unit

class NodeSyncer<TFeed, DB : Historical> @Inject constructor(
	private val entityFactory: Creator<TFeed, DB>,
	private val updateFromFeed: Updater<DB, TFeed>
) {

	fun update(changes: SyncOperations<DB, TFeed>, now: OffsetDateTime): SyncResults<DB> {

		val createdNodes = createNodes(changes.insert, now)
		val updatedNodes = updateNodes(changes.update, now)
		val (onlyUpdated, restored) = updatedNodes.partition { it._deleted == null }
		restored.onEach { it._deleted = null }

		val (nodesToDelete, nodesAlreadyDeleted) = changes.delete.partition { it._deleted == null }
		val deletedNodes = deleteNodes(nodesToDelete, now)

		return SyncResults(
			insert = createdNodes,
			update = onlyUpdated,
			restore = restored,
			delete = deletedNodes,
			alreadyDeleted = nodesAlreadyDeleted
		)
	}

	private fun createNodes(nodesToCreate: Collection<TFeed>, now: OffsetDateTime): List<DB> =
		nodesToCreate
			.map {
				entityFactory(it).apply {
					updateFromFeed(this, it)
					_created = now
				}
			}

	private fun updateNodes(nodesToUpdate: Map<DB, TFeed>, now: OffsetDateTime): List<DB> =
		nodesToUpdate
			.map { (db, feed) ->
				updateFromFeed(db, feed)
				db
			}
			.onEach { it._updated = now }

	private fun deleteNodes(nodesToDelete: List<DB>, now: OffsetDateTime): List<DB> =
		nodesToDelete
			.onEach { it._deleted = now }
}
