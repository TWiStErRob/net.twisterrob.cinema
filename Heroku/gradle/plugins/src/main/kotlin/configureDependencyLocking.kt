import org.gradle.api.Project

/**
 * To generate lockfiles, call:
 *  * `gradlew :allDependencies --write-locks >all.txt 2>&1`
 *  * `gradlew :module:dependencies --write-locks >deps.txt 2>&1`
 *
 * Tip: if version controlled, might need to change line endings to LF,
 *      see https://github.com/gradle/gradle/issues/19900.
 *
 * @see https://docs.gradle.org/current/userguide/dependency_locking.html
 */
@Suppress("KDocUnresolvedReference")
fun Project.configureDependencyLocking() {
	this.dependencyLocking {
		configurations.configureEach {
			when {
				name.endsWith("DependenciesMetadata") -> {
					// Don't activate to reduce noise.
				}

				else -> {
					resolutionStrategy.activateDependencyLocking()
				}
			}
		}
		val fileName = this@configureDependencyLocking.slug
		/** @see org.gradle.internal.locking.LockFileReaderWriter.DEPENDENCY_LOCKING_FOLDER */
		lockFile.set(rootProject.file("gradle/dependency-locks/${fileName}.lockfile"))
	}
}
