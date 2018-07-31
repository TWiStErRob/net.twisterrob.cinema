package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity

@NodeEntity // TODEL prevent https://github.com/neo4j/neo4j-ogm/issues/437
// Side effects: all abstract classes show up as label, ugh!
abstract class BaseNode {

	@Id
	@GeneratedValue
	var graphId: Long? = null
		private set
}
