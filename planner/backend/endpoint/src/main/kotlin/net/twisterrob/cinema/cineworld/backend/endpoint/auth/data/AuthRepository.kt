package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import net.twisterrob.cinema.database.services.UserService
import java.time.OffsetDateTime
import javax.inject.Inject

class AuthRepository @Inject constructor(
	private val service: UserService,
	private val mapper: UserMapper,
) {

	fun addUser(userId: String, email: String, name: String, realm: String, created: OffsetDateTime) {
		service.addUser(
			userId = userId,
			email = email,
			name = name,
			realm = realm,
			created = created,
		)
	}

	fun findUser(userId: String): User {
		val user = service.find(userId) ?: throw UnknownUserException(userId)
		return mapper.map(user)
	}
}
