package fr.skyost.serialkey.bukkit.padlock;

import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.bukkit.SerialKey;
import fr.skyost.serialkey.bukkit.util.DoorUtil;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.padlock.PluginPadlockManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * The Bukkit padlock manager.
 */

public class BukkitPadlockManager extends PluginPadlockManager<ItemStack, Location> {

	/**
	 * Creates a new Bukkit padlock manager instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitPadlockManager(final SerialKey plugin) {
		super(plugin);
	}

	@Override
	public boolean fixLocation(final SerialKeyLocation location) {
		if(hasPadlock(location, false)) {
			return false;
		}

		final Block block = BukkitTypeConverter.toBukkitLocation(location).getBlock();
		if(block.getState() instanceof Chest) {
			final InventoryHolder holder = ((org.bukkit.block.Chest)block.getState()).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				final SerialKeyLocation left = pluginLocationToSerialKeyLocation(((org.bukkit.block.Chest)((DoubleChest)holder).getLeftSide()).getLocation());
				if(hasPadlock(left, false)) {
					location.setX(left.getX());
					location.setZ(left.getZ());
					return true;
				}

				final SerialKeyLocation right = pluginLocationToSerialKeyLocation(((org.bukkit.block.Chest)((DoubleChest)holder).getRightSide()).getLocation());
				if(hasPadlock(right, false)) {
					location.setX(right.getX());
					location.setZ(right.getZ());
					return true;
				}
			}
			return false;
		}

		if(DoorUtil.getInstance(block.getBlockData()) != null) {
			location.setY(DoorUtil.getBlockBelow(block).getY());
			if(hasPadlock(location, false)) {
				return true;
			}
			final Block doubleDoor = DoorUtil.getDoubleDoor(block);
			if(doubleDoor != null) {
				final Location doubleDoorLocation = doubleDoor.getLocation();
				location.setX(doubleDoorLocation.getBlockX());
				location.setZ(doubleDoorLocation.getBlockZ());
			}
			return true;
		}

		return false;
	}

	@Override
	protected SerialKeyLocation pluginLocationToSerialKeyLocation(final Location location) {
		return BukkitTypeConverter.toSerialKeyLocation(location);
	}

}