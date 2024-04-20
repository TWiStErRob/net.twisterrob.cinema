package net.twisterrob.cinema.build.logging

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestOutputEvent
import org.gradle.api.tasks.testing.TestOutputListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.EnumSet
import kotlin.math.absoluteValue

@Suppress("FunctionMaxLength")
fun Test.configureVerboseReportsForGithubActions() {
	testLogging {
		// disable all events, output handled by custom callbacks below
		events = EnumSet.noneOf(TestLogEvent::class.java)
		//events = TestLogEvent.values().toSet() - TestLogEvent.STARTED
		exceptionFormat = TestExceptionFormat.FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
	}
	val processor = ResultProcessor(logger)
	addTestListener(processor)
	addTestOutputListener(processor)
}

private class ResultProcessor(
	private val logger: Logger,
) : TestListener, TestOutputListener {

	private val lookup: MutableMap<TestDescriptor, TestInfo> = mutableMapOf()

	override fun beforeSuite(suite: TestDescriptor) {
		lookup[suite] = TestInfo(suite)
	}

	override fun beforeTest(testDescriptor: TestDescriptor) {
		lookup[testDescriptor] = TestInfo(testDescriptor)
	}

	override fun onOutput(testDescriptor: TestDescriptor, outputEvent: TestOutputEvent) {
		val info = lookup.getValue(testDescriptor)
		when (outputEvent.destination!!) {
			TestOutputEvent.Destination.StdOut -> info.stdOut.append(outputEvent.message)
			TestOutputEvent.Destination.StdErr -> info.stdErr.append(outputEvent.message)
		}
	}

	override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
		logResults("test", testDescriptor, result)
	}

	override fun afterSuite(suite: TestDescriptor, result: TestResult) {
		logResults("suite", suite, result)
	}

	private fun logResults(testType: String, descriptor: TestDescriptor, result: TestResult) {
		val info = lookup.remove(descriptor) ?: error("Descriptor ${descriptor} was not in ${lookup}")
		val hasStdOut = info.stdOut.isNotEmpty()
		val hasStdErr = info.stdErr.isNotEmpty()
		val hasError = result.exception != null

		if (!descriptor.isLoggable(hasStdOut || hasStdErr || hasError)) return

		fun id(outputType: String): String =
			"${testType}_${outputType}_${descriptor.toString().hashCode().absoluteValue}"

		val fullName = "${descriptor.groupName} > ${descriptor.name}"
		logger.quiet("${fullName} ${result.resultType}")
		logger.fold(id("ex"), hasError) {
			quiet("EXCEPTION ${fullName}")
			@Suppress("UnsafeCallOnNullableType") // guarded by hasError, but not provable by contract {}.
			quiet(result.exception!!.stackTraceToString())
		}
		logger.fold(id("out"), hasStdOut) {
			quiet("STANDARD_OUT ${fullName}")
			quiet(info.stdOut.toString())
		}
		logger.fold(id("err"), hasStdErr) {
			quiet("STANDARD_ERR ${fullName}")
			quiet(info.stdErr.toString())
		}
	}

	private data class TestInfo(
		val descriptor: TestDescriptor,
		val stdOut: StringBuilder = StringBuilder(),
		val stdErr: StringBuilder = StringBuilder(),
	)
}

private fun TestDescriptor.isLoggable(hasOutput: Boolean): Boolean =
	hasOutput || when (inferredType) {
		GroupType.Suite_Gradle_Executor -> false // Don't log, this is because of concurrency.
		GroupType.Suite_Gradle_Run -> false // Don't log, this is because of Gradle's system.
		GroupType.Class -> false // Don't log, individual tests are enough.
		GroupType.Suite -> false // Don't log, individual tests are enough.
		GroupType.Test -> true
	}

private val TestDescriptor.groupName: String
	get() = when (inferredType) {
		GroupType.Suite_Gradle_Executor,
		GroupType.Suite_Gradle_Run,
		GroupType.Suite -> "Suite"
		GroupType.Class -> "Class"
		GroupType.Test -> className ?: "null"
	}

private val TestDescriptor.inferredType: GroupType
	get() = when (this.className) {
		null -> when {
			this.name.startsWith("Gradle Test Executor") -> GroupType.Suite_Gradle_Executor
			this.name.startsWith("Gradle Test Run") -> GroupType.Suite_Gradle_Run
			else -> GroupType.Suite
		}

		this.name -> GroupType.Class
		else -> GroupType.Test
	}

private enum class GroupType {
	Suite_Gradle_Executor,
	Suite_Gradle_Run,
	Suite,
	Class,
	Test,
}

private fun Logger.fold(id: String, condition: Boolean = true, output: Logger.() -> Unit) {
	if (condition) {
		this.quiet("::group::${id}")
		this.output()
		this.quiet("::endgroup:: ")
	}
}
