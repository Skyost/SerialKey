package fr.skyost.serialkey.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Function;

import fr.skyost.serialkey.SerialKey;
import fr.skyost.serialkey.util.DoorUtil;

/**
 * A listener that allows to globally listen plugin events.
 */

public class GlobalListener extends SerialKeyListener {

	/**
	 * Creates a new global listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public GlobalListener(final SerialKey plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPrepareItemCraft(final PrepareItemCraftEvent event) {
		final CraftingInventory craftingTable = event.getInventory();
		final ItemStack result = craftingTable.getResult();
		final Player player = (Player)event.getView().getPlayer();
		final boolean isKeyClone = api.getKeyCloneItem().equals(result);
		final boolean isPadlockFinder = api.isPadlockFinder(result);

		if((api.isBlankKey(result) && !player.hasPermission("serialkey.craft.key")) || (api.isMasterKey(result) && !player.hasPermission("serialkey.craft.masterkey")) || (isKeyClone && !player.hasPermission("serialkey.craft.keyclone")) || (api.isBlankBunchOfKeys(result) && !player.hasPermission("serialkey.craft.bunchofkeys")) || (isPadlockFinder && !player.hasPermission("serialkey.craft.padlockfinder"))) {
			plugin.sendMessage(player, plugin.getPluginMessages().messagePermission);
			event.getInventory().setResult(null);
			return;
		}

		if(isKeyClone) {
			cloneLore(craftingTable, item -> api.isBlankKey(item));
			return;
		}

		if(isPadlockFinder) {
			cloneLore(craftingTable, item -> item.getType() == Material.COMPASS);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPlayerInteract(final PlayerInteractEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		final Block clicked = event.getClickedBlock();
		if(clicked == null) {
			return;
		}
		final BlockData data = clicked.getBlockData();
		if(!(data instanceof Chest) && DoorUtil.getInstance(data) == null && !(data instanceof TrapDoor)) {
			return;
		}

		final Action action = event.getAction();
		final ItemStack item = event.getItem();
		if(action == Action.LEFT_CLICK_BLOCK) {
			boolean isBlankKey = api.isBlankKey(item);
			if(!isBlankKey && !api.isMasterKey(item)) {
				return;
			}
			event.setCancelled(true);
			final Player player = event.getPlayer();
			if(isBlankKey ? !player.hasPermission("serialkey.use.key") : !player.hasPermission("serialkey.use.masterkey")) {
				plugin.sendMessage(player, plugin.getPluginMessages().messagePermission);
				return;
			}
			final Location location = clicked.getLocation();
			if(api.hasPadlock(location)) {
				plugin.sendMessage(player, plugin.getPluginMessages().message3);
				return;
			}
			api.createPadlock(location, item);
			plugin.sendMessage(player, plugin.getPluginMessages().message1);
			return;
		}

		if(action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final Location location = clicked.getLocation();
		if(!api.hasPadlock(location)) {
			return;
		}

		if(!api.isValidKey(item, location)) {
			plugin.sendMessage(event.getPlayer(), plugin.getPluginMessages().message3);
			event.setCancelled(true);
		}
	}

	private boolean cloneLore(final CraftingInventory craftingTable, final Function<ItemStack, Boolean> isIngredient) {
		final ItemStack[] items = craftingTable.getContents();
		ItemStack key = null;
		ItemStack ingredient = null;
		for(final ItemStack item : items) {
			if(item.getAmount() >= 2) {
				continue;
			}

			if(api.isUsedKey(item)) {
				key = item;
				continue;
			}

			if(isIngredient.apply(item)) {
				ingredient = item;
			}
		}

		if(key == null || ingredient == null) {
			craftingTable.setResult(null);
			return false;
		}

		final ItemStack result = craftingTable.getResult();
		final ItemMeta meta = result.getItemMeta();
		meta.setLore(key.getItemMeta().getLore());
		result.setItemMeta(meta);
		return true;
	}

}