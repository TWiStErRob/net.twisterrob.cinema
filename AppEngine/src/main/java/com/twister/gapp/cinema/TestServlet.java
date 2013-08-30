package com.twister.gapp.cinema;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.jdo.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Lists;
import com.twister.gapp.PMF;
import com.twister.gapp.cinema.model.TestDatesFields;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(TestServlet.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		testDateFields();
	}

	protected void testDateFields() {
		{ // clean
			PersistenceManager pm = getPM();
			try {
				Extent<TestDatesFields> obj = pm.getExtent(TestDatesFields.class);
				pm.deletePersistentAll(Lists.newArrayList(obj.iterator()));
			} catch (JDOObjectNotFoundException ex) {
				// already clean
			} finally {
				pm.close();
			}
		}
		sleep(100);
		Key k = null;
		{ // write
			PersistenceManager pm = getPM();
			try {
				TestDatesFields obj = new TestDatesFields(12345, "name1");
				output("New", obj);
				pm.makePersistent(obj);
				k = obj.getKey();
				output("Persisted", obj);
			} finally {
				pm.close();
			}
		}
		sleep(100);
		{ // read and modify
			PersistenceManager pm = getPM();
			try {
				TestDatesFields obj = pm.getObjectById(TestDatesFields.class, k);
				output("Loaded", obj);
				obj.setName("name2");
				output("Modified", obj);
				pm.makePersistent(obj);
				output("ModifiedPersisted", obj);
			} finally {
				pm.close();
			}
		}
		sleep(100);
		{ // read again
			PersistenceManager pm = getPM();
			try {
				TestDatesFields obj = pm.getObjectById(TestDatesFields.class, k);
				output("ModifiedLoaded", obj);
			} finally {
				pm.close();
			}
		}
	}

	protected PersistenceManager getPM() {
		PersistenceManager pm = PMF.getPM();
		//		pm.addInstanceLifecycleListener(new StoreLifecycleListener() {
		//			@Override
		//			public void preStore(InstanceLifecycleEvent event) {
		//				TestDatesFields entity = (TestDatesFields)event.getPersistentInstance();
		//				entity.setLastUpdate(new Date());
		//			}
		//
		//			@Override
		//			public void postStore(InstanceLifecycleEvent event) {}
		//		}, TestDatesFields.class);
		return pm;
	}

	private void sleep(long millies) {
		try {
			Thread.sleep(millies);
		} catch (InterruptedException ex) {}
	}

	private void output(String why, TestDatesFields obj) {
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
	@SuppressWarnings("unused")
	private void log() {
		org.slf4j.Logger slf4j = org.slf4j.LoggerFactory.getLogger(ListingFilms.class);
		slf4j.trace("message-slf4j-trace");
		slf4j.debug("message-slf4j-debug");
		slf4j.info("message-slf4j-info");
		slf4j.warn("message-slf4j-warn");
		slf4j.error("message-slf4j-error");

		java.util.logging.Logger jul = java.util.logging.Logger.getLogger(ListingFilms.class.getName());
		jul.severe("message-jul-severe");
		jul.warning("message-jul-warning");
		jul.info("message-jul-info");
		jul.config("message-jul-config");
		jul.fine("message-jul-fine");
		jul.finer("message-jul-finer");
		jul.finest("message-jul-finest");
	}
}
