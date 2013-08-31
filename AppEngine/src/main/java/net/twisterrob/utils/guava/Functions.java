package net.twisterrob.utils.guava;

import com.google.common.base.Function;

public abstract class Functions {
	private Functions() {
		// prevent instantiation
	}
	public static Function<Class<?>, String> className() {
		return new ClassNameStringFunction();
	}
}
