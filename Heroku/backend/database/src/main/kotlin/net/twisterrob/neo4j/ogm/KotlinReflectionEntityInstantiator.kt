package net.twisterrob.neo4j.ogm

import org.neo4j.ogm.exception.core.MappingException
import org.neo4j.ogm.session.EntityInstantiator
import java.util.Collections
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class KotlinReflectionEntityInstantiator : EntityInstantiator {

	override fun <T : Any> createInstance(clazz: Class<T>, propertyValues: Map<String, Any>): T {
		val allCtors = clazz.kotlin.constructors

		// try to use default ctor:
		allCtors.singleOrNull { it.parameters.isEmpty() }?.let { return it.callByDescriptive(Collections.emptyMap()) }

		// no default ctor..., try to find one to use
		val usableCtors = allCtors.filter { ctor ->
			ctor.parameters.all { param -> param.isOptional || propertyValues.containsKey(param.name) }
		}

		val ctor = usableCtors.singleOrNull() ?: throw MappingException(
			"Unable to find unique constructor to instantiate $clazz using ${propertyValues.keys}\n${
				allCtors.joinToString("\n") { ctor -> ctor.toMismatchString(ctor in usableCtors, propertyValues) }
			}"
		)
		val args = ctor.createArgsFrom(propertyValues)
		return ctor.callByDescriptive(args)
	}
}

private fun <T : Any> KFunction<T>.toMismatchString(usable: Boolean, propertyValues: Map<String, Any>): String =
	buildString {
		if (usable) {
			append("@UsableBasedOnParams ")
		}
		append(this@toMismatchString.toConstructorString())
		append(" ")
		append(mismatch(this@toMismatchString, propertyValues))
	}

private fun <T> mismatch(it: KFunction<T>, propertyValues: Map<String, Any>): String {
	val missing = it.parameters.filterNot { it.name in propertyValues.keys }
	val (optional, required) = missing.partition { it.isOptional }
	return "not provided: ${required.map { it.name }}, automatic: ${optional.map { it.name }}"
}

private fun <T : Any> KFunction<T>.createArgsFrom(params: Map<String, Any>): Map<KParameter, Any?> =
	// TODO default values shouldn't be required on constructor, nullable types will end up being null in the args
	parameters
		// create all parameters
		.associateBy({ it }, { params[it.name] })
		// use default value if value is missing (i.e. don't include value in args map)
		.filterNot { (param, value) ->
			param.isOptional && !param.type.isMarkedNullable && value == null
		}

private fun <T : Any> KFunction<T>.callByDescriptive(args: Map<KParameter, Any?>): T {
	try {
		return callBy(args)
	} catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
		throw MappingException(
			"Unable to call selected constructor ${toConstructorString(true)} using\n${args.toCallString()}",
			e
		)
	}
}

private fun <T> KFunction<T>.toConstructorString(returnType: Boolean = false) =
	parameters.joinToString(", ", "constructor(", if (returnType) ") : ${this.returnType}" else ")") {
		it.toParamString()
	}

private fun Map<KParameter, Any?>.toCallString(): String =
	entries.joinToString(separator = "\n") { (k, v) ->
		"\t\t${k.toParamString()} => $v as ${v?.javaClass}"
	}

private fun KParameter.toParamString(): String {
	val vararg = if (isVararg) "vararg " else ""
	val optional = if (isOptional) " = ?" else ""
	return "$vararg$name: $type$optional"
}
