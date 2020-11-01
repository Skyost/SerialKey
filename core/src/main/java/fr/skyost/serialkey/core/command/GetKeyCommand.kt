package fr.skyost.serialkey.core.command;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.object.PersonIdentity;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;

import java.util.function.Consumer;

/**
 * Represents the <pre>/serialkey getkey</pre> command executor.
 */

public abstract class GetKeyCommand<I> {

	/**
	 * The plugin instance.
	 */

	private final SerialKeyPlugin<I, ?> plugin;

	/**
	 * Creates a new <pre>/serialkey getkey</pre> command executor instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public GetKeyCommand(final SerialKeyPlugin<I, ?> plugin) {
		this.plugin = plugin;
	}

	/**
	 * Executes the command.
	 *
	 * @param executor The executor.
	 * @param targetedBlock The targeted block.
	 * @param dropItem The function that allows to drop an item.
	 *
	 * @return Number of affected entities.
	 */

	protected int execute(final SerialKeyPerson executor, final SerialKeyLocation targetedBlock, final Consumer<I> dropItem) {
		if(executor.getIdentity().getType() != PersonIdentity.Type.PLAYER) {
			plugin.sendMessage(executor, "&cYou must be a player to run this command.");
			return 0;
		}

		if(!executor.hasPermission("serialkey.command.getkey")) {
			plugin.sendMessage(executor, plugin.getPluginMessages().getPermissionMessage());
			return 0;
		}

		if(targetedBlock != null && plugin.getPadlockManager().hasPadlock(targetedBlock)) {
			final I key = copyItem(plugin.getItemManager().getKeyItem());
			plugin.getUnlocker().addLocation(key, targetedBlock);
			dropItem.accept(key);
			return 1;
		}

		plugin.sendMessage(executor, "&cYou must be looking at a valid block.");
		return 0;
	}

	/**
	 * Returns the plugin instance.
	 *
	 * @return The plugin instance.
	 */

	public SerialKeyPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Copies an item.
	 *
	 * @param item The item.
	 *
	 * @return The copied item.
	 */

	protected abstract I copyItem(final I item);

}