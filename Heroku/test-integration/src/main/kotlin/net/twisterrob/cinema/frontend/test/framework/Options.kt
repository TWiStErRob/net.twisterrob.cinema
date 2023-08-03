package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.Dimension
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object Options {

	@Suppress("MagicNumber")
	val windowSize: Dimension = Dimension(1920, 1080)

	@Suppress("MagicNumber")
	val defaultWaitTimeout: Duration = 30.seconds

	val isHeadless: Boolean
		get() = System.getProperty("net.twisterrob.test.selenium.headless", "false").toBooleanStrict()

	val baseUrl: String
		get() = System.getProperty("net.twisterrob.test.selenium.baseUrl")

	val userName: String
		get() = System.getProperty("net.twisterrob.test.selenium.user.name")

	val userPass: String
		get() = System.getProperty("net.twisterrob.test.selenium.user.pass")

	val screenshotDir: File
		get() = File(System.getProperty("net.twisterrob.test.selenium.screenshot.dir"))
}
