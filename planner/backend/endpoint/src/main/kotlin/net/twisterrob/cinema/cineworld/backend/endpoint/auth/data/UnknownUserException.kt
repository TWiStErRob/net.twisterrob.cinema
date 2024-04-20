package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

class UnknownUserException(
	@Suppress("CanBeParameter") // keep for debug
	val userId: String
) : RuntimeException("Cannot find user $userId")

