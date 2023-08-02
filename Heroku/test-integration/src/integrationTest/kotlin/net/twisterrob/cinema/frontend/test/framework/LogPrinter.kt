package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.logging.LogEntry
import java.util.logging.Level

class LogPrinter {

	fun print(entry: LogEntry) {
		val color = LOG_COLORS[entry.level] ?: Color.Grey
		val method = LOG_METHODS[entry.level] ?: entry.level.name
		println("\u001b[${color.value}m${method} - ${entry.message}\u001b[39m")
	}

	companion object {
		private enum class Color(val value: Int) {
			Red(31),
			Yellow(33),
			Blue(34),
			Magenta(35),
			Grey(37),
		}

		private val LOG_COLORS = mapOf(
			Level.FINE to Color.Blue,
			Level.INFO to Color.Magenta,
			Level.WARNING to Color.Yellow,
			Level.SEVERE to Color.Red,
		)
		private val LOG_METHODS: Map<Level, String> = mapOf(
			Level.FINE to "console.debug",
			Level.INFO to "console.log",
			Level.WARNING to "console.warn",
			Level.SEVERE to "console.error",
		)
	}
}
