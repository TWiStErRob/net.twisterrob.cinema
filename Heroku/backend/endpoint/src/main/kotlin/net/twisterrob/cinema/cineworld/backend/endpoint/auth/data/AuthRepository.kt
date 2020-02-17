package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import java.time.OffsetDateTime
import javax.inject.Inject

class AuthRepository @Inject constructor(
) {

	fun addUser(userId: String, email: String, name: String, realm: String, created: OffsetDateTime) {
		println("addUser($userId, $email, $name, $realm, $created)")
	}
}
