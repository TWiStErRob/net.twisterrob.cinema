// TODEL https://github.com/gradle/gradle/issues/24409
@file:Suppress("UnusedImport", "UnusedImports")

package net.twisterrob.cinema.build.dependencies

import net.twisterrob.cinema.build.dsl.slug
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign

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
			@Suppress("UseIfInsteadOfWhen")
			when {
				name.endsWith("DependenciesMetadata") -> {
					// Don't activate to reduce noise.
				}

				else -> {
					// For all other configurations, activate blindly.
					resolutionStrategy.activateDependencyLocking()
				}
			}
		}
		val fileName = this@configureDependencyLocking.slug
		/** @see org.gradle.internal.locking.LockFileReaderWriter.DEPENDENCY_LOCKING_FOLDER */
		lockFile = rootProject.file("gradle/dependency-locks/${fileName}.lockfile")
	}
}
