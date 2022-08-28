/**
 * Let's give Gradle one to chew on.
 * It's clearly broken since it's not a plugin, but it makes the warning disappear.
 *
 * The warnings were:
 * ```
 * :buildSrc:jar: No valid plugin descriptors were found in META-INF/gradle-plugins
 * :buildSrc:jar: A valid plugin descriptor was found for dummy.properties but the implementation class DummyPlugin was not found in the jar.
 * ```
 */
@Suppress("UnusedPrivateClass")
private class DummyPlugin
