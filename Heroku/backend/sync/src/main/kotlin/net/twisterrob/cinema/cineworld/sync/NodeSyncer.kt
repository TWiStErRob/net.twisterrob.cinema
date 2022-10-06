package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Historical
import java.time.OffsetDateTime
import javax.inject.Inject

typealias Creator<TFeed, TDB> = @JvmSuppressWildcards TFeed.(Feed) -> TDB
typealias Updater<DB, TFeed> = @JvmSuppressWildcards DB.(TFeed, Feed) -> Unit

class NodeSyncer<TFeed, TDB : Historical> @Inject constructor(
	private val toEntity: Creator<TFeed, TDB>,
	private val setFrom: Updater<TDB, TFeed>
) {

	fun update(feed: Feed, changes: SyncOperations<TDB, TFeed>, now: OffsetDateTime): SyncResults<TDB> {
		val createdNodes = createNodes(feed, changes.insert, now)
		val updatedNodes = updateNodes(feed, changes.update, now)
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

	private fun createNodes(feed: Feed, nodesToCreate: Collection<TFeed>, now: OffsetDateTime): List<TDB> =
		nodesToCreate
			.map { feedItem ->
				feedItem.toEntity(feed)
			}
			.onEach { it._created = now }

	private fun updateNodes(feed: Feed, nodesToUpdate: Map<TDB, TFeed>, now: OffsetDateTime): List<TDB> =
		nodesToUpdate
			.map { (db, feedItem) ->
				db.apply { setFrom(feedItem, feed) }
			}
			.onEach { it._updated = now }

	private fun deleteNodes(nodesToDelete: List<TDB>, now: OffsetDateTime): List<TDB> =
		nodesToDelete
			.onEach { it._deleted = now }
}
