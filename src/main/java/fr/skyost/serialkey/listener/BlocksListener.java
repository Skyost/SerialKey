package fr.skyost.serialkey.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import fr.skyost.serialkey.SerialKey;

/**
 * A listener that allows to listen blocks related events.
 */

public class BlocksListener extends SerialKeyListener {

	/**
	 * Creates a new blocks listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BlocksListener(final SerialKey plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockPlace(final BlockPlaceEvent event) {
		if(event.isCancelled()) {
			return;
		}

		final ItemStack item = event.getItemInHand();
		if(api.isKey(item) || api.isMasterKey(item) || api.isBunchOfKeys(item)/* || api.isPadlockFinder(item)*/) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockBreak(final BlockBreakEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		final Location location = event.getBlock().getLocation();
		if(!api.hasPadlock(location)) {
			return;
		}

		final Player player = event.getPlayer();
		final ItemStack inHand = player.getInventory().getItemInMainHand();
		if(player.getGameMode() == GameMode.CREATIVE && api.isBlankKey(inHand)) {
			api.correctLocation(location);
			api.formatItem(location, inHand);
		}
		else if(api.isValidKey(inHand, location)) {
			if(!api.isMasterKey(inHand)) {
				if(plugin.getPluginConfig().reusableKeys) {
					int amount = 0;
					if(api.isUsedKey(inHand)) {
						amount = inHand.getAmount();
						player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
					}
					else if(api.isBunchOfKeys(inHand)) {
						amount = api.removeKey(inHand, api.getKey(location));
					}
					if(amount == 0) {
						return;
					}

					final ItemStack key = api.getKeyItem();
					key.setAmount(amount);
					player.getWorld().dropItemNaturally(player.getEyeLocation(), key);
				}
				else {
					if(api.isBunchOfKeys(inHand)) {
						api.removeKey(inHand, api.getKey(location));
					}
					else {
						player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
					}
					player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
				}
			}
			api.removePadlock(location);
			plugin.sendMessage(player, plugin.getPluginMessages().message2);
		}
		else {
			plugin.sendMessage(player, plugin.getPluginMessages().message3);
		}

		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onEntityBreakDoor(final EntityBreakDoorEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(api.hasPadlock(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onEntityExplode(final EntityExplodeEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		final List<Block> blocks = event.blockList();
		for(final Block block : new ArrayList<>(blocks)) {
			if(api.hasPadlock(block.getLocation())) {
				blocks.remove(block);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockRedstone(final BlockRedstoneEvent event) {
		if(api.hasPadlock(event.getBlock().getLocation())) {
			event.setNewCurrent(0);
		}
	}
	
}
