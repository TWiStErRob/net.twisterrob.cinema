import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestOutputEvent
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.KotlinClosure1
import org.gradle.kotlin.dsl.KotlinClosure2
import java.util.EnumSet
import kotlin.math.absoluteValue

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
	class TestInfo(
		val descriptor: TestDescriptor,
		val stdOut: StringBuilder = StringBuilder(),
		val stdErr: StringBuilder = StringBuilder()
	)

	val lookup = mutableMapOf<TestDescriptor, TestInfo>()
	beforeTest(KotlinClosure1<TestDescriptor, Any>({
		lookup.put(this, TestInfo(this))
	}))
	onOutput(KotlinClosure2({ descriptor: TestDescriptor, event: TestOutputEvent ->
		if (descriptor.name.startsWith("Gradle Test Executor")) {
			// Gradle Test Executor 155: Sep 04, 2021 12:26:40 PM org.junit.platform.launcher.core.EngineDiscoveryOrchestrator lambda$logTestDescriptorExclusionReasons$7
			// Gradle Test Executor 155: INFO: 0 containers and 14 tests were excluded because tags do not match tag expression(s): [integration]
			return@KotlinClosure2
		}
		val info = lookup.getValue(descriptor)
		when (event.destination!!) {
			TestOutputEvent.Destination.StdOut -> info.stdOut.append(event.message)
			TestOutputEvent.Destination.StdErr -> info.stdErr.append(event.message)
		}
	}))
	afterTest(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
		val info = lookup.remove(descriptor)!!
		fun fold(type: String, condition: Boolean, output: () -> Unit) {
			val id = descriptor.toString().hashCode().absoluteValue
			if (condition) {
				println("::group::test_${type}_${id}")
				output()
				println("::endgroup:: ")
			}
		}
		println("${descriptor.className} > ${descriptor.name} ${result.resultType}")
		fold("ex", result.exception != null) {
			result.exception!!.printStackTrace()
		}
		fold("out", info.stdOut.isNotEmpty()) {
			println("STANDARD_OUT")
			println(info.stdOut)
		}
		fold("err", info.stdErr.isNotEmpty()) {
			println("STANDARD_ERR")
			println(info.stdErr)
		}
	}))
}
