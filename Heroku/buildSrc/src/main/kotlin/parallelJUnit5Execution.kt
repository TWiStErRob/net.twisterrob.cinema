import org.gradle.api.tasks.testing.Test

fun Test.parallelJUnit5Execution() {
	// Split up work into as many processes as possible to ease memory pressure for each executor.
	// But not as a workaround for https://github.com/gradle/gradle/issues/6453, as it is not necessary since Gradle 5.1.
	maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1

	systemProperties = mapOf(
		"junit.jupiter.testinstance.lifecycle.default" to "per_method",
		"junit.jupiter.execution.parallel.enabled" to true,
		"junit.jupiter.execution.parallel.mode.default" to "concurrent",
		"junit.jupiter.execution.parallel.mode.classes.default" to "concurrent",
		"junit.jupiter.execution.parallel.config.strategy" to "dynamic"
	)
}
