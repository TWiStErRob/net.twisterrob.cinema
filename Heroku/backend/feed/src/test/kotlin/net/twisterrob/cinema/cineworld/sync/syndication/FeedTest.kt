package net.twisterrob.cinema.cineworld.sync.syndication

import com.flextrade.jfixture.JFixture
import net.twisterrob.test.build
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

class FeedTest {

	@Test fun `Fixture can be built`() {
		val fixture = JFixture()

		val feed: Feed = fixture.build()

		assertThat(feed.cinemas, not(empty()))
		assertThat(feed.films, not(empty()))
		assertThat(feed.attributes, not(empty()))
		assertThat(feed.performances, not(empty()))
	}

	@Test fun `plus adds all fields together`() {
		val fixture = JFixture()
		val feed1: Feed = fixture.build()
		val feed2: Feed = generateSequence { fixture.build<Feed>() }
			.first { feed2 ->
				val hasUniqueFilms =
					feed2.films.map(Feed.Film::id)
						.intersect(feed1.films.map(Feed.Film::id))
						.isEmpty()
				val hasUniqueAttributes =
					feed2.attributes.map(Feed.Attribute::code)
						.intersect(feed1.attributes.map(Feed.Attribute::code))
						.isEmpty()
				hasUniqueFilms && hasUniqueAttributes
			}

		val result = feed1 + feed2

		assertAdded(result, feed1, feed2, Feed::attributes)
		assertAdded(result, feed1, feed2, Feed::films)
		assertAdded(result, feed1, feed2, Feed::performances)
		assertAdded(result, feed1, feed2, Feed::cinemas)
	}

	@Test fun `plus skips duplicate films`() {
		val fixture = JFixture()
		val feed1: Feed = fixture.build()
		val extra: Feed.Film = fixture.build()
		val feed2 = feed1.copy(_films = feed1.films + extra)

		val result = feed1 + feed2

		assertThat(result.films, contains(feed1.films + extra))
	}

	@Test fun `plus skips duplicate attributes`() {
		val fixture = JFixture()
		val feed1: Feed = fixture.build()
		val extra: Feed.Attribute = fixture.build()
		val feed2 = feed1.copy(_attributes = feed1.attributes + extra)

		val result = feed1 + feed2

		assertThat(result.attributes, contains(feed1.attributes + extra))
	}
}

private inline fun <reified T> assertAdded(
	result: Feed, feed1: Feed, feed2: Feed,
	property: Feed.() -> List<T>
) {
	assertThat(result.property(), hasItems(feed1.property()))
	assertThat(result.property(), hasItems(feed2.property()))
	assertThat(result.property(), hasSize(feed1.property().size + feed2.property().size))
}

private inline fun <reified T> hasItems(items: Iterable<T>): Matcher<Iterable<T>> =
	Matchers.hasItems(*items.toList().toTypedArray())

private inline fun <reified T> contains(items: Iterable<T>): Matcher<Iterable<T>> =
	Matchers.contains(*items.toList().toTypedArray())
