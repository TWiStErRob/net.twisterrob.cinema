import org.gradle.api.JavaVersion
import org.gradle.api.tasks.testing.Test

/**
 * https://github.com/neo4j/neo4j/issues/12712
 */
fun Test.allowUnsafe() {
	if (JavaVersion.current() < JavaVersion.VERSION_1_9) return
	// WARNING: Illegal reflective access using Lookup on org.neo4j.memory.RuntimeInternals
	// (org.neo4j/neo4j-unsafe/4.2.0/neo4j-unsafe-4.2.0.jar)
	// to class java.lang.String
	addOpens("java.base/java.lang")
	// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
	// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
	// to field sun.nio.ch.FileChannelImpl.positionLock
	addOpens("java.base/sun.nio.ch")
	// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
	// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
	// to field java.io.FileDescriptor.fd
	addOpens("java.base/java.io")
	// WARNING: Illegal reflective access using Lookup on org.neo4j.internal.unsafe.UnsafeUtil
	// (org.neo4j/neo4j-unsafe/4.4.12/neo4j-unsafe-4.4.12.jar)
	// to class java.nio.Buffer
	//
	// See https://github.com/neo4j/neo4j/issues/12932
	// java.lang.IllegalAccessException: module java.base does not open java.nio to unnamed module @7a35b0f5
	// at java.base/java.lang.invoke.MethodHandles.privateLookupIn(MethodHandles.java:259)
	// at org.neo4j.internal.unsafe.UnsafeUtil.<clinit>(UnsafeUtil.java:111)
	addOpens("java.base/java.nio")
	// WARNING: Illegal reflective access by com.shazam.shazamcrest.CyclicReferenceDetector
	// (com.shazam/shazamcrest/0.11/shazamcrest-0.11.jar)
	// to field java.time.OffsetDateTime.serialVersionUID
	// to field java.time.zone.ZoneRules.serialVersionUID
	// to field java.net.URI.serialVersionUID
	addOpens("java.base/java.net")
	addOpens("java.base/java.time")
	addOpens("java.base/java.time.zone")
	// WARNING: Illegal reflective access by org.eclipse.collections.impl.utility.ArrayListIterate
	// (org.eclipse.collections/eclipse-collections/10.3.0//eclipse-collections-10.3.0.jar)
	// to field java.util.ArrayList.elementData
	addOpens("java.base/java.util")
}

private fun Test.addOpens(module: String) {
	jvmArgs("--add-opens", "$module=ALL-UNNAMED")
}
