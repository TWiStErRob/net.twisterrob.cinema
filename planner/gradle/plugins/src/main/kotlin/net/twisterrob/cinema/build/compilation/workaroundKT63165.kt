// There's no syntax for fully qualified extension functions: https://discuss.kotlinlang.org/t/18575
// So we have to suppress file level for the imports, this is why it's a separate file.
@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package net.twisterrob.cinema.build.compilation

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.diagnostics.CheckKotlinGradlePluginConfigurationErrors
import org.jetbrains.kotlin.gradle.plugin.diagnostics.kotlinToolingDiagnosticsCollectorProvider

// TODEL Workaround for https://youtrack.jetbrains.com/issue/KT-63165
// Everything involved is internal, so we need to suppress compile errors, or use reflection.
fun workaroundKT63165(project: Project) {
	project.tasks.withType<CheckKotlinGradlePluginConfigurationErrors>().configureEach {
		usesService(this.project.kotlinToolingDiagnosticsCollectorProvider)
	}
}
