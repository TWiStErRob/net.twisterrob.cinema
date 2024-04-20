package net.twisterrob.cinema.database.model

import java.util.LinkedList
import java.util.Queue

fun fixup(vararg entities: BaseNode) {
	val visited: MutableSet<BaseNode> = mutableSetOf()
	val remaining: Queue<BaseNode> = LinkedList(entities.toList())
	while (remaining.isNotEmpty()) {
		val current = remaining.remove()
		fixupProperties(current)
		if (current in visited) continue
		remaining.addAll(neighbors(current))
		visited.add(current)
	}
}

private fun neighbors(entity: BaseNode): Collection<BaseNode> =
	when (entity) {
		is Cinema ->
			entity.users + entity.views
		is Film ->
			entity.views
		is View ->
			listOf(entity.watchedFilm, entity.atCinema, entity.userRef)
		is User ->
			entity.cinemas + entity.views
		else ->
			error("neighbors not implemented for ${entity}")
	}

private fun fixupProperties(entity: BaseNode) {
	@Suppress("OptionalWhenBraces")
	when (entity) {
		is Cinema -> {
			entity.users.forEach { it.cinemas.maybeAdd(entity) }
			entity.views.forEach { it.atCinema = entity }
		}

		is Film -> {
			entity.views.forEach { it.watchedFilm = entity }
		}

		is View -> {
			entity.userRef.views.maybeAdd(entity)
			entity.watchedFilm.views.maybeAdd(entity)
			entity.atCinema.views.maybeAdd(entity)
		}

		is User -> {
			entity.views.forEach { it.userRef = entity }
			entity.cinemas.forEach { it.users.maybeAdd(entity) }
		}

		else -> {
			error("fixupProperties not implemented for ${entity}")
		}
	}
}

private fun <T : BaseNode> MutableCollection<T>.maybeAdd(entity: T) {
	if (entity !in this) add(entity)
}
