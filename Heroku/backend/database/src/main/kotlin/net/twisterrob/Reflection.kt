package net.twisterrob

import java.lang.reflect.InvocationTargetException

inline fun unwrapITE(block: () -> Unit) {
	try {
		block()
	} catch (ex: InvocationTargetException) {
		throw ex.cause ?: ex
	}
}
