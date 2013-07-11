package com.twister.cineworld.web;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <ul>
 * <li>java.lang.Math.max(2, 4):<br>
 * ${call.static['java.lang.Math'].set[2].set[4].invoke['max']}
 * <li>result.lookup(film.edi, cinema.id).size():<br>
 * ${fn:length(call.init[result].set[film.edi].set[cinema.id].invoke['lookup'])}
 * </ul>
 * 
 * @author Based on How to call methods from EL expressions- pre JSP 2.0 trick for JSPs with JSTL
 * @see http://technology.amis.nl/2005/06/15/how-to-call-methods-from-el-expressions-pre-jsp-20-trick-for-jsps-with-jstl
 * @author papp.robert.s
 * 
 */
public class InvokerMap implements Map<Object, Object> {
	private static enum Mode {
		Target, Parameter, Invoke, Invalid
	};
	private Mode mode = Mode.Invalid;

	private boolean isInstance;
	private Object target;
	private String methodName;
	private ArrayList<Object> args = new ArrayList<Object>();

	public Object get(Object key) {
		try {
			switch (mode) {
				case Target:
					target = key;
					mode = Mode.Invalid;
					break;
				case Parameter:
					args.add(key);
					mode = Mode.Invalid;
					break;
				case Invoke:
					methodName = String.valueOf(key);
					Class<?> clazz = isInstance? target.getClass() : Class.forName((String)target);
					Method[] methods = clazz.getMethods();
					Method targetMethod = null;
					for (Method method: methods) {
						if (method.getName().equals(methodName) && method.getParameterTypes().length == args.size()) {
							targetMethod = method;
							break;
						}
					}
					if (targetMethod == null) {
						error("No such method", null);
					}
					mode = Mode.Invalid;
					return targetMethod.invoke(target, args.toArray());
				case Invalid:
				default:
					if ("init".equals(key)) {
						// if init is passed, reinitializing for the next invocation
						target = null;
						methodName = null;
						args.clear();
						isInstance = true;
						mode = Mode.Target;
					} else if ("static".equals(key)) {
						target = null;
						methodName = null;
						args.clear();
						isInstance = false;
						mode = Mode.Target;
					} else if ("arg".equals(key)) {
						// if set is passed, we can expect the next call to get to pass the value of the next
						// attribute
						mode = Mode.Parameter;
					} else if ("invoke".equals(key)) {
						// if invoke is passed, the next call to get will pass the name of the method to call
						mode = Mode.Invoke;
					} else {
						error("I'm lost", null);
					}
					break;
			}
		} catch (Exception ex) {
			error("Something bad", ex);
		}
		return this;
	}

	private void error(String message, Throwable t) {
		StringBuilder call = new StringBuilder();
		call.append(String.valueOf(target));
		call.append('.');
		call.append(methodName);
		call.append('(');
		Iterator<Object> values = args.iterator();
		while (values.hasNext()) {
			Object arg = values.next();
			call.append(arg != null? arg.getClass() : "?");
			call.append(": ");
			call.append(arg);
			if (values.hasNext()) {
				call.append(", ");
			}
		}
		call.append(')');
		throw new IllegalArgumentException(message + ": " + call.toString(), t);
	}

	// Mandatory implementations for Map<Object, Object>

	public int size() {
		return 0;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean containsKey(Object key) {
		return true;
	}

	public boolean containsValue(Object value) {
		return true;
	}

	public Object put(Object key, Object value) {
		return null;
	}

	public Object remove(Object key) {
		return null;
	}

	public void putAll(Map<? extends Object, ? extends Object> t) {}

	public void clear() {}

	public Set<Object> keySet() {
		return null;
	}

	public Collection<Object> values() {
		return null;
	}

	public Set<Map.Entry<Object, Object>> entrySet() {
		return null;
	}

	public boolean equals(Object o) {
		return false;
	}

	public int hashCode() {
		return 0;
	}
}
