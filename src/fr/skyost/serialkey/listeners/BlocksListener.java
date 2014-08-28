package fr.skyost.serialkey.listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import fr.skyost.serialkey.SerialKeyAPI;

public class BlocksListener implements Listener {
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	private final void onBlockBreak(final BlockBreakEvent event) {
		final Location location = event.getBlock().getLocation();
		if(!SerialKeyAPI.hasPadlock(location)) {
			return;
		}
		final Player player = event.getPlayer();
		final ItemStack inHand = player.getItemInHand();
		if(SerialKeyAPI.isValidKey(inHand, location)) {
			if(SerialKeyAPI.getConfig().reusableKeys) {
				if(!inHand.equals(SerialKeyAPI.getMasterKeyItem())) {
					final ItemStack key = SerialKeyAPI.getKeyItem();
					key.setAmount(inHand.getAmount());
					player.setItemInHand(key);
				}
			}
			else {
				player.setItemInHand(new ItemStack(Material.AIR));
				player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
			}
			SerialKeyAPI.removePadlock(location);
			SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message4);
		}
		else {
			SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message5);
		}
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	private final void onEntityBreakDoor(final EntityBreakDoorEvent event) {
		if(SerialKeyAPI.hasPadlock(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onEntityExplode(final EntityExplodeEvent event) {
		for(final Block block : new ArrayList<Block>(event.blockList())) {
			if(SerialKeyAPI.hasPadlock(block.getLocation())) {
				event.blockList().remove(block);
			}
		}
	}
	
}
