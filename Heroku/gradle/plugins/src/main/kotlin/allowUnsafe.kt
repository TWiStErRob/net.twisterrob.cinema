import org.gradle.api.JavaVersion
import org.gradle.api.tasks.testing.Test

// https://github.com/neo4j/neo4j/issues/12712 which one is this for?
// https://github.com/neo4j/neo4j/issues/12932 which one is this for?
/**
 * See also https://nipafx.dev/five-command-line-options-hack-java-module-system/
 */
fun Test.allowUnsafe() {
	if (JavaVersion.current() < JavaVersion.VERSION_1_9) return
	jvmArgs("--illegal-access=debug")
	// WARNING: Illegal reflective access using Lookup on org.neo4j.memory.RuntimeInternals
	// (org.neo4j/neo4j-unsafe/4.2.0/neo4j-unsafe-4.2.0.jar)
	// to class java.lang.String
	addOpens("java.base/java.lang", "org.neo4j.unsafe")
	// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
	// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
	// to field sun.nio.ch.FileChannelImpl.positionLock
	addOpens("java.base/sun.nio.ch", "org.apache.commons.lang3@3.12.0")
	// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
	// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
	// to field java.io.FileDescriptor.fd
	addOpens("java.base/java.io", "org.apache.commons.lang3@3.12.0")
	// WARNING: Illegal reflective access by com.shazam.shazamcrest.CyclicReferenceDetector
	// (com.shazam/shazamcrest/0.11/shazamcrest-0.11.jar)
	// to field java.time.OffsetDateTime.serialVersionUID
	// to field java.time.zone.ZoneRules.serialVersionUID
	// to field java.net.URI.serialVersionUID
	addOpens("java.base/java.net", "shazamcrest")
	addOpens("java.base/java.time", "shazamcrest")
	addOpens("java.base/java.time.zone", "shazamcrest")
	// WARNING: Illegal reflective access by org.eclipse.collections.impl.utility.ArrayListIterate
	// (org.eclipse.collections/eclipse-collections/10.3.0//eclipse-collections-10.3.0.jar)
	// to field java.util.ArrayList.elementData
	addOpens("java.base/java.util", "org.eclipse.collections.impl")
}

/**
 * @param user Find out the user by running `jar --file <jar> --describe-module`.
 *
 * Example output:
 * ```
 * No module descriptor found. Derived automatic module.
 *
 * org.apache.commons.lang3@3.12.0 automatic
 * requires java.base mandated
 * contains org.apache.commons.lang3
 * contains ...
 * ```
 * From this the package name is before `@`: `org.apache.commons.lang3`
 */
private fun Test.addOpens(module: String, user: String = "ALL-UNNAMED") {
	jvmArgs("--add-opens", "$module=$user")
}
