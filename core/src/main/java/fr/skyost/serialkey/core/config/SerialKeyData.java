package fr.skyost.serialkey.core.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import fr.skyost.serialkey.core.object.SerialKeyLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Allows to collect and save padlocks.
 */

public interface SerialKeyData {

	/**
	 * Returns the raw data.
	 *
	 * @return The raw data.
	 */

	Collection<String> getData();

	/**
	 * Returns the padlocks.
	 *
	 * @return The padlocks.
	 */

	default Collection<SerialKeyLocation> getPadlocks() {
		final HashSet<SerialKeyLocation> result = new HashSet<>();
		for(final String padlock : getData()) {
			result.add(locationFromJson(padlock));
		}
		return result;
	}

	/**
	 * Adds a padlock.
	 *
	 * @param location The padlock location.
	 */

	default void addPadlock(final SerialKeyLocation location) {
		getData().add(locationToJson(location));
	}

	/**
	 * Removes a padlock.
	 *
	 * @param location The padlock location.
	 *
	 * @return Whether a padlock has been removed.
	 */

	default boolean removePadlock(final SerialKeyLocation location) {
		final Collection<String> data = getData();
		for(final String padlock : new ArrayList<>(data)) {
			if(locationFromJson(padlock).equals(location)) {
				data.remove(padlock);
				return true;
			}
		}

		return false;
	}

	/**
	 * Converts a JSON String to a SerialKey location.
	 *
	 * @param json The JSON String.
	 *
	 * @return The SerialKey location.
	 */

	default SerialKeyLocation locationFromJson(final String json) {
		final JsonObject representation = new JsonParser().parse(json).getAsJsonObject();
		return new SerialKeyLocation(representation.get("world").getAsString(), representation.get("x").getAsInt(), representation.get("y").getAsInt(), representation.get("z").getAsInt());
	}

	/**
	 * Converts a SerialKey location to a JSON String.
	 *
	 * @param location The SerialKey location.
	 *
	 * @return The JSON String.
	 */

	default String locationToJson(final SerialKeyLocation location) {
		final JsonObject representation = new JsonObject();
		representation.add("world", new JsonPrimitive(location.getWorld()));
		representation.add("x", new JsonPrimitive(location.getX()));
		representation.add("y", new JsonPrimitive(location.getY()));
		representation.add("z", new JsonPrimitive(location.getZ()));
		return representation.toString();
	}

}