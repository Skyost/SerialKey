package fr.skyost.serialkey.listeners;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;

import fr.skyost.serialkey.PluginConfig;
import fr.skyost.serialkey.SerialKeyAPI;
import fr.skyost.serialkey.utils.DoorUtils;

public class GlobalListener implements Listener {
	
	private final HashSet<CraftingInventory> padlockFinders = new HashSet<CraftingInventory>();
	
	@EventHandler(ignoreCancelled = true)
	private final void onPrepareItemCraft(final PrepareItemCraftEvent event) {
		final CraftingInventory craftingTable = event.getInventory();
		final ItemStack result = craftingTable.getResult();
		final Player player = (Player)event.getView().getPlayer();
		final boolean isKeyClone = SerialKeyAPI.getKeyCloneItem().equals(result);
		final boolean isPadlockFinder = SerialKeyAPI.isPadlockFinder(result);
		if((SerialKeyAPI.isBlankKey(result) && !player.hasPermission("serialkey.craft.key")) || (SerialKeyAPI.isMasterKey(result) && !player.hasPermission("serialkey.craft.masterkey")) || (isKeyClone && !player.hasPermission("serialkey.craft.keyclone")) || (SerialKeyAPI.isBlankBunchOfKeys(result) && !player.hasPermission("serialkey.craft.bunchofkeys")) || (isPadlockFinder && !player.hasPermission("serialkey.craft.padlockfinder"))) {
			SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().messagePermission);
			event.getInventory().setResult(null);
			return;
		}
		final PluginConfig config = SerialKeyAPI.getConfig();
		if(isKeyClone) {
			ItemStack key = null;
			ItemStack blankKey = null;
			for(final ItemStack item : craftingTable.all(config.keyMaterial).values()) {
				if(!SerialKeyAPI.isKey(item) || item.getAmount() == 2) {
					continue;
				}
				if(SerialKeyAPI.isUsedKey(item)) {
					key = item;
				}
				else if(SerialKeyAPI.isBlankKey(item)) {
					blankKey = item;
				}
			}
			if(key == null || blankKey == null) {
				craftingTable.setResult(null);
				return;
			}
			final ItemMeta meta = result.getItemMeta();
			meta.setLore(key.getItemMeta().getLore());
			result.setItemMeta(meta);
		}
		else if(isPadlockFinder) {
			ItemStack key = null;
			ItemStack compass = null;
			for(final ItemStack item : craftingTable.getMatrix()) {
				if(SerialKeyAPI.isUsedKey(item)) {
					key = item;
				}
				else if(item != null && item.getType() == Material.COMPASS) {
					compass = item;
				}
			}
			if(key == null || compass == null) {
				craftingTable.setResult(null);
				return;
			}
			final ItemMeta meta = result.getItemMeta();
			meta.setLore(key.getItemMeta().getLore());
			result.setItemMeta(meta);
			padlockFinders.add(craftingTable);
		}
	}
	
	@EventHandler
	private final void onInventoryClick(final InventoryClickEvent event) {
		final Inventory inventory = event.getInventory();
		if(padlockFinders.contains(inventory)) {
			final ItemStack item = event.getCurrentItem();
			if(SerialKeyAPI.isUsedPadlockFinder(item)) {
				final HumanEntity player = event.getWhoClicked();
				try {
					player.getWorld().dropItemNaturally(player.getEyeLocation(), SerialKeyAPI.getKey(SerialKeyAPI.extractLocation(item)));
				}
				catch(final Exception ex) {
					ex.printStackTrace();
					SerialKeyAPI.sendMessage(player, ChatColor.RED + ex.getClass().getName());
				}
			}
		}
	}
	
	@EventHandler
	private final void onInventoryClose(final InventoryCloseEvent event) {
		final Inventory inventory = event.getInventory();
		if(padlockFinders.contains(inventory)) {
			padlockFinders.remove(inventory);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		final Block clicked = event.getClickedBlock();
		if(clicked == null) {
			return;
		}
		final BlockState state = clicked.getState();
		if(!(state instanceof Chest)) {
			final MaterialData data = state.getData();
			if(!DoorUtils.instanceOf(data) && !(data instanceof TrapDoor)) {
				return;
			}
		}
		final Action action = event.getAction();
		final ItemStack item = event.getItem();
		if(action == Action.LEFT_CLICK_BLOCK) {
			boolean isBlankKey = SerialKeyAPI.isBlankKey(item);
			if(!isBlankKey && !SerialKeyAPI.isMasterKey(item)) {
				return;
			}
			final Player player = event.getPlayer();
			if(isBlankKey ? !player.hasPermission("serialkey.use.key") : !player.hasPermission("serialkey.use.masterkey")) {
				SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().messagePermission);
				return;
			}
			final Location location = clicked.getLocation();
			if(SerialKeyAPI.hasPadlock(location)) {
				SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message3);
				return;
			}
			try {
				SerialKeyAPI.createPadlock(location, item);
				SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message1);
				event.setCancelled(true);
			}
			catch(final Exception ex) {
				ex.printStackTrace();
				SerialKeyAPI.sendMessage(player, ChatColor.RED + ex.getClass().getName());
			}
		}
		else if(action == Action.RIGHT_CLICK_BLOCK) {
			final Location location = clicked.getLocation();
			if(!SerialKeyAPI.hasPadlock(location)) {
				return;
			}
			try {
				if(!SerialKeyAPI.isValidKey(item, location)) {
					SerialKeyAPI.sendMessage(event.getPlayer(), SerialKeyAPI.getMessages().message3);
					event.setCancelled(true);
					return;
				}
			}
			catch(final Exception ex) {
				ex.printStackTrace();
				SerialKeyAPI.sendMessage(event.getPlayer(), ChatColor.RED + ex.getClass().getName());
			}
		}
	}
	
}
