package net.twisterrob.kotlin

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

operator fun <T, R> KProperty0<R>.getValue(instance: T, metadata: KProperty<*>): R =
	this.get()

operator fun <T, R> KMutableProperty0<R>.setValue(instance: T, metadata: KProperty<*>, value: R) =
	this.set(value)
