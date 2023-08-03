package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.pages.dsl.Cinema
import org.assertj.core.api.AbstractIterableAssert

fun <A : AbstractIterableAssert<*, *, Cinema, *>> A.anyMeet(assertions: WebElementAssert.() -> Unit) {
	this.anySatisfy { assertThat(it.root).assertions() }
}

fun <A : AbstractIterableAssert<*, *, Cinema, *>> A.allMeet(assertions: WebElementAssert.() -> Unit) {
	this.allSatisfy { assertThat(it.root).assertions() }
}

fun <A : AbstractIterableAssert<*, *, Cinema, *>> A.noneMeet(assertions: WebElementAssert.() -> Unit) {
	this.noneSatisfy { assertThat(it.root).assertions() }
}
