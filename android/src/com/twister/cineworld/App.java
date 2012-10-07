package com.twister.cineworld;

import java.util.*;

import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.accessor.impl.*;
import com.twister.cineworld.tools.StringTools;
import com.twister.cineworld.ui.ProgressReporter;

public class App extends android.app.Application {
	private static/* final */App	s_instance;

	public App() {
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	private DataBaseHelper		m_dataBaseHelper	= new DataBaseHelper(this);
	private ProgressReporter	m_status;
	private static List<String>	s_logList			= new LinkedList<String>();

	public static void exit() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public DataBaseHelper getDataBaseHelper() {
		return m_dataBaseHelper;
	}

	public Accessor getCineworldAccessor() {
		// return new CineworldJSONAccessor();
		return new TrivialDBCacheAccessor(new DBAccessor(), new CineworldJSONAccessor());
	}

	public void setActiveStatusBar(final ProgressReporter status) {
		m_status = status;
	}

	public static void reportStatus(final String messageFormat, final Object... messageArgs) {
		String message = StringTools.format(messageFormat, messageArgs);
		ProgressReporter bar = App.getInstance().m_status;
		if (bar != null) {
			synchronized (s_logList) {
				s_logList.add(String.format("%tT: %s", Calendar.getInstance(), message));
				while (s_logList.size() > 5) {
					s_logList.remove(0);
				}
				message = StringTools.join(s_logList, "\n");
			}
			bar.reportStatus(message);
		}
	}
}
