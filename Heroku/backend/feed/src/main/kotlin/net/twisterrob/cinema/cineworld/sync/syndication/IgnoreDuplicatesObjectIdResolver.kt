package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.annotation.ObjectIdGenerator
import com.fasterxml.jackson.annotation.ObjectIdResolver
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(IgnoreDuplicatesObjectIdResolver::class.java)

internal class IgnoreDuplicatesObjectIdResolver : ObjectIdResolver {

	private val items: MutableMap<ObjectIdGenerator.IdKey, Any> = mutableMapOf()

	override fun bindItem(id: ObjectIdGenerator.IdKey, ob: Any) {
		val existing: Any? = resolveId(id)
		if (existing == ob) {
			// Same as SimpleObjectIdResolver.
			return
		}
		if (existing != null) {
			// From SimpleObjectIdResolver, but don't abort, ignore.
			LOG.warn("Already had POJO for id (" + id.key.javaClass.name + ") [" + id + "]")
			return
		}
		items[id] = ob
	}

	override fun resolveId(id: ObjectIdGenerator.IdKey): Any? =
		items[id]

	override fun canUseFor(resolverType: ObjectIdResolver): Boolean =
		resolverType.javaClass == javaClass

	override fun newForDeserialization(context: Any?): ObjectIdResolver =
		IgnoreDuplicatesObjectIdResolver()
}
