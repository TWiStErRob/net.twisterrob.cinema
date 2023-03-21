package net.twisterrob.cinema.build.dsl

import org.gradle.api.Named
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

