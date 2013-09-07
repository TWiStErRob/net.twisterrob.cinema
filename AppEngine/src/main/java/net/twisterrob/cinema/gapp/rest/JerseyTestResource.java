package net.twisterrob.cinema.gapp.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.appengine.api.datastore.*;

@Path("/test")
public class JerseyTestResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public KeyHolder test() {
		Key parent = KeyFactory.createKey("SubKey", 12345);
		Key child = KeyFactory.createKey(parent, "SuperKey", "KeyName");
		return new KeyHolder(child);
	}

	private static class KeyHolder {
		@XmlElement(namespace = "http://appengine.google.com/datastore")
		@XmlJavaTypeAdapter(net.twisterrob.utils.datastore.KeyAdapter.class)
		private com.google.appengine.api.datastore.Key key;

		/**
		 * This method is a gift to JAXB.
		 */
		@SuppressWarnings("unused")
		private KeyHolder() {}

		public KeyHolder(com.google.appengine.api.datastore.Key key) {
			this.key = key;
		}
	}
}
