@file:Suppress("RemoveCurlyBracesFromTemplate", "DEPRECATION_ERROR")

package net.twisterrob.test

import java.lang.reflect.Field

inline operator fun <reified T : Any?> Any.get(fieldName: String): T {
	val field = this.findField(fieldName)
	@Suppress("DEPRECATION")
	val accessible = field.isAccessible
	try {
		field.isAccessible = true
		val value = field.get(this)
		if (value != null && value !is T) {
			val message = "${field}'s value of type ${value::class} cannot be cast to ${T::class}"
			throw ClassCastException(message)
		}
		return value as T
	} finally {
		field.isAccessible = accessible
	}
}

operator fun Any.set(fieldName: String, value: Any?) {
	val field = this.findField(fieldName)
	@Suppress("DEPRECATION")
	val accessible = field.isAccessible
	try {
		field.isAccessible = true
		field.set(this, value)
	} catch (ex: java.lang.IllegalArgumentException) {
		val valueType = if (value != null) value::class else null
		val message = "${field} = ${valueType} is not possible"
		throw ClassCastException(message).initCause(ex)
	} finally {
		field.isAccessible = accessible
	}
}

@Deprecated(message = "Don't use directly, use Any.get or Any.set", level = DeprecationLevel.ERROR)
fun Any.findField(fieldName: String): Field {
	val hierarchy = generateSequence<Class<*>>(this::class.java) { it.superclass }.toList()
	val fields = hierarchy.mapNotNull {
		try {
			it.getDeclaredField(fieldName)
		} catch (e: NoSuchFieldException) {
			null
		}
	}
	return fields.singleOrNull()
		?: throw NoSuchFieldException("'${fieldName}', looked everywhere (${hierarchy}), found: ${fields}")
}
