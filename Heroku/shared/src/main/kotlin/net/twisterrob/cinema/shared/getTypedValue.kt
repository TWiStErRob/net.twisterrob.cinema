package net.twisterrob.cinema.shared

@Suppress("UNCHECKED_CAST")
fun <K, V, T : V> Map<K, V>.getTypedValue(key: K): T =
	this.getValue(key) as T
