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
}