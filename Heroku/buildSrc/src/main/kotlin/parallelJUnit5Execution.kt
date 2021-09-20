import org.gradle.api.tasks.testing.Test

enum class Concurrency {
	PerSuite,
	PerClass,
	PerMethod,
}

fun Test.parallelJUnit5Execution(concurrency: Concurrency) {
	// Split up work into as many processes as possible to ease memory pressure for each executor.
	// But not as a workaround for https://github.com/gradle/gradle/issues/6453, as it is not necessary since Gradle 5.1.
	maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1

	systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_method")
	systemProperty("junit.jupiter.execution.parallel.config.strategy", "dynamic")
	when (concurrency) {
		Concurrency.PerSuite ->
			systemProperties(
				mapOf(
					"junit.jupiter.execution.parallel.enabled" to false,
					"junit.jupiter.execution.parallel.mode.default" to "same_thread",
					"junit.jupiter.execution.parallel.mode.classes.default" to "same_thread"
				)
			)
		Concurrency.PerClass ->
			systemProperties(
				mapOf(
					"junit.jupiter.execution.parallel.enabled" to true,
					"junit.jupiter.execution.parallel.mode.default" to "same_thread",
					"junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
				)
			)
		Concurrency.PerMethod ->
			systemProperties(
				mapOf(
					"junit.jupiter.execution.parallel.enabled" to true,
					"junit.jupiter.execution.parallel.mode.default" to "concurrent",
					"junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
				)
			)
	}
}
