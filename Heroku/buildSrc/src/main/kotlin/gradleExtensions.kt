import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

fun Task.notDependsOn(predicate: (TaskProvider<*>) -> Boolean) {
	this.setDependsOn(this.dependsOn.filterNot { it is TaskProvider<*> && predicate(it) })
}
