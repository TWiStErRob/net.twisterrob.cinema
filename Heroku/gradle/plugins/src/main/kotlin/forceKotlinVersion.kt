import net.twisterrob.cinema.heroku.plugins.internal.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.forceKotlinVersion() {
	afterEvaluate {
		dependencies {
			if ("implementation" in configurations.names) {
				add("implementation", platform(libs.kotlin.bom))
			}
		}
	}
}