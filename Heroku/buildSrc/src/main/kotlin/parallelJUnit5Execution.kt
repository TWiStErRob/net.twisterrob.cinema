@file:Suppress("MatchingDeclarationName")

import org.gradle.api.tasks.testing.Test

enum class Concurrency {
	PerSuite,
	PerClass,
	PerMethod,
}

fun Test.parallelJUnit5Execution(concurrency: Concurrency) {
	val overrideRaw = project.property("net.twisterrob.build.testConcurrencyOverride").toString()
	val override = if (overrideRaw == "") null else Concurrency.valueOf(overrideRaw)
	when (override ?: concurrency) {
		Concurrency.PerSuite -> {
			props(parallel = false, defaultMode = ConcurrencyMode.SameThread, classMode = ConcurrencyMode.SameThread)
		}
		Concurrency.PerClass -> {
			// Split up work into as many processes as possible to ease memory pressure for each executor.
			maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1
			props(parallel = true, defaultMode = ConcurrencyMode.SameThread, classMode = ConcurrencyMode.Concurrent)
		}
		Concurrency.PerMethod -> {
			// Split up work into as many processes as possible to ease memory pressure for each executor.
			maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1
			props(parallel = true, defaultMode = ConcurrencyMode.Concurrent, classMode = ConcurrencyMode.Concurrent)
		}
	}
}

private enum class ConcurrencyMode(val mode: String) {
	SameThread("same_thread"),
	Concurrent("concurrent"),
}

private fun Test.props(parallel: Boolean, defaultMode: ConcurrencyMode, classMode: ConcurrencyMode) {
	systemProperties(
		mapOf(
			"junit.jupiter.testinstance.lifecycle.default" to "per_method",
			"junit.jupiter.execution.parallel.config.strategy" to "dynamic",
			"junit.jupiter.execution.parallel.enabled" to parallel,
			"junit.jupiter.execution.parallel.mode.default" to defaultMode.mode,
			"junit.jupiter.execution.parallel.mode.classes.default" to classMode.mode
		)
	)
}
