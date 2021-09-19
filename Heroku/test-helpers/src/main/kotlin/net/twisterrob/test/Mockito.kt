package net.twisterrob.test

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

inline fun <reified T> captureSingle(block: KArgumentCaptor<T>.() -> Unit): T {
	val captor: KArgumentCaptor<T> = argumentCaptor()
	captor.apply(block)
	return captor.allValues.single()
}
