package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.Property
import java.time.OffsetDateTime

@Suppress("PropertyName")
abstract class Historical : BaseNode() {

	@Property(name = "_created")
	lateinit var _created: OffsetDateTime

	@Property(name = "_updated")
	var _updated: OffsetDateTime? = null

	@Property(name = "_deleted")
	var _deleted: OffsetDateTime? = null
}
