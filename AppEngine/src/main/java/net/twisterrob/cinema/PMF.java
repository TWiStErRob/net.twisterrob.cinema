package net.twisterrob.cinema;
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
		PersistenceManager pm = getPM();
		try {
			User user;
			try {
				user = pm.getObjectById(User.class, authUser.getUserId());
			} catch (JDOObjectNotFoundException ex) {
				// TODO register user properly
				user = new User(authUser.getUserId(), authUser.getEmail(), authUser.getNickname());
				pm.makePersistent(user);
			}
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
}