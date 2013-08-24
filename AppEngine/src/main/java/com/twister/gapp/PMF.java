package com.twister.gapp;
import javax.jdo.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserServiceFactory;
import com.twister.gapp.cinema.model.User;

public final class PMF {
	/**
	 * Get instance from <em>jdoconfig.xml</em>.<br>
	 * Defined as: <code>&lt;persistence-manager-factory name="{arg-to-getPersistenceManagerFactory}">...</code>
	 */
	private static final PersistenceManagerFactory pmfInstance = //
	JDOHelper.getPersistenceManagerFactory("com.twister.gapp-ds-jdo");

	private PMF() {}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	public static PersistenceManager getPM() {
		return get().getPersistenceManager();
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
				return null;
			}
			return pm.detachCopy(user);
		} finally {
			pm.close();
		}
	}

	public static void clear(String entityName) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		com.google.appengine.api.datastore.Query cdb = new com.google.appengine.api.datastore.Query(entityName);
		cdb.setKeysOnly();

		Iterable<Entity> results = datastore.prepare(cdb).asIterable();
		for (Entity entity: results) {
			datastore.delete(entity.getKey());
		}
	}
}