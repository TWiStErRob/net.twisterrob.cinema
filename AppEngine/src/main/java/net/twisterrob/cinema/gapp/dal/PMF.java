package net.twisterrob.cinema.gapp.dal;
import javax.jdo.*;

import net.twisterrob.cinema.gapp.model.User;

import org.slf4j.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Lists;

public final class PMF {
	private static final Logger LOG = LoggerFactory.getLogger(PMF.class);

	/**
	 * Get instance from <em>jdoconfig.xml</em>.<br>
	 * Defined as: <code>&lt;persistence-manager-factory name="{arg-to-getPersistenceManagerFactory}">...</code>
	 */
	private static final PersistenceManagerFactory pmfInstance = //
	JDOHelper.getPersistenceManagerFactory("net.twisterrob.cinema-ds-jdo");

	private PMF() {}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	public static PersistenceManager getPM() {
		LOG.debug("Getting a PersistenceManager");
		PersistenceManager pm = get().getPersistenceManager();
		return pm;
	}

	public static User getCurrentUser() {
		com.google.appengine.api.users.User authUser = UserServiceFactory.getUserService().getCurrentUser();
		if (authUser == null) {
			return null;
		}

		try {
			return getById(User.class, authUser.getUserId());
		} catch (JDOObjectNotFoundException ex) {
			return registerUser(authUser);
		}
	}

	public static String getCurrentUserId() {
		com.google.appengine.api.users.User authUser = UserServiceFactory.getUserService().getCurrentUser();
		return authUser != null? authUser.getUserId() : null;
	}

	protected static User registerUser(com.google.appengine.api.users.User authUser) {
		PersistenceManager pm = getPM();
		try {
			// TODO register user properly
			User user = new User(authUser.getUserId(), authUser.getEmail(), authUser.getNickname());
			user = pm.makePersistent(user);
			return pm.detachCopy(user);
		} finally {
			pm.close();
		}
	}

	public static void clear(Class<?> clazz) {
		PersistenceManager pm = PMF.getPM();
		try {
			Extent<?> obj = pm.getExtent(clazz);
			pm.deletePersistentAll(Lists.newArrayList(obj.iterator()));
		} catch (JDOObjectNotFoundException ex) {
			// already clean
		} finally {
			pm.close();
		}
	}
	public static void clear(String entityName) {
		LOG.info("Clearing all {}", entityName);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		com.google.appengine.api.datastore.Query cdb = new com.google.appengine.api.datastore.Query(entityName);
		cdb.setKeysOnly();

		Iterable<Entity> results = datastore.prepare(cdb).asIterable();
		for (Entity entity: results) {
			datastore.delete(entity.getKey());
		}
	}

	public static <T> T getById(Class<T> clazz, Object key) {
		PersistenceManager pm = getPM();
		try {
			T entity = pm.getObjectById(clazz, key);
			return pm.detachCopy(entity);
		} finally {
			pm.close();
		}
	}
}