package net.twisterrob.test.mock.easymock;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.integration.EasyMock2Adapter;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * A factory for mocks, approximately equivalent to JMock's mockery (with a dash of JMock's Expectations class too).
 *
 * <p>
 * Also implements JUnit's MethodRule interface, so can automatically verify all created mocks.
 */
public class EasyMocks implements MethodRule {

	/**
	 * Factory to create mocks for interfaces only.
	 */
	public static EasyMocks createMockFactory() {
		return new EasyMocks(MockFactory.EASYMOCK);
	}

	////////////////////////////////////////////////

	/**
	 * Adapter to allow Hamcrest matchers to be used in expectations.
	 */
	public static <T> T with(Matcher<T> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return null;
	}

	public static boolean withBoolean(Matcher<Boolean> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return false;
	}

	public static byte withByte(Matcher<Byte> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return 0;
	}

	public static short withShort(Matcher<Short> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return 0;
	}

	public static int withInt(Matcher<Integer> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return 0;
	}

	public static long withLong(Matcher<Long> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return 0;
	}

	public static float withFloat(Matcher<Float> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return 0.0f;
	}

	public static double withDouble(Matcher<Double> matcher) {
		EasyMock2Adapter.adapt(matcher);
		return 0.0;
	}

	////////////////////////////////////////////////

	public <T> T mock(Class<T> cls) {
		return mock(null, cls);
	}
	public <T> T mockNice(Class<T> cls) {
		return mockNice(null, cls);
	}
	public <T> T mockStrict(Class<T> cls) {
		return mockStrict(null, cls);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T mock(String name, Class<T> cls) {
		ClassAndName classAndName = new ClassAndName(name, cls);
		Object mock = mocks.get(classAndName);
		if (mock == null) {
			mock = mockFactory.createMock(name, cls);
			mocks.put(classAndName, mock);
		}
		return (T)mock;
	}
	@SuppressWarnings("unchecked")
	public synchronized <T> T mockNice(String name, Class<T> cls) {
		ClassAndName classAndName = new ClassAndName(name, cls);
		Object mock = mocks.get(classAndName);
		if (mock == null) {
			mock = mockFactory.createNiceMock(name, cls);
			mocks.put(classAndName, mock);
		}
		return (T)mock;
	}
	@SuppressWarnings("unchecked")
	public synchronized <T> T mockStrict(String name, Class<T> cls) {
		ClassAndName classAndName = new ClassAndName(name, cls);
		Object mock = mocks.get(classAndName);
		if (mock == null) {
			mock = mockFactory.createStrictMock(name, cls);
			mocks.put(classAndName, mock);
		}
		return (T)mock;
	}

	public void replay() {
		for (Object mock: mocks.values()) {
			mockFactory.replay(mock);
		}
	}

	private void verifyMocks() {
		for (Object mock: mocks.values()) {
			mockFactory.verify(mock);
		}
	}

	////////////////////////////////////////////////

	/**
	 * JUnit's MethodRule implementation, automatically verifying any mocks.
	 */
	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
				verifyMocks();
				mocks.clear();
			}
		};
	}

	////////////////////////////////////////////////

	private EasyMocks(MockFactory mockFactory) {
		this.mockFactory = mockFactory;
	}

	private Map<ClassAndName, Object> mocks = new HashMap<>();
	private MockFactory mockFactory;
	@SuppressWarnings("unused")
	private enum MockFactory {
		EASYMOCK;
		public <T> T createMock(Class<T> cls) {
			return org.easymock.EasyMock.createMock(cls);
		}
		public <T> T createMock(String name, Class<T> cls) {
			return org.easymock.EasyMock.createMock(name, cls);
		}
		public <T> T createNiceMock(Class<T> cls) {
			return org.easymock.EasyMock.createNiceMock(cls);
		}
		public <T> T createNiceMock(String name, Class<T> cls) {
			return org.easymock.EasyMock.createNiceMock(name, cls);
		}
		public <T> T createStrictMock(Class<T> cls) {
			return org.easymock.EasyMock.createStrictMock(cls);
		}
		public <T> T createStrictMock(String name, Class<T> cls) {
			return org.easymock.EasyMock.createStrictMock(name, cls);
		}
		public void replay(Object mock) {
			org.easymock.EasyMock.replay(mock);
		}
		public void verify(Object mock) {
			org.easymock.EasyMock.verify(mock);
		}
	}

	private static class ClassAndName {
		private Class<?> cls;
		private String name;

		public ClassAndName(String name, Class<?> cls) {
			this.name = name;
			this.cls = cls;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cls == null)? 0 : cls.hashCode());
			result = prime * result + ((name == null)? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ClassAndName other = (ClassAndName)obj;
			if (cls == null) {
				if (other.cls != null) {
					return false;
				}
			} else if (cls != other.cls) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}
	}
}
