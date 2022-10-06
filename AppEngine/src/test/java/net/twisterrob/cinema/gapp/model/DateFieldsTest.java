package net.twisterrob.cinema.gapp.model;

import java.text.SimpleDateFormat;

import javax.jdo.PersistenceManager;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import net.twisterrob.cinema.gapp.dal.PMF;
import net.twisterrob.test.mock.easymock.EasyMocks;

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
	public void setUp() {
		helper.setUp();

		control.replay();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testDS() {
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
