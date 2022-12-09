package net.twisterrob.cinema.cineworld.generate

import java.io.File

class MainParametersParser {

	fun parse(vararg args: String): MainParameters {
		check(args.size == 1) {
			"Expected exactly one argument, the target file, got ${args.size}: ${args.joinToString()}"
		}
		return MainParameters(
			targetFile = File(args[0]),
		)
	}
}
