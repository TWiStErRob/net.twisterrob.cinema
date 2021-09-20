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
	jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
	// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
	// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
	// to field sun.nio.ch.FileChannelImpl.positionLock
	jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED")
	// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
	// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
	// to field java.io.FileDescriptor.fd
	jvmArgs("--add-opens", "java.base/java.io=ALL-UNNAMED")
	// WARNING: Illegal reflective access by com.shazam.shazamcrest.CyclicReferenceDetector
	// (com.shazam/shazamcrest/0.11/shazamcrest-0.11.jar)
	// to field java.time.OffsetDateTime.serialVersionUID
	// to field java.time.zone.ZoneRules.serialVersionUID
	// to field java.net.URI.serialVersionUID
	jvmArgs("--add-opens", "java.base/java.net=ALL-UNNAMED")
	jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
	jvmArgs("--add-opens", "java.base/java.time.zone=ALL-UNNAMED")
	// WARNING: Illegal reflective access by org.eclipse.collections.impl.utility.ArrayListIterate
	// (org.eclipse.collections/eclipse-collections/10.3.0//eclipse-collections-10.3.0.jar)
	// to field java.util.ArrayList.elementData
	jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
}
