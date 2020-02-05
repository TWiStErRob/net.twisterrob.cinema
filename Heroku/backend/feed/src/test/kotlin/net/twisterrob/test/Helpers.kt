package net.twisterrob.test

import com.flextrade.jfixture.JFixture

inline fun <reified T> JFixture.build(): T = create(T::class.java)
