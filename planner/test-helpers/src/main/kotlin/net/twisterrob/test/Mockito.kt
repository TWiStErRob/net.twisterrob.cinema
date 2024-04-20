package net.twisterrob.test

import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor

inline fun <reified T> captureSingle(block: KArgumentCaptor<T>.() -> Unit): T {
	val captor: KArgumentCaptor<T> = argumentCaptor()
	captor.apply(block)
	return captor.allValues.single()
}
