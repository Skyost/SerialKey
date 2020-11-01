package fr.skyost.serialkey.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains some useful methods.
 */

public class Util {

	/**
	 * Returns the map which contains only the specified object.
	 *
	 * @param map The map.
	 * @param objects The object.
	 *
	 * @return A map which contains only the object (as keys).
	 */

	public static <V> Map<String, V> keepAll(final Map<String, V> map, final Collection<String> objects) {
		final Map<String, V> result = new HashMap<>();
		for(final String object : objects) {
			final char[] chars = new char[object.length()];
			object.getChars(0, object.length() > 3 ? 3 : object.length(), chars, 0);
			for(final char c : chars) {
				if(c == ' ') {
					continue;
				}
				final String character = String.valueOf(c);
				if(map.containsKey(character)) {
					result.put(character, map.get(character));
				}
			}
		}
		return result;
	}

	/**
	 * Creates a map.
	 *
	 * @param keys The keys.
	 * @param values The values.
	 *
	 * @return The map.
	 */

	public static <V, K> Map<K, V> createMap(final K[] keys, final V[] values) {
		if(keys.length != values.length) {
			return null;
		}
		final Map<K, V> map = new HashMap<>();
		for(int i = 0; i != keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}

}