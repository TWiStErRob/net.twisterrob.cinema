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
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.ConversionService
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.pre
import kotlinx.html.title
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.staticRootFolder
import net.twisterrob.cinema.cineworld.backend.endpoint.app.App
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID.Scopes.email
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID.Scopes.openid
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID.Scopes.profile
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

/**
 * @see RouteControllerRegistrar
 */
internal fun Application.configuration(
	staticRootFolder: File = File("./deploy/static"),
	oauthHttpClient: HttpClient = HttpClient(),
	config: Map<String, Any?> = jacksonObjectMapper()
		.readValue(App::class.java.getResourceAsStream("/default-env.json"))
) {
	this.attributes.staticRootFolder = staticRootFolder

	install(DefaultHeaders)
	install(CallLogging)
	//install(HeaderLoggingFeature)
	install(DataConversion) {
		this.convert(LocalDate::class, object : ConversionService {
			private val formatter = DateTimeFormatterBuilder()
				.appendValue(ChronoField.YEAR, 4, 4, SignStyle.NEVER)
				.appendValue(ChronoField.MONTH_OF_YEAR, 2)
				.appendValue(ChronoField.DAY_OF_MONTH, 2)
				.toFormatter(Locale.ROOT)

			override fun fromValues(values: List<String>, type: Type): LocalDate =
				LocalDate.from(formatter.parse(values.single()))

			override fun toValues(value: Any?): List<String> =
				TODO("YAGNI")
		})
	}
	install(ContentNegotiation) {
		jackson {
			enable(SerializationFeature.INDENT_OUTPUT)
			registerModule(JavaTimeModule())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
			disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
			registerModule(object : SimpleModule("cineworld-backend") {
				init {
					// Work around https://github.com/FasterXML/jackson-modules-java8/issues/76
					// Make sure trailing zeros are serialized. Disregards all (de-)serialization features.
					addSerializer(OffsetDateTime::class.java, object : JsonSerializer<OffsetDateTime>() {
						override fun serialize(
							value: OffsetDateTime, gen: JsonGenerator, serializers: SerializerProvider
						) {
							gen.writeString(value.toString())
						}
					})
				}
			})
		}
	}
	install(StatusPages) {
		exception<Throwable> { cause ->
			call.respondHtml(HttpStatusCode.InternalServerError) {
				head {
					title { +"Internal Server Error" }
				}
				body {
					h1 {
						+"Internal Server Error"
					}
					h2 { +"Exception" }
					pre {
						+StringWriter().apply { cause.printStackTrace(PrintWriter(this, true)) }.toString()
					}
				}
			}
			throw cause
		}
	}
	install(Locations) // support @Location
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
			client = oauthHttpClient
			providerLookup = { googleOauthProvider }
			urlProvider = { absoluteUrl("/auth/google/return") }
		}
	}
}
