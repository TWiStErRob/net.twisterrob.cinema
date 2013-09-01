package net.twisterrob.cinema.gapp.rest;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import net.twisterrob.cinema.gapp.model.View;
import net.twisterrob.cinema.gapp.services.*;
import net.twisterrob.utils.guava.Functions;

import org.easymock.*;
import org.junit.*;
import org.junit.rules.ExpectedException;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.test.framework.*;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly2.web.GrizzlyWebTestContainerFactory;

public class JerseyHelloTest extends JerseyTest {
	private IMocksControl m_control;
	private FilmService m_viewService;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		m_control = EasyMock.createControl();
		m_viewService = m_control.createMock(FilmService.class);
		MockViewServiceProvider.mockService = m_viewService;
	}

	@Override
	public WebAppDescriptor configure() {
		List<Class<?>> classes = ImmutableList.of( //
				JerseyHello.class, MockViewServiceProvider.class, NotFoundMapper.class);
		String classNames = Joiner.on(';').join(Collections2.transform(classes, Functions.className()));
		return new WebAppDescriptor.Builder()
				.initParam(WebComponent.RESOURCE_CONFIG_CLASS, ClassNamesResourceConfig.class.getName())
				.initParam(ClassNamesResourceConfig.PROPERTY_CLASSNAMES, classNames).build();
	}

	@Override
	public TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}

	// until the test dependency version is updated too
	@Ignore
	@Test
	public void removeTodoShouldThrowNotFoundException() throws ServiceException {
		EasyMock.expect(m_viewService.getAllFilms()).andThrow(new JDOObjectNotFoundException("view-not-found"));
		exception.expect(UniformInterfaceException.class);

		m_control.replay();
		try {
			@SuppressWarnings("unused")
			View result = resource().path("/helloworld/").get(View.class);
		} catch (UniformInterfaceException ex) {
			Assert.assertEquals(Status.NOT_FOUND, ex.getResponse().getClientResponseStatus());
			Assert.assertEquals("view-not-found", ex.getResponse().getEntity(String.class));
			throw ex;
		}
		m_control.verify();
	}

	@Provider
	public static class MockViewServiceProvider extends SingletonTypeInjectableProvider<Context, FilmService> {
		private static FilmService mockService;
		public MockViewServiceProvider() {
			super(FilmService.class, mockService);
		}
	}
}
