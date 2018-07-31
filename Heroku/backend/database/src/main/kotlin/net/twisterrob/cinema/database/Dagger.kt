package net.twisterrob.cinema.database

import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.services.CinemaServices
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Scope

private val LOG = LoggerFactory.getLogger(Neo4JModule::class.java)

@Scope
annotation class Neo4J

@Subcomponent(modules = [Neo4JModule::class])
@Neo4J
interface Neo4JComponent : CinemaServices {

	@Subcomponent.Builder
	interface Builder {

		fun build(): Neo4JComponent
	}
}

@Module
class Neo4JModule constructor(
	private val uri: URI = URI.create(System.getenv()["NEO4J_URL"] as String),
	private vararg val packages: String = arrayOf(Cinema::class.java.`package`.name)
) {

	init {
		LOG.debug("Server: {}, packages = {}", uri, packages.toList())
	}

	@Neo4J
	@Provides
	fun config(): Configuration = Configuration
		.Builder()
		.uri(uri.toString())
		.connectionLivenessCheckTimeout(TimeUnit.SECONDS.toMillis(30).toInt())
		.connectionPoolSize(150)
		.build()

	@Neo4J
	@Provides
	fun sessionFactory(config: Configuration): SessionFactory =
		SessionFactory(config, *packages).apply {
			setEntityInstantiator(KotlinReflectionEntityInstantiator())
//			register(LoggingEventListener())
		}

	@Provides
	fun session(factory: SessionFactory): Session =
		factory.openSession()
}
