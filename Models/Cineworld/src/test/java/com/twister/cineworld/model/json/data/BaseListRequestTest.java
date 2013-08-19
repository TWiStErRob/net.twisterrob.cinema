package com.twister.cineworld.model.json.data;

import static org.easymock.EasyMock.createControl;
import static org.junit.Assert.*;

import java.net.URL;

import org.easymock.IMocksControl;
import org.junit.*;

import com.twister.cineworld.model.json.request.BaseListRequest;
import com.twister.cineworld.model.json.response.BaseListResponse;
import com.twister.test.junit.matchers.RegexMatcher;

public class BaseListRequestTest {
	private BaseListRequest<CineworldBase> target;
	private IMocksControl control;

	@Before
	public void setUp() throws Throwable {
		control = createControl();
		target = new BaseListRequest<CineworldBase>() {
			@Override
			public URL getURL() {
				return null;
			}

			@Override
			public String getRequestType() {
				return null;
			}

			@Override
			public Class<? extends BaseListResponse<CineworldBase>> getResponseClass() {
				return null;
			}
		};

		control.replay();
	}

	@After
	public void tearDown() throws Throwable {
		control.verify();
	}

	@Test
	public void testMakeUrlBasic() throws Throwable {
		String expectedPattern = "http://www\\.cineworld\\.co\\.uk/api/testType\\?.*&key1=value1&key2=value2";
		URL actual = target.makeUrl("testType", "key1=value1", "key2=value2");
		assertNotNull(actual);
		assertThat(actual.toString(), RegexMatcher.matchesPattern(expectedPattern));
	}
}
