package fr.skyost.serialkey.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.skyost.serialkey.PluginConfig;
import fr.skyost.serialkey.SerialKeyAPI;
import fr.skyost.serialkey.utils.DoorUtils;

public class GlobalListener implements Listener {
	
	@EventHandler
	private final void onPrepareItemCraft(final PrepareItemCraftEvent event) {
		final CraftingInventory craftingTable = event.getInventory();
		final ItemStack result = craftingTable.getResult();
		final Player player = (Player)event.getView().getPlayer();
		final boolean isKeyClone = SerialKeyAPI.isBlankKey(result) && result.getAmount() == 2;
		if((SerialKeyAPI.isBlankKey(result) && !player.hasPermission("serialkey.craft.key")) || (SerialKeyAPI.isMasterKey(result) && !player.hasPermission("serialkey.craft.masterkey")) || (isKeyClone && !player.hasPermission("serialkey.craft.keyclone")) || (SerialKeyAPI.isBlankBunchOfKeys(result) && !player.hasPermission("serialkey.craft.bunchofkeys"))) {
			SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message6);
			event.getInventory().setResult(null);
			return;
		}
		if(isKeyClone) {
			final PluginConfig config = SerialKeyAPI.getConfig();
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
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		final Block clicked = event.getClickedBlock();
		if(clicked == null) {
			return;
		}
		final BlockState state = clicked.getState();
		if(!(state instanceof Chest) && !DoorUtils.instanceOf(state.getData())) {
			return;
		}
		final Action action = event.getAction();
		final ItemStack item = event.getItem();
		if(action == Action.LEFT_CLICK_BLOCK) {
			if(!SerialKeyAPI.isBlankKey(item)) {
				return;
			}
			final Player player = event.getPlayer();
			if(!player.hasPermission("serialkey.use.key")) {
				SerialKeyAPI.sendMessage(player, SerialKeyAPI.getMessages().message6);
				return;
			}
			final Location location = clicked.getLocation();
			if(SerialKeyAPI.hasPadlock(location)) {
				SerialKeyAPI.sendMessage(event.getPlayer(), !(state instanceof Chest) ? SerialKeyAPI.getMessages().message2 : SerialKeyAPI.getMessages().message3);
				return;
			}
			SerialKeyAPI.createPadlock(location, item);
			SerialKeyAPI.sendMessage(event.getPlayer(), SerialKeyAPI.getMessages().message1);
			event.setCancelled(true);
		}
		else if(action == Action.RIGHT_CLICK_BLOCK) {
			final Location location = clicked.getLocation();
			if(!SerialKeyAPI.hasPadlock(location)) {
				return;
			}
			if(!SerialKeyAPI.isValidKey(item, location)) {
				SerialKeyAPI.sendMessage(event.getPlayer(), SerialKeyAPI.getMessages().message5);
				event.setCancelled(true);
				return;
			}
		}
	}
	
}
