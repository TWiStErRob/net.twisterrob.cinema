package net.twisterrob.cinema.cineworld.backend.ktor

import com.fasterxml.jackson.databind.SerializationFeature
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

internal fun Application.configuration(
	staticRootFolder: File = File("./deploy/static"),
	oauthHttpClient: HttpClient = HttpClient(),
	config: Map<String, Any?> = jacksonObjectMapper()
		.readValue(App::class.java.getResourceAsStream("/default-env.json"))
) {
	this.attributes.staticRootFolder = staticRootFolder

	install(DefaultHeaders)
	install(CallLogging)
	install(HeaderLoggingFeature)
	install(ContentNegotiation) {
		jackson {
			enable(SerializationFeature.INDENT_OUTPUT)
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
