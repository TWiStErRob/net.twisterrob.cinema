package net.twisterrob.cinema.cineworld.sync

data class SyncOperations<DB, Feed>(
	val insert: Collection<Feed>,
	val delete: Collection<DB>,
	val update: Map<DB, Feed>
)

fun <DB, Feed, Identity> calculateChanges(
	database: Iterable<DB>,
	databaseIdentity: (node: DB) -> Identity,
	feed: Iterable<Feed>,
	feedIdentity: (data: Feed) -> Identity
): SyncOperations<DB, Feed> {
	val dbByID = database.associateBy(databaseIdentity)
	val dbIDs = dbByID.keys
	val feedByID = feed.associateBy(feedIdentity)
	val feedIDs = feedByID.keys
	val newContent = feedByID.filterKeys { it !in dbIDs }
	val existingContent = dbByID.filterKeys { it in feedIDs }
	val deletedContent = dbByID.filterKeys { it !in feedIDs }
	return SyncOperations(
		insert = newContent.values,
		delete = deletedContent.values,
		update = existingContent.entries.associateBy({ it.value }, { feedByID.getValue(it.key) })
	)
}
