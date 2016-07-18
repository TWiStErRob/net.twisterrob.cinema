package net.twisterrob.utils.datastore;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.appengine.api.datastore.Key;

public class KeyAdapter extends XmlAdapter<String, Key> {
	@Override
	public Key unmarshal(String v) throws Exception {
		throw new UnsupportedOperationException("Cannot convert to " + Key.class);
	}

	@Override
	public String marshal(Key v) {
		return v.toString();
	}
}
