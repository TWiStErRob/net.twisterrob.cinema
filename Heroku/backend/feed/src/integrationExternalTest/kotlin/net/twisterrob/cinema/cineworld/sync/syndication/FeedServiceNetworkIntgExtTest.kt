package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator
import io.ktor.client.HttpClient
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class FeedServiceNetworkIntgExtTest {

	private val sut = FeedServiceNetwork(HttpClient())

	@Disabled("Endpoint is dead, returns HTML error page")
	@Test fun `read weekly film times XML`() {
		try {
			val feed = sut.getWeeklyFilmTimes()
			feed.sanityCheck()
		} catch (e: Throwable) {
			if (e is com.fasterxml.jackson.databind.exc.ValueInstantiationException) {
				val message = e.message.orEmpty()
				val filmClass = Regex.escape(Feed.Film::class.java.name)
				val filmId = Regex.escape(Long::class.javaObjectType.name)
				val idGenName = Regex.escape(PropertyBasedObjectIdGenerator::class.java.name)
				// com.fasterxml.jackson.databind.exc.ValueInstantiationException: Cannot construct instance of `net.twisterrob.cinema.cineworld.sync.syndication.Feed$Film`, problem: Already had POJO for id (java.lang.Long) [[ObjectId: key=265121, type=com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator, scope=net.twisterrob.cinema.cineworld.sync.syndication.Feed$Film]]
				assumeFalse(
					message.matches(
						Regex(
							"""^Cannot construct instance of `$filmClass`, """ +
									"""problem: Already had POJO for id \($filmId\) """ +
									"""\[\[ObjectId: key=\d+, type=$idGenName, scope=$filmClass\]\]""" +
									""".*$""",
							RegexOption.DOT_MATCHES_ALL
						)
					)
				) {
					"Cineworld has messed up their data, nothing to do about it.\n$e"
				}
			}
			throw e
		}
	}
}
