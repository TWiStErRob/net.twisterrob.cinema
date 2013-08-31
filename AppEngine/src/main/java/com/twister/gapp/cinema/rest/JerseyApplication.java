package com.twister.gapp.cinema.rest;

import org.glassfish.jersey.server.ResourceConfig;

import com.twister.gapp.cinema.services.ServiceBinder;

public class JerseyApplication extends ResourceConfig {
	public JerseyApplication() {
		register(new ServiceBinder());
		packages("com.twister.gapp.cinema.rest");
	}
}
