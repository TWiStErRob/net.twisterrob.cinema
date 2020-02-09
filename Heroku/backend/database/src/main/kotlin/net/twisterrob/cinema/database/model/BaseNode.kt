package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id

abstract class BaseNode {

	@Id
	@GeneratedValue
	var graphId: Long? = null
		private set
}
