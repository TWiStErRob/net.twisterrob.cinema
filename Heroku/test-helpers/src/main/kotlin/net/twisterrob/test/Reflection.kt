package net.twisterrob.test

operator fun <T : Any?> Any.get(fieldName: String): T {
	val field = this::class.java.getDeclaredField(fieldName)!!
	val accessible = field.isAccessible
	try {
		field.isAccessible = true
		@Suppress("UNCHECKED_CAST") // user will fail if wrong type
		return field.get(this) as T
	} finally {
		field.isAccessible = accessible
	}
}

operator fun Any.set(fieldName: String, value: Any?) {
	val field = this::class.java.getDeclaredField(fieldName)!!
	val accessible = field.isAccessible
	try {
		field.isAccessible = true
		field.set(this, value)
	} finally {
		field.isAccessible = accessible
	}
}
