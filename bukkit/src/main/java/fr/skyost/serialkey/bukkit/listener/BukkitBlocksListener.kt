package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.bukkit.util.DoorUtil;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.BlocksListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener that allows to listen blocks related events.
 */

public class BukkitBlocksListener extends BlocksListener<ItemStack, Location> implements Listener {

	/**
	 * Creates a new blocks listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitBlocksListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onBlockPlace(final BlockPlaceEvent event) {
		super.onBlockPlace(event.getItemInHand(), () -> event.setCancelled(true));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onEntityBreakDoor(final EntityBreakDoorEvent event) {
		super.onBlockBreakByCreature(event.getBlock().getLocation(), () -> event.setCancelled(true));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onEntityExplode(final EntityExplodeEvent event) {
		final List<Block> blocks = event.blockList();
		for(final Block block : new ArrayList<>(blocks)) {
			super.onBlockExplode(block.getLocation(), () -> blocks.remove(block));
			protectUpperDoor(block, () -> blocks.remove(block));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onBlockRedstone(final BlockRedstoneEvent event) {
		super.onBlockPoweredByRedstone(event.getBlock().getLocation(), () -> event.setNewCurrent(0));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onBlockBreak(final BlockBreakEvent event) {
		if(protectUpperDoor(event)) {
			plugin.sendMessage(BukkitTypeConverter.toSerialKeyPerson(event.getPlayer()), plugin.getPluginMessages().getBlockHasPadlockMessage());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onBlockIgnite(final BlockIgniteEvent event) {
		protectUpperDoor(event);
	}

	/**
	 * Protects the door that is up to the specified block.
	 *
	 * @param event The block event.
	 * @param <T> The event type.
	 *
	 * @return Whether the event has been cancelled or not.
	 */

	private <T extends BlockEvent & Cancellable> boolean protectUpperDoor(final T event) {
		return protectUpperDoor(event.getBlock(), () -> event.setCancelled(true));
	}

	/**
	 * Protects the door that is up to the specified block.
	 *
	 * @param event The block event.
	 * @param cancelEvent The runnable that allows to cancel the event.
	 *
	 * @return Whether the event has been cancelled or not.
	 */

	private boolean protectUpperDoor(final Block event, final Runnable cancelEvent) {
		final Block up = event.getRelative(BlockFace.UP);
		if(DoorUtil.getInstance(up.getBlockData()) == null) {
			return false;
		}

		return super.cancelIfHasPadlock(up.getLocation(), cancelEvent);
	}
	
}
