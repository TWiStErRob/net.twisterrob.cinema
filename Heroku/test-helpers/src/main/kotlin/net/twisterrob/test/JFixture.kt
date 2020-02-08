package net.twisterrob.test

import com.flextrade.jfixture.JFixture

inline fun <reified T> JFixture.build(): T =
	this.create(T::class.java)

@Suppress("UNCHECKED_CAST")
inline fun <reified T> JFixture.buildList(size: Int = 3): List<T> =
	this.collections().createCollection(List::class.java as Class<List<T>>, T::class.java, size)
