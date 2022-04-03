import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.forceKotlinVersion() {
	afterEvaluate {
		dependencies {
			if ("implementation" in configurations.names) {
				add("implementation", platform(deps.Kotlin.bom))
			}
		}
	}
}
