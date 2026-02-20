package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.TimestampConverter
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.time.OffsetDateTime

/**
 * Nodes of this entity store temporal history.
 * When it was [_created], [_updated] and [_deleted].
 * This gives ability to diagnose issues, and recover for synchronization mishaps.
 */
@Suppress(
	"PropertyName",
	"ObjectPropertyNaming",
	"detekt.AbstractClassCanBeConcreteClass", // Prevent usage as OGM model directly, has to be inherited.
)
abstract class Historical : BaseNode() {

	/**
	 * Timestamp of when this entity was first inserted into the database.
	 */
	@Convert(TimestampConverter::class)
	@Property(name = "_created")
	lateinit var _created: OffsetDateTime

	/**
	 * Timestamp of when this entity was last updated in the database.
	 * `null` if it was never updated, only ever [_created] once upon a time.
	 */
	@Convert(TimestampConverter::class)
	@Property(name = "_updated")
	var _updated: OffsetDateTime? = null

	/**
	 * [Historical] entities don't get deleted, their [_deleted] property is set to the time of deletion.
	 * Queries that need up-to-date information will filter on this field.
	 * `null` means this entity is active and hasn't been deleted yet.
	 * It is not possible to see if an "un-deletion" happened.
	 */
	@Convert(TimestampConverter::class)
	@Property(name = "_deleted")
	var _deleted: OffsetDateTime? = null

	protected fun copyProperties(from: Historical) {
		super.copyProperties(from)
		_created = from._created
		_updated = from._updated
		_deleted = from._deleted
	}
}
