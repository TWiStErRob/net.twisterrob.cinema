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
	beforeSuite(KotlinClosure1<TestDescriptor, Any>({
		lookup.put(this, TestInfo(this))
	}))
	beforeTest(KotlinClosure1<TestDescriptor, Any>({
		lookup.put(this, TestInfo(this))
	}))
	onOutput(KotlinClosure2({ descriptor: TestDescriptor, event: TestOutputEvent ->
		val info = lookup.getValue(descriptor)
		when (event.destination!!) {
			TestOutputEvent.Destination.StdOut -> info.stdOut.append(event.message)
			TestOutputEvent.Destination.StdErr -> info.stdErr.append(event.message)
		}
	}))
	fun logResults(testType: String, descriptor: TestDescriptor, result: TestResult) {
		val info = lookup.remove(descriptor)!!
		fun fold(outputType: String, condition: Boolean, output: () -> Unit) {
			val id = descriptor.toString().hashCode().absoluteValue
			if (condition) {
				println("::group::${testType}_${outputType}_${id}")
				output()
				println("::endgroup:: ")
			}
		}

		val groupName = when (val className = descriptor.className) {
			null -> "Suite"
			descriptor.name -> "Class"
			else -> className
		}
		val name = descriptor.name
		println("${groupName} > ${name} ${result.resultType}")

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
	}
	afterTest(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
		logResults("test", descriptor, result)
	}))
	afterSuite(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
		logResults("suite", descriptor, result)
	}))
}
