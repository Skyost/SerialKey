package fr.skyost.serialkey.core.unlocker;

import fr.skyost.serialkey.core.item.ItemManager;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.util.ROT47;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents an unlocker that depends on items lore.
 *
 * @param <T> ItemStack class.
 */

public abstract class LoreUnlocker<T> implements Unlocker<T> {

	/**
	 * The item manager.
	 */

	final ItemManager<T> itemManager;

	/**
	 * Creates a new lore unlocker instance.
	 *
	 * @param itemManager The item manager.
	 */

	public LoreUnlocker(final ItemManager<T> itemManager) {
		this.itemManager = itemManager;
	}

	@Override
	public List<SerialKeyLocation> getLocations(final T item) {
		return getLocations(item, isCipheringEnabled() ? ROT47::rotate : string -> string);
	}

	/**
	 * Returns the locations located in the item lore.
	 *
	 * @param item The item.
	 * @param stringProcessor The string processor.
	 *
	 * @return All locations located in the item lore.
	 */

	public List<SerialKeyLocation> getLocations(final T item, final Function<String, String> stringProcessor) {
		final List<SerialKeyLocation> result = new ArrayList<>();
		if(!itemManager.isLoreValid(item)) {
			return result;
		}

		final List<String> lore = itemManager.getLore(item);
		final int n = lore.size();

		for(int i = 0; i < n; i++) {
			result.add(new SerialKeyLocation(stringProcessor.apply(lore.get(i)), stringProcessor.apply(lore.get(++i))));
		}

		return result;
	}

	@Override
	public boolean canUnlock(final T item, final String world, final String position) {
		return canUnlock(item, world, position, isCipheringEnabled() ? ROT47::rotate : string -> string);
	}

	/**
	 * Returns whether the item can unlock the padlock located at the specified location.
	 *
	 * @param item The item.
	 * @param world The world.
	 * @param position The position.
	 * @param stringProcessor The string processor.
	 *
	 * @return Whether the item can unlock the padlock located at the specified location.
	 */

	protected boolean canUnlock(final T item, final String world, final String position, final Function<String, String> stringProcessor) {
		if(itemManager.isMasterKey(item)) {
			return true;
		}

		if(!itemManager.isLoreValid(item)) {
			return false;
		}

		final List<String> lore = itemManager.getLore(item);
		final int n = lore.size();
		for(int i = 0; i < n; i++) {
			if(stringProcessor.apply(lore.get(i)).equals(world) && stringProcessor.apply(lore.get(++i)).equals(position)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds a location to the specified collection item.
	 *
	 * @param collection The collection (bunch of keys for example).
	 * @param item The item.
	 */

	public void addLocation(final T collection, final T item) {
		if(!itemManager.isLoreValid(item)) {
			return;
		}

		final List<String> lore = itemManager.getLore(collection);
		lore.addAll(itemManager.getLore(item));
		itemManager.setLore(collection, lore);
	}

	/**
	 * Adds a location to the specified item.
	 *
	 * @param item The item.
	 * @param location The location.
	 */

	public void addLocation(final T item, final SerialKeyLocation location) {
		addLocation(item, location.getWorld(), location.getPosition());
	}

	/**
	 * Adds a location to the specified item.
	 *
	 * @param item The item.
	 * @param world The world.
	 * @param position The position.
	 */

	protected void addLocation(final T item, final String world, final String position) {
		addLocation(item, world, position, isCipheringEnabled() ? ROT47::rotate : string -> string);
	}

	/**
	 * Adds a location to the specified item.
	 *
	 * @param item The item.
	 * @param world The world.
	 * @param position The position.
	 * @param stringProcessor The string processor.
	 */

	protected void addLocation(final T item, final String world, final String position, final Function<String, String> stringProcessor) {
		final List<String> lore = itemManager.getLore(item);
		lore.add(stringProcessor.apply(world));
		lore.add(stringProcessor.apply(position));
		itemManager.setLore(item, lore);
	}

	/**
	 * Removes a location from the specified item.
	 *
	 * @param item The item.
	 * @param location The location.
	 */

	public short removeLocation(final T item, final SerialKeyLocation location) {
		return removeLocation(item, location.getWorld(), location.getPosition());
	}

	/**
	 * Removes a location from the specified item.
	 *
	 * @param item The item.
	 * @param world The world.
	 * @param position The position.
	 */

	protected short removeLocation(final T item, final String world, final String position) {
		return removeLocation(item, world, position, isCipheringEnabled() ? ROT47::rotate : string -> string);
	}

	/**
	 * Removes a location from the specified item.
	 *
	 * @param item The item.
	 * @param world The world.
	 * @param position The position.
	 * @param stringProcessor The string processor.
	 */

	protected short removeLocation(final T item, final String world, final String position, final Function<String, String> stringProcessor) {
		if(!itemManager.isLoreValid(item)) {
			return 0;
		}

		final List<String> lore = itemManager.getLore(item);
		short count = 0;
		for(int i = 0; i < lore.size(); i++) {
			if(world.equals(stringProcessor.apply(lore.get(i))) && position.equals(stringProcessor.apply(lore.get(++i)))) {
				lore.remove(i);
				lore.remove(--i);
				i--;

				count++;
			}
		}
		itemManager.setLore(item, lore.isEmpty() ? null : lore);

		return count;
	}

	/**
	 * Clears all locations of the specified item.
	 *
	 * @param item The item.
	 */

	public void clearLocations(final T item) {
		itemManager.setLore(item, null);
	}

	/**
	 * Returns whether ROT47 ciphering should be enabled.
	 *
	 * @return Whether ROT47 ciphering should be enabled.
	 */

	public abstract boolean isCipheringEnabled();

}