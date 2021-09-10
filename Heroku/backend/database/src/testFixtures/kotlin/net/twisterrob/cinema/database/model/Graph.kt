@file:Suppress("unused")

package net.twisterrob.cinema.database.model

import java.util.LinkedList
import java.util.Queue

private fun fixup(vararg entities: BaseNode) {
	@Suppress("UNUSED_VARIABLE")
	val visited: MutableSet<BaseNode> = mutableSetOf()
	val remaining: Queue<BaseNode> = LinkedList(entities.toList())
	while (remaining.isNotEmpty()) {
		val current = remaining.remove()
		fixupProperties(current)
		remaining.addAll(neightbors(current))
	}
}

private fun neightbors(entity: BaseNode): Collection<BaseNode> =
	when (entity) {
		is Cinema -> {
			entity.users + entity.views
		}

		else ->
			emptyList()
	}

private fun fixupProperties(entity: BaseNode) {
	when (entity) {
		is Cinema -> {
			entity.users.forEach { if (entity !in it.cinemas) it.cinemas.add(entity) }
			entity.views.forEach { it.atCinema = entity }
		}
	}
}
