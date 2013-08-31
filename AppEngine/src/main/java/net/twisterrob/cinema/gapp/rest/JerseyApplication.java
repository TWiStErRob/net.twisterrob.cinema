package net.twisterrob.cinema.gapp.rest;

import net.twisterrob.cinema.gapp.services.ServiceBinder;

import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApplication extends ResourceConfig {
	public JerseyApplication() {
		register(new ServiceBinder());
		packages("net.twisterrob.cinema.gapp.rest");
	}
}
