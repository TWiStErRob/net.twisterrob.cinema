package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.TimestampConverter
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.time.OffsetDateTime

@Suppress("PropertyName")
abstract class Historical : BaseNode() {

	@Convert(TimestampConverter::class)
	@Property(name = "_created")
	lateinit var _created: OffsetDateTime

	@Convert(TimestampConverter::class)
	@Property(name = "_updated")
	var _updated: OffsetDateTime? = null

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
