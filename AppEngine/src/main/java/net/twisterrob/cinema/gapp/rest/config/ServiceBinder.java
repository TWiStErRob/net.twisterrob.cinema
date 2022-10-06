package net.twisterrob.cinema.gapp.rest.config;

import net.twisterrob.cinema.gapp.services.*;
import net.twisterrob.cinema.gapp.services.impl.*;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ServiceBinder extends AbstractBinder {
	@Override
	protected void configure() {
		bind(FilmServiceImpl.class).to(FilmService.class);
		bind(CinemaServiceImpl.class).to(CinemaService.class);
	}
}
