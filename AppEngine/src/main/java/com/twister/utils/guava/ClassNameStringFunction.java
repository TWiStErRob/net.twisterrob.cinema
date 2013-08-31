package com.twister.utils.guava;

import com.google.common.base.Function;

public class ClassNameStringFunction implements Function<Class<?>, String> {
	@Override
	public String apply(Class<?> clazz) {
		return clazz.getName();
	}
}
