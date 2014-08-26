package fr.skyost.serialkey.listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;

import fr.skyost.serialkey.SerialKeyAPI;

@SuppressWarnings("deprecation")
public class EventsListener implements Listener {
	
	@EventHandler
	private final void onPrepareItemCraft(final PrepareItemCraftEvent event) {
		final ItemStack result = event.getRecipe().getResult();
		final Player player = (Player)event.getView().getPlayer();
		if((result.equals(SerialKeyAPI.getKeyItem()) && !player.hasPermission("serialkey.craft.key")) || (result.equals(SerialKeyAPI.getMasterKeyItem()) && !player.hasPermission("serialkey.craft.masterkey"))) {
			player.sendMessage(SerialKeyAPI.getMessages().message6);
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		final Block clicked = event.getClickedBlock();
		if(clicked == null) {
			return;
		}
		final BlockState state = clicked.getState();
		if(!(state instanceof Chest) && !(state.getData() instanceof Door)) {
			return;
		}
		final Action action = event.getAction();
		if(action == Action.LEFT_CLICK_BLOCK) {
			final ItemStack inHand = event.getItem();
			if(inHand == null) {
				return;
			}
			if(!inHand.isSimilar(SerialKeyAPI.getKeyItem())) {
				return;
			}
			final Location location = clicked.getLocation();
			if(SerialKeyAPI.hasPadlock(location)) {
				event.getPlayer().sendMessage(!(state instanceof Chest) ? SerialKeyAPI.getMessages().message2 : SerialKeyAPI.getMessages().message3);
				return;
			}
			final Player player = event.getPlayer();
			if(!player.hasPermission("serialkey.use.key")) {
				player.sendMessage(SerialKeyAPI.getMessages().message6);
				return;
			}
			SerialKeyAPI.createPadlock(location, inHand);
			player.sendMessage(SerialKeyAPI.getMessages().message1);
			event.setCancelled(true);
		}
		else if(action == Action.RIGHT_CLICK_BLOCK) {
			final Location location = clicked.getLocation();
			if(!SerialKeyAPI.hasPadlock(location)) {
				return;
			}
			if(!SerialKeyAPI.isValidKey(event.getItem(), clicked.getLocation())) {
				event.getPlayer().sendMessage(SerialKeyAPI.getMessages().message5);
				event.setCancelled(true);
				return;
			}
		}
	}
	
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
					player.setItemInHand(SerialKeyAPI.getKeyItem());
				}
			}
			else {
				player.setItemInHand(new ItemStack(Material.AIR));
				player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
			}
			SerialKeyAPI.removePadlock(location);
			player.sendMessage(SerialKeyAPI.getMessages().message4);
		}
		else {
			player.sendMessage(SerialKeyAPI.getMessages().message5);
			event.setCancelled(true);
		}
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
