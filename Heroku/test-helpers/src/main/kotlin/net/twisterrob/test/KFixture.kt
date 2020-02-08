package net.twisterrob.test

import com.flextrade.kfixture.KFixture

inline fun <reified T> KFixture.build(): T =
	this.invoke()

inline fun <reified T> KFixture.buildList(size: Int = 3): List<T> =
	this.jFixture.buildList(size)
