@file:Suppress("unused")

package net.twisterrob.test

import com.flextrade.jfixture.ConstructorQuery
import com.flextrade.jfixture.builders.ClassToConstructorRelay
import com.flextrade.jfixture.customisation.Customisation
import com.flextrade.jfixture.specifications.AlwaysSpecification
import com.flextrade.jfixture.utility.comparators.ConstructorParameterCountComparator
import java.lang.reflect.Constructor
import java.lang.reflect.Parameter

private fun ignoreKotlinConstructors() = Customisation {
	val mostParameterConstructorRelay = ClassToConstructorRelay(MostParameterConstructorQuery(), AlwaysSpecification())
	it.addBuilderToStartOfPipeline(mostParameterConstructorRelay)
}

private class MostParameterConstructorQuery : ConstructorQuery {
	@Suppress("ReturnCount") // Cannot think of a better structure for now.
	override fun getConstructorsForClass(clazz: Class<*>): List<Constructor<*>>? {
		if (clazz.constructors.any(Constructor<*>::hasDefaultConstructorMarker)) {
			val constructor = clazz.constructors
				.filter(Constructor<*>::hasNoDefaultConstructorMarker)
				.sortedWith(ConstructorParameterCountComparator())
				.lastOrNull()
				?: return emptyList() // Oops, this class has no constructors.
			return listOf(constructor)
		} else {
			return null // We have nothing to do here, use default behavior.
		}
	}
}

/*
 * Kotlin generates a constructor with DefaultConstructorMarker as a parameter
 * which often has more parameters than any other genuinely useful constructors.
 */
private fun Parameter.isDefaultMarker(): Boolean =
	this.type.simpleName == "DefaultConstructorMarker"

private fun Constructor<*>.hasNoDefaultConstructorMarker(): Boolean =
	this.parameters.none(Parameter::isDefaultMarker)

private fun Constructor<*>.hasDefaultConstructorMarker(): Boolean =
	this.parameters.lastOrNull()?.isDefaultMarker() ?: false
