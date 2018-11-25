package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.PadlockFinderListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A listener that allows to listen padlock finder related events.
 */

public class BukkitPadlockFinderListener extends PadlockFinderListener<ItemStack, Location> implements Listener {

	/**
	 * Creates a new padlock finder listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitPadlockFinderListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onPlayerInteract(final PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final Player player = event.getPlayer();
		super.onPlayerRightClick(
				event.getItem(),
				BukkitTypeConverter.toSerialKeyPerson(player),
				BukkitTypeConverter.toSerialKeyLocation(player.getWorld().getSpawnLocation()),
				BukkitTypeConverter.toSerialKeyLocation(player.getCompassTarget()),
				location -> player.setCompassTarget(BukkitTypeConverter.toBukkitLocation(location)),
				() -> event.setCancelled(true)
		);
	}
	
}
