package net.twisterrob.cinema.gapp.rest.config;

import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApplication extends ResourceConfig {
	public JerseyApplication() {
		register(new ServiceBinder());
		packages("net.twisterrob.cinema.gapp.rest");
	}
}
