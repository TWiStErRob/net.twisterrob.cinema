package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import net.twisterrob.cinema.database.services.UserService
import java.time.OffsetDateTime
import javax.inject.Inject

class AuthRepository @Inject constructor(
	private val userService: UserService
) {

	fun addUser(userId: String, email: String, name: String, realm: String, created: OffsetDateTime) {
		userService.addUser(userId, email, name, realm, created)
	}

	fun findUser(userId: String): User {
		val user = userService.find(userId) ?: throw UnknownUserException(userId)
		return user.let {
			User(it.id, it.email)
		}
	}

	class UnknownUserException(
		@Suppress("CanBeParameter") // keep for debug
		val userId: String
	) : RuntimeException("Cannot find user $userId")

	data class User(
		val id: String,
		val email: String
	)
}
