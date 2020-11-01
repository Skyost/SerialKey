package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.HopperListener;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A listener that allows to listen hoppers related events.
 */

public class BukkitHopperListener extends HopperListener<ItemStack, Location> implements Listener {

	/**
	 * Creates a new hoppers listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitHopperListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onBlockPlace(final BlockPlaceEvent event) {
		final Block block = event.getBlockPlaced();
		if(block.getType() == Material.HOPPER) {
			super.onBlockPlace(
					BukkitTypeConverter.toSerialKeyLocation(event.getBlockPlaced().getLocation()),
					BukkitTypeConverter.toSerialKeyPerson(event.getPlayer()),
					() -> event.setCancelled(true)
			);
		}
	}

	@Override
	protected boolean isChest(final SerialKeyLocation location) {
		return BukkitTypeConverter.toBukkitLocation(location).getBlock().getState() instanceof Chest;
	}

}
