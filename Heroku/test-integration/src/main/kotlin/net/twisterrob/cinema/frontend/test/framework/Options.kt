package net.twisterrob.cinema.frontend.test.framework

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object Options {

	val defaultWaitTimeout: Duration = 30.seconds

	val headless: Boolean
		get() = System.getProperty("net.twisterrob.test.selenium.headless", "false").toBooleanStrict()

	val baseUrl: String
		get() = "http://127.0.0.1:8080"

	val userName: String
		get() = System.getProperty("net.twisterrob.test.selenium.user.name")

	val userPass: String
		get() = System.getProperty("net.twisterrob.test.selenium.user.pass")
}
