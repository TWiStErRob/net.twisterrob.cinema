package com.twister.gapp;
import javax.jdo.*;

import com.google.appengine.api.users.UserServiceFactory;
import com.twister.gapp.cinema.model.User;

public final class PMF {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private PMF() {}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	public static PersistenceManager getPM() {
		return get().getPersistenceManager();
	}

	public static User getUser() {
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
				user = addUser(pm, authUser);
			}
			return pm.detachCopy(user);
		} finally {
			pm.close();
		}
	}

	private static User addUser(PersistenceManager pm, com.google.appengine.api.users.User authUser) {
		if (authUser == null) {
			return null;
		}
		User user = new User(authUser);
		pm.makePersistent(user);
		return user;
	}
}