package net.twisterrob.test

import com.flextrade.jfixture.FluentCustomisation
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.extensions.CreateExtensions

fun JFixture.applyCustomisation(block: FluentCustomisation.() -> Unit): JFixture {
	this.customise().apply(block)
	return this
}

inline fun <reified T> JFixture.build(block: T.() -> Unit = {}): T =
	this.create(T::class.java).apply(block)

@Suppress("UNCHECKED_CAST")
inline fun <reified T> JFixture.buildList(size: Int = 3): MutableList<T> =
	this.collections()
		.createCollection(MutableList::class.java as Class<MutableList<T>>, T::class.java, size)

@Suppress("UNCHECKED_CAST")
inline fun <reified K, reified V> JFixture.buildMap(size: Int = 3): MutableMap<K, V> =
	this.collections()
		.createMap(LinkedHashMap::class.java as Class<MutableMap<K, V>>, K::class.java, V::class.java, size)

inline fun <reified T> JFixture.buildRange(range: ClosedRange<T>): T
		where T : Number,
		      T : Comparable<T> =
	this.create().range(range)

inline fun <reified T> CreateExtensions.range(range: ClosedRange<T>): T
		where T : Number,
		      T : Comparable<T> =
	this.inRange(T::class.java, range.start, range.endInclusive)!!
