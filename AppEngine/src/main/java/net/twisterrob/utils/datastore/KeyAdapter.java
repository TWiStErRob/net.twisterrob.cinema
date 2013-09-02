package net.twisterrob.utils.datastore;

import java.util.*;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.appengine.api.datastore.Key;

public class KeyAdapter extends XmlAdapter<Map<String, Object>, Key> {
	@Override
	public Key unmarshal(Map<String, Object> v) throws Exception {
		// TODO implement
		return null;
	}
	@Override
	public Map<String, Object> marshal(Key v) throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("kind", v.getKind());
		if (v.getId() != 0) {
			map.put("id", v.getId());
		}
		if (v.getName() != null) {
			map.put("name", v.getName());
		}
		if (v.getNamespace() != null) {
			map.put("namespace", v.getNamespace());
		}
		if (v.getParent() != null) {
			map.put("parent", marshal(v.getParent()));
		}
		return map;
	}
}
