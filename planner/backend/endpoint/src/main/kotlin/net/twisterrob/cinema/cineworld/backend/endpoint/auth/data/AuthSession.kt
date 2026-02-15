package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(val userId: String)
