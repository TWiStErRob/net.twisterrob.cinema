package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id

@Suppress("UnnecessaryAbstractClass") // Prevent usage as OGM model directly, has to be inherited.
abstract class BaseNode {

	@Id
	@GeneratedValue
	var graphId: Long? = null

	protected fun copyProperties(from: BaseNode) {
		graphId = from.graphId
	}
}
