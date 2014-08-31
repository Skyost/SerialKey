package fr.skyost.serialkey.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import fr.skyost.serialkey.SerialKeyAPI;

public class BlocksListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onBlockPlace(final BlockPlaceEvent event) {
		final ItemStack item = event.getItemInHand();
		if(SerialKeyAPI.isKey(item) || SerialKeyAPI.isMasterKey(item) || SerialKeyAPI.isBunchOfKeys(item)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onBlockBreak(final BlockBreakEvent event) {
		final Location location = event.getBlock().getLocation();
		if(!SerialKeyAPI.hasPadlock(location)) {
			return;
		}
		final Player player = event.getPlayer();
		final ItemStack inHand = player.getItemInHand();
		if(SerialKeyAPI.isValidKey(inHand, location)) {
			if(!SerialKeyAPI.isMasterKey(inHand)) {
				if(SerialKeyAPI.getConfig().reusableKeys) {
					int amount = 0;
					if(SerialKeyAPI.isUsedKey(inHand)) {
						amount = inHand.getAmount();
						player.setItemInHand(new ItemStack(Material.AIR));
					}
					else if(SerialKeyAPI.isBunchOfKeys(inHand)) {
						amount = SerialKeyAPI.removeKey(inHand, SerialKeyAPI.getKey(location));
					}
					if(amount == 0) {
						return;
					}
					final ItemStack key = SerialKeyAPI.getKeyItem();
					key.setAmount(amount);
					player.getWorld().dropItemNaturally(player.getEyeLocation(), SerialKeyAPI.getKeyItem());
				}
				else {
					if(SerialKeyAPI.isBunchOfKeys(inHand)) {
						SerialKeyAPI.removeKey(inHand, SerialKeyAPI.getKey(location));
					}
					else {
						player.setItemInHand(new ItemStack(Material.AIR));
					}
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
				}
			}
			SerialKeyAPI.removePadlock(location);
			SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message2);
		}
		else {
			SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message3);
		}
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private final void onEntityBreakDoor(final EntityBreakDoorEvent event) {
		if(SerialKeyAPI.hasPadlock(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private final void onEntityExplode(final EntityExplodeEvent event) {
		final List<Block> blocks = event.blockList();
		for(final Block block : new ArrayList<Block>(blocks)) {
			if(SerialKeyAPI.hasPadlock(block.getLocation())) {
				blocks.remove(block);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private final void onBlockRedstone(final BlockRedstoneEvent event) {
		if(SerialKeyAPI.hasPadlock(event.getBlock().getLocation())) {
			event.setNewCurrent(0);
		}
	}
	
}
