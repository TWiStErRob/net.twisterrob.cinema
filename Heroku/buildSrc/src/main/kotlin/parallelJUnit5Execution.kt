import org.gradle.api.tasks.testing.Test

enum class Concurrency {
	PerSuite,
	PerClass,
	PerMethod,
}

fun Test.parallelJUnit5Execution(concurrency: Concurrency) {
	systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
	systemProperty("junit.jupiter.execution.parallel.config.strategy", "dynamic")
	val overrideRaw = project.property("net.twisterrob.build.testConcurrencyOverride").toString()
	val override = if (overrideRaw == "") null else Concurrency.valueOf(overrideRaw)
	when (override ?: concurrency) {
		Concurrency.PerSuite -> {
			systemProperties(
				mapOf(
					"junit.jupiter.execution.parallel.enabled" to false,
					"junit.jupiter.execution.parallel.mode.default" to "same_thread",
					"junit.jupiter.execution.parallel.mode.classes.default" to "same_thread"
				)
			)
		}
		Concurrency.PerClass -> {
			// Split up work into as many processes as possible to ease memory pressure for each executor.
			maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1
			systemProperties(
				mapOf(
					"junit.jupiter.execution.parallel.enabled" to true,
					"junit.jupiter.execution.parallel.mode.default" to "same_thread",
					"junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
				)
			)
		}
		Concurrency.PerMethod -> {
			// Split up work into as many processes as possible to ease memory pressure for each executor.
			maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1
			systemProperties(
				mapOf(
					"junit.jupiter.execution.parallel.enabled" to true,
					"junit.jupiter.execution.parallel.mode.default" to "concurrent",
					"junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
				)
			)
		}
	}
}
