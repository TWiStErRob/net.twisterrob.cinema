package net.twisterrob.cinema.build.logging

import org.gradle.api.Project
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withModule

/**
 * Catch this error during runtime:
 * ```
 * SLF4J: Class path contains multiple SLF4J bindings.
 * SLF4J: Found binding in [.../log4j-slf4j-impl-2.14.1.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: Found binding in [.../slf4j-nop-1.7.30.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
 * SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
 * ```
 * 
 * See https://github.com/neo4j/neo4j/issues/12770
 */
fun Project.configureSLF4JBindings() {
	dependencies {
		components {
			withModule<SLF4JBindingCapability>("org.slf4j:slf4j-simple") // SLF4J Simple logger
			withModule<SLF4JBindingCapability>("org.slf4j:slf4j-nop") // SLF4J No op logger
			withModule<SLF4JBindingCapability>("org.slf4j:slf4j-jdk14") // JUL / Java Util Logging
			withModule<SLF4JBindingCapability>("org.slf4j:slf4j-log4j12") // Log4J 1.x
			withModule<SLF4JBindingCapability>("org.slf4j:slf4j-reload4j") // reload4j
			withModule<SLF4JBindingCapability>("org.apache.logging.log4j:log4j-slf4j-impl")   // SLF4J 1.x to Log4J 2.x
			withModule<SLF4JBindingCapability>("org.apache.logging.log4j:log4j-slf4j18-impl") // SLF4J 1.8 to Log4J 2.x
			withModule<SLF4JBindingCapability>("org.apache.logging.log4j:log4j-slf4j2-impl") // SLF4J 2.x to Log4J 2.x
			withModule<SLF4JBindingCapability>("ch.qos.logback:logback-classic") // Logback 1.x (SLF4J reference implementation)
		}
	}
	@Suppress("NestedScopeFunctions") // It's a DSL, it's supposed to be nested.
	configurations.all @Suppress("LabeledExpression") configuration@{
		resolutionStrategy.capabilitiesResolution.withCapability("org.slf4j:org.slf4j.impl.StaticLoggerBinder") {
			because("This project uses SLF4J 2.x over Log4J 2.x.")
			logger.info(
				"Capability conflict resolution for ${this@configuration}, candidates for ${capability} are\n" +
						candidates.joinToString(separator = "\n") { " * $it" }
			)
			val slf4jLogger = candidates.singleOrNull { candidate ->
				candidate.id.isMatching("org.apache.logging.log4j", "log4j-slf4j2-impl")
			}
			if (slf4jLogger != null) {
				logger.info("In ${this@configuration}, the resolved candidate for ${capability} is ${slf4jLogger}.")
				select(slf4jLogger)
			}
		}
	}
}

private fun ComponentIdentifier.isMatching(group: String, module: String): Boolean =
	this is ModuleComponentIdentifier && this.group == group && this.module == module

class SLF4JBindingCapability : ComponentMetadataRule {

	override fun execute(context: ComponentMetadataContext) {
		@Suppress("NestedScopeFunctions") // It's a DSL, it's supposed to be nested.
		context.details.run {
			allVariants {
				withCapabilities {
					addCapability("org.slf4j", "org.slf4j.impl.StaticLoggerBinder", "${id.name}-${id.version}")
				}
			}
		}
	}
}
