package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import javax.inject.Inject
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User as FrontendUser
import net.twisterrob.cinema.database.model.User as DBUser

class UserMapper @Inject constructor(
) {

	fun map(user: DBUser): FrontendUser =
		FrontendUser(
			id = user.id,
			name = user.name,
			email = user.email,
			realm = user.realm,
			_created = user._created
		)
}
