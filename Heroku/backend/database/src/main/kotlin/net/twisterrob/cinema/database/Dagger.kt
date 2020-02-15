package net.twisterrob.cinema.database

import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import net.twisterrob.cinema.database.model.BaseNode
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.database.model.View
import net.twisterrob.unwrapITE
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.event.Event
import org.neo4j.ogm.session.event.EventListenerAdapter
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Scope
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties

private val LOG = LoggerFactory.getLogger(Neo4JModule::class.java)

@Scope
annotation class Neo4J

/**
 * @see Neo4JModule.Dependencies when using this module.
 */
@Module
@Suppress("RedundantEmptyInitializerBlock")
object Neo4JModule {

	init {
		// For debugging.
		//nonapi.io.github.classgraph.utils.LogNode.logInRealtime(true)
	}

	/**
	 * Inherit this from [dagger.Component.Builder] for the component to get a consistent dependency setup.
	 *
	 * @param Builder actual type of the [dagger.Component.Builder]
	 */
	interface Dependencies<Builder> {

		/**
		 * For most cases just call this method without any argument, the default will work.
		 */
		@BindsInstance
		fun graphDBUri(
			@Named(GRAPH_DB) uri: URI =
				URI.create(System.getenv()["NEO4J_URL"] as String)
		): Builder
	}

	private const val GRAPH_DB = "graphDB"
	private val ENTITIES = listOf(
		Cinema::class,
		Film::class,
		User::class,
		View::class
	)

	/**
	 * Need to include super-classes explicitly, otherwise they don't get processed by DomainInfo.
	 */
	private val OGM_ENTITIES = ENTITIES
		.flatMap { listOf(it) + it.allSuperclasses }
		.toSet() - Any::class

	@Neo4J
	@Provides
	fun config(@Named(GRAPH_DB) uri: URI): Configuration = Configuration
		.Builder()
		.uri(uri.toString())
		.connectionLivenessCheckTimeout(TimeUnit.SECONDS.toMillis(30).toInt())
		.connectionPoolSize(150)
		.build()

	@Neo4J
	@Provides
	fun sessionFactory(config: Configuration): SessionFactory {
		val modelClasses = OGM_ENTITIES.map { it.qualifiedName!! }
		return SessionFactory(config, *modelClasses.toTypedArray()).apply {
			// Disabled for now, let's try to use default Neo4J OGM first
			//setEntityInstantiator(KotlinReflectionEntityInstantiator())

			// For debugging.
			//register(LoggingEventListener())

			// Only fully valid object can be saved.
			this.register(object : EventListenerAdapter() {
				override fun onPreSave(event: Event) {
					// Make sure all entities extend BaseNode.
					val entity = event.`object` as BaseNode
					// Execute all getters to trigger any lateinit misses.
					entity::class.declaredMemberProperties.forEach { unwrapITE { it.getter.call(entity) } }
				}
			})
			LOG.debug("Server: {}, packages = {}", config.uri, modelClasses)
		}
	}

	@Provides
	fun session(factory: SessionFactory): Session =
		factory.openSession()
}
