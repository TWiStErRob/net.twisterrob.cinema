import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.LockMode
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault

/**
 * Call `gradlew resolveAndLockAll --write-locks` to update.
 * TODEL Then change line endings to LF, see https://github.com/gradle/gradle/issues/19900.
 *
 * @see https://docs.gradle.org/current/userguide/dependency_locking.html
 */
@Suppress("KDocUnresolvedReference")
fun Project.configureDependencyLocking() {
	configureDependencyLocking(
		"compileClasspath", "runtimeClasspath", "kapt",
		"testCompileClasspath", "testRuntimeClasspath", "kaptTest"
	)
}

fun Project.configureDependencyLocking(vararg lockWorthy: String) {
	this.configurations.activateDependencyLockingFor(lockWorthy.toSet())
	this.dependencyLocking {
		@Suppress("UnstableApiUsage")
		lockMode.set(LockMode.STRICT)

		val fileName = this@configureDependencyLocking.slug ?: "rootProject"
		/** @see org.gradle.internal.locking.LockFileReaderWriter.DEPENDENCY_LOCKING_FOLDER */
		lockFile.set(rootProject.file("gradle/dependency-locks/${fileName}.lockfile"))
	}
	this.tasks.register<ResolveAndLockAllTask>("resolveAndLockAll")
}

private fun ConfigurationContainer.activateDependencyLockingFor(lockWorthy: Collection<String>) {
	this.configureEach {
		if (name in lockWorthy) {
			resolutionStrategy.activateDependencyLocking()
		}
	}
}

/**
 * @see https://docs.gradle.org/current/userguide/dependency_locking.html#lock_all_configurations_in_one_build_execution
 */
@DisableCachingByDefault(because = "Resolution needs to happen every time this task executes.")
internal abstract class ResolveAndLockAllTask : DefaultTask() {

	companion object {

		/**
		 * TODEL in Gradle 8.
		 * > The * configuration has been deprecated for resolution.
		 * > This will fail with an error in Gradle 8.0.
		 * > Please resolve the compileClasspath or runtimeClasspath configuration instead.
		 * > Consult the upgrading guide for further information:
		 * > https://docs.gradle.org/7.4/userguide/upgrading_version_5.html#dependencies_should_no_longer_be_declared_using_the_compile_and_runtime_configurations
		 */
		private val resolutionDeprecated: Set<String> = setOf("archives", "default")
	}

	init {
		group = LifecycleBasePlugin.BUILD_GROUP
		description = "Lock all configurations in one build execution"
		outputs.upToDateWhen { false }
	}

	@TaskAction fun resolve() {
		require(project.gradle.startParameter.isWriteDependencyLocks) {
			"Please make sure to call ${this} with --write-locks parameter."
		}
		project.configurations
			.filterNot { it.name in resolutionDeprecated }
			.filter { it.isCanBeResolved }
			.forEach { it.resolve() }
	}
}
