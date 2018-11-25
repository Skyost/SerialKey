package fr.skyost.serialkey.core.unlocker;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;
import fr.skyost.serialkey.core.util.ROT47;

import java.util.List;
import java.util.function.Function;

/**
 * An unlocker that depends on a plugin.
 *
 * @param <T> ItemStack class.
 */

public abstract class PluginUnlocker<T> extends LoreUnlocker<T> {

	/**
	 * The plugin instance.
	 */

	private final SerialKeyPlugin<T, ?> plugin;

	/**
	 * Creates a new plugin unlocker instance.
	 *
	 * @param plugin The plugin.
	 */

	public PluginUnlocker(final SerialKeyPlugin<T, ?> plugin) {
		super(plugin.getItemManager());

		this.plugin = plugin;
	}

	@Override
	public List<SerialKeyLocation> getLocations(final T item) {
		final Function<String, String> stringProcessor = isCipheringEnabled() ? string -> ROT47.rotate(stripColor(string)) : this::stripColor;
		return getLocations(item, stringProcessor);
	}

	@Override
	public void addLocation(final T item, final SerialKeyLocation location) {
		plugin.getPadlockManager().fixLocation(location);
		super.addLocation(item, location);
	}

	@Override
	public void addLocation(final T item, final String world, final String position) {
		final String color = randomColor();
		addLocation(item, world, position, isCipheringEnabled() ? string -> color + ROT47.rotate(string) : string -> color + string);
	}

	@Override
	public short removeLocation(final T item, final SerialKeyLocation location) {
		plugin.getPadlockManager().fixLocation(location);
		return super.removeLocation(item, location);
	}

	@Override
	public short removeLocation(final T item, final String world, final String position) {
		final Function<String, String> stringProcessor = isCipheringEnabled() ? string -> ROT47.rotate(stripColor(string)) : this::stripColor;
		return removeLocation(item, world, position, stringProcessor);
	}

	@Override
	public boolean canUnlock(final T item, final SerialKeyLocation location) {
		plugin.getPadlockManager().fixLocation(location);
		return super.canUnlock(item, location);
	}

	@Override
	public boolean canUnlock(final T item, final String world, final String position) {
		final Function<String, String> stringProcessor = isCipheringEnabled() ? string -> ROT47.rotate(stripColor(string)) : this::stripColor;
		return super.canUnlock(item, world, position, stringProcessor);
	}

	@Override
	public boolean isCipheringEnabled() {
		return plugin.getPluginConfig().areLoresEncrypted();
	}

	/**
	 * Checks if the specified item is a valid key for the specified object.
	 *
	 * @param item The object.
	 * @param location The location.
	 * @param player If you want to check if the player has the right permission. Will send a message if he doesn't.
	 *
	 * @return <b>true</b> : yes.
	 * <br><b>false</b> : no.
	 */

	public boolean canUnlock(final T item, final SerialKeyLocation location, final SerialKeyPerson player) {
		if(itemManager.isMasterKey(item)) {
			if(player != null && !player.hasPermission("serialkey.use.masterkey")) {
				plugin.sendMessage(player, plugin.getPluginMessages().getPermissionMessage());
			}
			return true;
		}

		plugin.getPadlockManager().fixLocation(location);
		if(itemManager.isUsedKey(item)) {
			if(player != null && !player.hasPermission("serialkey.use.key")) {
				plugin.sendMessage(player, plugin.getPluginMessages().getPermissionMessage());
				return false;
			}
			return canUnlock(item, location.getWorld(), location.getPosition());
		}

		if(itemManager.isUsedBunchOfKeys(item)) {
			if(player != null && !player.hasPermission("serialkey.use.bunchofkeys")) {
				plugin.sendMessage(player, plugin.getPluginMessages().getPermissionMessage());
				return false;
			}
			return canUnlock(item, location.getWorld(), location.getPosition());
		}

		return false;
	}

	/**
	 * Returns a random chat color.
	 *
	 * @return A random chat color.
	 */

	protected abstract String randomColor();

	/**
	 * Strips all colors from the specified string.
	 *
	 * @param string The string.
	 *
	 * @return The handled string.
	 */

	protected abstract String stripColor(final String string);

}