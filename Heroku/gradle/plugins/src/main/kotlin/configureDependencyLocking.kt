import org.gradle.api.Project

/**
 * Call `gradlew :module:dependencies --write-locks` to generate lockfiles.
 * Tip: might need to change line endings to LF, see https://github.com/gradle/gradle/issues/19900.
 *
 * @see https://docs.gradle.org/current/userguide/dependency_locking.html
 */
@Suppress("KDocUnresolvedReference")
fun Project.configureDependencyLocking() {
	this.dependencyLocking {
		lockAllConfigurations()
		val fileName = this@configureDependencyLocking.slug
		/** @see org.gradle.internal.locking.LockFileReaderWriter.DEPENDENCY_LOCKING_FOLDER */
		lockFile.set(rootProject.file("gradle/dependency-locks/${fileName}.lockfile"))
	}
}
