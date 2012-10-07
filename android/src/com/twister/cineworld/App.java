package com.twister.cineworld;

import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.accessor.impl.*;

public class App extends android.app.Application {
	private static/* final */App	s_instance;

	public App() {
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	private DataBaseHelper	m_dataBaseHelper	= new DataBaseHelper(this);

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
}
