import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

fun Task.notDependsOn(predicate: (String) -> Boolean) {
	this.setDependsOn(
		this.dependsOn
			.filterNot { it is TaskProvider<*> && predicate(it.name) }
			// Since Gradle 7.3 NamedDomainObjectCreatingProvider<TestSuite> contains "test", not a TaskProvider.
			.filterNot { it is Named && predicate(it.name) }
	)
}

val Project.slug: String
	get() =
		if (this == rootProject) {
			// Special case, since the other case removes leading ":".
			"root"
		} else {
			this
				.path // Project's Gradle path -> ":a:b".
				.removePrefix(":") // Remove first colon -> "a:b".
				.replace(":", "-") // Convert to Maven coordinate convention -> "a-b".
		}
