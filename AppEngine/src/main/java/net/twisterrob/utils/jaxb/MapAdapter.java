package net.twisterrob.utils.jaxb;

import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

public class MapAdapter<K, V> extends XmlAdapter<MapAdapter.AdaptedMap<K, V>, Map<K, V>> {
	@Override
	public AdaptedMap<K, V> marshal(Map<K, V> map) throws Exception {
		if (map == null) {
			return null;
		}
		AdaptedMap<K, V> adaptedMap = new AdaptedMap<K, V>();
		for (Entry<K, V> entry: map.entrySet()) {
			AdaptedEntry<K, V> adaptedEntry = new AdaptedEntry<K, V>(entry.getKey(), entry.getValue());
			adaptedMap.entries.add(adaptedEntry);
		}
		return adaptedMap;
	}

	@Override
	public Map<K, V> unmarshal(AdaptedMap<K, V> adaptedMap) throws Exception {
		if (adaptedMap == null) {
			return null;
		}
		List<AdaptedEntry<K, V>> adaptedEntries = adaptedMap.entries;
		Map<K, V> map = new HashMap<K, V>(adaptedEntries.size());
		for (AdaptedEntry<K, V> adaptedEntry: adaptedEntries) {
			map.put(adaptedEntry.key, adaptedEntry.value);
		}
		return map;
	}

	public static class AdaptedMap<K, V> {
		/**
		 * This annotation ({@link XmlVariableNode}) requires <code>org.eclipse.persistence.moxy:2.5.1</code>, which is
		 * not yet available with <code>jersey-media-moxy:2.2</code> (it includes <code>2.5.0</code>).
		 * 
		 * @see http://stackoverflow.com/q/18666984/253468
		 */
		@XmlVariableNode("key")
		List<AdaptedEntry<K, V>> entries = new ArrayList<AdaptedEntry<K, V>>();
	}

	public static class AdaptedEntry<K, V> {
		public AdaptedEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@XmlTransient
		private K key;

		@XmlValue
		private V value;
	}
}
