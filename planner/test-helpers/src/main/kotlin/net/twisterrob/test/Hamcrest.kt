package net.twisterrob.test

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun <T> emptyIterable(): Matcher<Iterable<T>> = object : TypeSafeMatcher<Iterable<T>>() {

	override fun describeTo(description: Description) {
		description.appendText("an empty collection")
	}

	override fun matchesSafely(item: Iterable<T>): Boolean =
		!item.iterator().hasNext()

	override fun describeMismatchSafely(item: Iterable<T>, mismatchDescription: Description) {
		mismatchDescription.appendValue(item)
	}
}
