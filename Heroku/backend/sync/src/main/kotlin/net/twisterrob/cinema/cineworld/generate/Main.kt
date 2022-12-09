package net.twisterrob.cinema.cineworld.generate

import io.ktor.client.HttpClient
import net.twisterrob.cinema.cineworld.sync.syndication.FeedMapper
import org.neo4j.ogm.session.SessionFactory
import java.io.Closeable
import java.net.URI
import javax.inject.Inject

class Main @Inject constructor(
	private val neo4j: SessionFactory,
	private val network: HttpClient,
	private val generator: PerformanceGenerator,
	private val writer: FeedMapper,
) : Closeable {

	fun generate(params: MainParameters) {
		println("Generating: $params")
		val feed = generator.generate()
		writer.write(params.targetFile, feed)
	}

	override fun close() {
		neo4j.close()
		network.close()
	}

	companion object {

		@Suppress("MemberNameEqualsClassName")
		@JvmStatic
		fun main(vararg args: String) {
			val params = MainParametersParser().parse(*args)
			val dagger = DaggerGenerateAppComponent.builder()
				.graphDBUri(getNeo4jUrl())
				.params(params)
				.build()
			dagger.main.use { it.generate(params) }
		}

		private fun getNeo4jUrl(): URI {
			val url = System.getenv()["NEO4J_URL"]
				?: error("NEO4J_URL environment variable must be defined (=neo4j+s://username:password@hostname:port).")
			return URI.create(url)
		}
	}
}
