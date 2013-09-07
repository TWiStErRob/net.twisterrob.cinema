package net.twisterrob.cinema.gapp.model;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import javax.jdo.PersistenceManager;

import net.twisterrob.cinema.gapp.dal.PMF;
import net.twisterrob.test.mock.easymock.EasyMocks;

import org.joda.time.*;
import org.junit.*;
import org.slf4j.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.*;
public class DateFieldsTest {
	private static final Logger LOG = LoggerFactory.getLogger(DateFieldsTest.class);

	private static final String INITIAL_NAME = "name1";
	private static final String CHANGED_NAME = "name2";

	@Rule
	// https://github.com/junit-team/junit/wiki/Rules
	public EasyMocks control = EasyMocks.createMockFactory();

	/**
	 * {@link https://developers.google.com/appengine/docs/java/tools/localunittesting}
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
			.setEnvIsAdmin(false).setEnvIsLoggedIn(false);

	@Before
	public void setUp() throws Throwable {
		helper.setUp();

		control.replay();
	}

	@After
	public void tearDown() throws Throwable {
		helper.tearDown();
	}

	@Test
	public void testDS() throws InterruptedException {
		DateTime startDateReference = new DateTime();
		Key targetKey;

		DateFieldsTestObject target = new DateFieldsTestObject(12345, INITIAL_NAME);
		assertEquals(INITIAL_NAME, target.getName());
		DateTime createdBeforeFirstPersistence = target.getCreated();
		DateTime lastUpdatedBeforeFirstPersistence = target.getLastUpdated();

		// create
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(target);
		assertEquals(INITIAL_NAME, target.getName());
		assertNotNull(targetKey = target.getKey());
		DateTime createdAfterFirstPersistence = target.getCreated();
		DateTime lastUpdatedAfterFirstPersistence = target.getLastUpdated();

		assertNull(lastUpdatedBeforeFirstPersistence);
		assertNotNull(lastUpdatedAfterFirstPersistence);
		assertThat(createdBeforeFirstPersistence, greaterThan((ReadableInstant)startDateReference));
		assertEquals(createdBeforeFirstPersistence, createdAfterFirstPersistence);
		assertThat(createdAfterFirstPersistence, lessThan((ReadableInstant)lastUpdatedAfterFirstPersistence));

		// read and modify
		target = pm.getObjectById(DateFieldsTestObject.class, targetKey);
		DateTime createdAfterFirstRead = target.getCreated();
		DateTime lastUpdatedAfterFirstRead = target.getLastUpdated();
		assertNotEquals(INITIAL_NAME, CHANGED_NAME);
		target.setName(CHANGED_NAME);
		assertEquals(CHANGED_NAME, target.getName());
		DateTime createdAfterModification = target.getCreated();
		DateTime lastUpdatedAfterModification = target.getLastUpdated();
		pm.makePersistent(target);
		assertEquals(CHANGED_NAME, target.getName());
		DateTime createdAfterModificationPersistence = target.getCreated();
		DateTime lastUpdatedAfterModificationPersistence = target.getLastUpdated();

		assertEquals(createdBeforeFirstPersistence, createdAfterFirstRead);
		assertEquals(createdBeforeFirstPersistence, createdAfterModification);
		assertEquals(createdBeforeFirstPersistence, createdAfterModificationPersistence);
		assertEquals(lastUpdatedAfterFirstPersistence, lastUpdatedAfterFirstRead);
		assertThat(lastUpdatedAfterFirstRead, lessThan((ReadableInstant)lastUpdatedAfterModificationPersistence));
		assertThat(lastUpdatedAfterFirstRead, lessThanOrEqualTo((ReadableInstant)lastUpdatedAfterModification));
		assertThat(lastUpdatedAfterModification,
				lessThanOrEqualTo((ReadableInstant)lastUpdatedAfterModificationPersistence));

		// read again
		target = pm.getObjectById(DateFieldsTestObject.class, targetKey);
		assertEquals(CHANGED_NAME, target.getName());
		DateTime createdAfterReRead = target.getCreated();
		DateTime lastUpdatedAfterReRead = target.getLastUpdated();
		assertEquals(createdBeforeFirstPersistence, createdAfterReRead);
		assertEquals(lastUpdatedAfterReRead, lastUpdatedAfterModificationPersistence);
	}

	@SuppressWarnings("unused")
	private static void output(String why, DateFieldsTestObject obj) {
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSS");
		LOG.info("{}: id={}@{}, name={}, created={}, lastUpdated={}", //
				String.format("%16s", why), // reason
				obj.getKey(), // id
				String.format("%08x", obj.hashCode()), // instance id
				obj.getName(), // name
				obj.getCreated() != null? fmt.format(obj.getCreated().toDate()) : "not-set", // created
				obj.getLastUpdated() != null? fmt.format(obj.getLastUpdated().toDate()) : "not-set" // last update
		);
	}
}
