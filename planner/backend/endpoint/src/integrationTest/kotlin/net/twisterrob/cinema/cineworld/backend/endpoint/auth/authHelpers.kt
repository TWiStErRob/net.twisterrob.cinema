@file:Suppress("MatchingDeclarationName")

package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.flextrade.jfixture.JFixture
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
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
	 * @see configuration
	 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
	 */
	const val realisticCookie =
		"auth=userId%3D%2523srealistic%5Fgoogle%5Fsub%2F1d9f41780441596d9ec55c20219873d813180e8d1d7caab07e1463fcb6462622"

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
