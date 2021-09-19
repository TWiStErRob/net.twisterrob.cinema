import org.gradle.api.tasks.testing.Test

fun Test.parallelJUnit5Execution() {
	// Workaround for https://github.com/gradle/gradle/issues/6453 is not necessary since Gradle 5.1
	maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1

	systemProperties = mapOf(
		"junit.jupiter.testinstance.lifecycle.default" to "per_method",
		"junit.jupiter.execution.parallel.enabled" to true,
		"junit.jupiter.execution.parallel.mode.default" to "concurrent",
		"junit.jupiter.execution.parallel.mode.classes.default" to "concurrent",
		"junit.jupiter.execution.parallel.config.strategy" to "dynamic"
//		"junit.jupiter.execution.parallel.config.fixed.parallelism" to 3
	)
}
