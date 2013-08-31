package net.twisterrob.cinema.gapp.services;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ServiceBinder extends AbstractBinder {
	@Override
	protected void configure() {
		bind(ViewServiceImpl.class).to(ViewService.class);
	}
}
