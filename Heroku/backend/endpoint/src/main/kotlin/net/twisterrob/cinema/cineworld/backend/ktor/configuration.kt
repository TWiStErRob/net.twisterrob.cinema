package net.twisterrob.cinema.cineworld.backend.ktor

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.pre
import kotlinx.html.title
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.fakeRootFolder
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.staticRootFolder
import net.twisterrob.cinema.cineworld.backend.endpoint.app.App
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID.Scopes.email
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID.Scopes.openid
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID.Scopes.profile
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * @param staticRootFolder `.` is the Heroku project folder when ran from IDEA.
 * @see RouteControllerRegistrar
 */
@Suppress("LongMethod") // TODO find a way to split this up into smaller pieces, while keeping visibility.
internal fun Application.configuration(
	staticRootFolder: File = File("./deploy/static"),
	fakeRootFolder: File = File("./backend/src/test/fake"),
	oauthHttpClient: HttpClient = HttpClient(),
	config: Map<String, Any?> = jacksonObjectMapper()
		.readValue(App::class.java.getResourceAsStream("/default-env.json")!!)
) {
	log.info("Configuring app as ${environment.config.environment} environment.")

	this.attributes.staticRootFolder = staticRootFolder
	this.attributes.fakeRootFolder = fakeRootFolder

	install(DefaultHeaders)
	install(CallLogging)
	install(Compression) {
		default()
	}
	install(CachingHeaders) {
		// default is `options { it.caching }`
	}
	//install(HeaderLoggingFeature)
	install(ContentNegotiation) {
		jackson {
			enable(SerializationFeature.INDENT_OUTPUT)
			registerModule(JavaTimeModule())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
			disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
			registerModule(object : SimpleModule("cineworld-backend") {
				init {
					addSerializer(OffsetDateTime::class.java, OffsetDateTimeJsonSerializer())
				}
			})
		}
	}
	install(StatusPages) {
		exception<Throwable> { call, cause ->
			call.application.log.error("Unhandled exception", cause)
			call.respondHtml(HttpStatusCode.InternalServerError) {
				render500InternalServerError(cause)
			}
			throw cause
		}
	}
	install(Resources) // support @Resource
	install(Sessions) {
		cookie<AuthSession>("auth") {
			val secretSignKey = "twister".toByteArray()
			transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
		}
	}

	install(Authentication) {
		val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
			name = "google",
			// From https://accounts.google.com/.well-known/openid-configuration
			authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
			// was https://www.googleapis.com/oauth2/v3/token in ktor docs
			accessTokenUrl = "https://oauth2.googleapis.com/token",
			requestMethod = HttpMethod.Post,

			// https://console.developers.google.com/apis/credentials?project=twisterrob-cinema > OAuth 2.0 Client IDs
			clientId = config["GOOGLE_CLIENT_ID"] as String,
			clientSecret = config["GOOGLE_CLIENT_SECRET"] as String,
			// https://console.developers.google.com/apis/credentials/consent/edit?project=twisterrob-cinema
			defaultScopes = listOf(openid, email, profile)
		)
		oauth(name = null /* default */) {
			skipWhen { call ->
				// AuthController will install an interceptor to set the user
				call.sessions.get<AuthSession>() != null
				false // mandatory for now, TODO use above condition? see ApplicationCall.isAuthenticated
			}
			client = oauthHttpClient
			providerLookup = { googleOauthProvider }
			urlProvider = {
				absoluteUrl("/auth/google/return")
			}
		}
	}
}

private fun HTML.render500InternalServerError(error: Throwable) {
	head {
		title { +"Internal Server Error" }
	}
	body {
		h1 {
			+"Internal Server Error"
		}
		h2 { +"Exception" }
		pre {
			+error.stackTraceToString()
		}
	}
}

/**
 * @see OffsetDateTimeJsonSerializer
 */
class OffsetDateTimeJsonSerializer : JsonSerializer<OffsetDateTime>() {
	override fun serialize(
		value: OffsetDateTime, gen: JsonGenerator, serializers: SerializerProvider
	) {
		val utcTime = value.withOffsetSameInstant(ZoneOffset.UTC)
		gen.writeString(ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(utcTime))
	}
}
