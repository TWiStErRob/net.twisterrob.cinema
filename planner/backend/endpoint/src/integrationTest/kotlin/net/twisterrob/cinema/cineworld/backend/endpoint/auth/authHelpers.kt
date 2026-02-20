@file:Suppress("MatchingDeclarationName")

package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.flextrade.jfixture.JFixture
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.server.testing.ClientProvider
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.test.build
import org.mockito.kotlin.whenever

object AuthTestConstants {

	/**
	 * It needs to be a cookie that uses the right secretSignKey in Sessions Ktor feature.
	 * If this is broken, debug a test where [receiveAuthorizationFromGoogle] is visible and capture the value.
	 * The rest of the [HttpHeaders.SetCookie] is omitted as it's not relevant here.
	 *
	 * Note: In Ktor 3.0 with @Serializable, the format changed from custom reflection-based serialization
	 * to JSON-based kotlinx.serialization format: {"userId":"user_id"}/HMAC_SHA256_hex
	 *
	 * @see configuration
	 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
	 */
	const val realisticCookie =
		"auth=%7B%22userId%22%3A%22realistic_google_sub%22%7D%2F9eb59086c2ea9b62beaee4bf1665dea5f98730210e005cabf65b5de7b2acfdf9"

	/**
	 * User ID contained within [realisticCookie]. Needed to ensure session data is passed through the right way.
	 */
	const val realisticUserId = "realistic_google_sub"
}

/**
 * Makes sure that auth interceptor works as expected.
 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.AuthController
 * @see sendTestAuth
 */
fun AuthRepository.setupAuth(): User {
	val fixtUser: User = JFixture().build()
	whenever(this.findUser(AuthTestConstants.realisticUserId)).thenReturn(fixtUser)
	return fixtUser
}

/**
 * Makes sure that auth interceptor works as expected.
 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.AuthController
 * @see setupAuth
 */
fun HttpRequestBuilder.sendTestAuth() {
	header(HttpHeaders.Cookie, AuthTestConstants.realisticCookie)
}

val ClientProvider.noRedirectClient: HttpClient
	get() = this.createClient {
		followRedirects = false
	}
