package fr.skyost.serialkey.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.skyost.serialkey.SerialKeyAPI;

public class HopperListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onBlockPlace(final BlockPlaceEvent event) {
		if(event.getItemInHand().getType() != Material.HOPPER) {
			return;
		}
		final Block block = event.getBlock();
		final Block up = block.getRelative(BlockFace.UP);
		final Block down = block.getRelative(BlockFace.DOWN);
		if((up.getState() instanceof Chest && SerialKeyAPI.hasPadlock(up.getLocation(), true)) || (down.getState() instanceof Chest && SerialKeyAPI.hasPadlock(down.getLocation(), true))) {
			SerialKeyAPI.sendMessage(event.getPlayer(), SerialKeyAPI.getMessages().message3);
			event.setCancelled(true);
		}
	}

}
