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

@Suppress("ComplexMethod", "FunctionMaxLength", "CognitiveComplexMethod")
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

	private data class TestInfo(
		val descriptor: TestDescriptor,
		val stdOut: StringBuilder = StringBuilder(),
		val stdErr: StringBuilder = StringBuilder(),
	)

	private val lookup = mutableMapOf<TestDescriptor, TestInfo>()

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

	@Suppress("ReturnCount")
	private fun logResults(testType: String, descriptor: TestDescriptor, result: TestResult) {

		fun fold(outputType: String, condition: Boolean, output: () -> Unit) {
			val id = descriptor.toString().hashCode().absoluteValue
			if (condition) {
				logger.quiet("::group::${testType}_${outputType}_${id}")
				output()
				logger.quiet("::endgroup:: ")
			}
		}

		val info = lookup.remove(descriptor) ?: error("Descriptor ${descriptor} was not in ${lookup}")
		val hasStdOut = info.stdOut.isNotEmpty()
		val hasStdErr = info.stdErr.isNotEmpty()
		val hasError = result.exception != null
		val hasAnything = hasStdOut || hasStdErr || hasError

		val groupSuite = "Suite"
		val groupClass = "Class"
		val groupName = when (val className = descriptor.className) {
			null -> groupSuite
			descriptor.name -> groupClass
			else -> className
		}
		val name = descriptor.name
		val fullName = "${groupName} > ${name}"
		if (groupName == groupSuite && name.startsWith("Gradle Test Executor") && !hasAnything) {
			// Don't log, this is because of concurrency.
			return
		} else if (groupName == groupSuite && name.startsWith("Gradle Test Run") && !hasAnything) {
			// Don't log, this is because of Gradle's system.
			return
		} else if (groupName == groupClass && !hasAnything) {
			// Don't log, individual tests are enough.
			return
		}

		logger.quiet("${fullName} ${result.resultType}")

		fold("ex", hasError) {
			logger.quiet("EXCEPTION ${fullName}")
			@Suppress("UnsafeCallOnNullableType") // guarded by hasError, but not provable by contract {}.
			result.exception!!.printStackTrace()
		}
		fold("out", hasStdOut) {
			logger.quiet("STANDARD_OUT ${fullName}")
			logger.quiet(info.stdOut.toString())
		}
		fold("err", hasStdErr) {
			logger.quiet("STANDARD_ERR ${fullName}")
			logger.quiet(info.stdErr.toString())
		}
	}
}
