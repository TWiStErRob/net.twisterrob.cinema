package net.twisterrob.utils.datastore;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.appengine.api.datastore.Key;

public class KeyAdapter extends XmlAdapter<String, Key> {
	@Override
	public Key unmarshal(String v) throws Exception {
		throw new OperationNotSupportedException("Cannot convert to " + Key.class);
	}

	@Override
	public String marshal(Key v) throws Exception {
		return v.toString();
	}
}
