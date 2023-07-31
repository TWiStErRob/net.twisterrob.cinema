package net.twisterrob.cinema.frontend.test.framework

object Options {

	val headless: Boolean
		get() = System.getProperty("net.twisterrob.test.selenium.headless", "false").toBooleanStrict()
	
	val host: String
		get() = "http://127.0.0.1:8080"
}
