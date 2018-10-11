package fr.skyost.serialkey.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.skyost.serialkey.SerialKey;

/**
 * A listener that allows to listen hoppers related events.
 */

public class HopperListener extends SerialKeyListener {

	/**
	 * Faces to check for hoppers.
	 */

	private static final BlockFace[] FACES = new BlockFace[]{
			BlockFace.UP,
			BlockFace.DOWN,
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
	};

	/**
	 * Creates a new hoppers listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public HopperListener(final SerialKey plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockPlace(final BlockPlaceEvent event) {
		if(event.isCancelled()) {
			return;
		}

		final Block block = event.getBlockPlaced();
		if(block.getType() != Material.HOPPER) {
			return;
		}

		for(final BlockFace face : FACES) {
			final Block relative = block.getRelative(face);
			if(!(relative.getState() instanceof Chest) || !api.hasPadlock(relative.getLocation(), true)) {
				continue;
			}

			plugin.sendMessage(event.getPlayer(), plugin.getPluginMessages().message3);
			event.setCancelled(true);
			return;
		}
	}

}
