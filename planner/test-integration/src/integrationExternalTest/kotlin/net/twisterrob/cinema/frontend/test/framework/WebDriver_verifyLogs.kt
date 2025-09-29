package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.WebDriver
import org.openqa.selenium.logging.LogEntry
import org.openqa.selenium.logging.LogType
import java.util.logging.Level

/**
 * Grab logs from Chrome console at the end of the test, so we can make sure there are no problems:
 *  * Chrome deprecations
 *  * JavaScript errors
 *  * JavaScript warnings
 *  * Angular deprecations
 *  * Leftover console.log() calls
 *  * etc.
 */
internal fun WebDriver.verifyLogs(log: (LogEntry) -> Unit = LogPrinter()::print) {
	// JavaScript running in the browser: console.log("hello");
	// Results in the following output to driver logs:
	// > [1690997212.256][DEBUG]: DevTools WebSocket Event: Runtime.consoleAPICalled (session_id=...) ... {
	// >   "args": [ {
	// >     "type": "string",
	// >     "value": "hello"
	// >   } ],
	// >   "executionContextId": 1,
	// >   "stackTrace": { ... }
	val logs = manage().logs().get(LogType.BROWSER).all
	val filteredLogs = logs
		.filterNot {
			// See https://www.chromestatus.com/feature/5636954674692096 for more details.
			it.level == Level.INFO && it.message.matches(Regex("""Slow network is detected\. .*Fallback font will be used while loading"""))
		}
	logs.forEach(log)
	assertThat(logs).isEmpty()
}
