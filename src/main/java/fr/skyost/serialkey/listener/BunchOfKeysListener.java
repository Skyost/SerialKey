package fr.skyost.serialkey.listener;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

import fr.skyost.serialkey.SerialKey;

/**
 * A listener that allows to listen bunch of keys related events.
 */

public class BunchOfKeysListener extends SerialKeyListener {

	/**
	 * Creates a new bunch of keys listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BunchOfKeysListener(final SerialKey plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerInteract(final PlayerInteractEvent event) {
		switch(event.getAction()) {
		case RIGHT_CLICK_BLOCK:
			if(api.hasPadlock(event.getClickedBlock().getLocation())) {
				break;
			}
		case RIGHT_CLICK_AIR:
			if(api.createInventory(event.getItem(), event.getPlayer()) != null) {
				event.setCancelled(true);
			}
		default:
			break;
		}
	}
	
	@EventHandler
	private void onInventoryClick(final InventoryClickEvent event) {
		if(!api.isBunchOfKeys(event.getInventory())) {
			return;
		}

		final ItemStack item = event.getCurrentItem();
		if(item.getType() != Material.AIR && !api.isUsedKey(item)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onInventoryClose(final InventoryCloseEvent event) {
		final Inventory inventory = event.getInventory();
		if(!api.isBunchOfKeys(inventory)) {
			return;
		}

		final HumanEntity player = event.getPlayer();
		final ItemStack bunchOfKeys = player.getInventory().getItemInMainHand();
		api.clearKeys(bunchOfKeys);

		final Collection<? extends ItemStack> items = inventory.all(api.getKeyItem().getType()).values();
		for(final ItemStack item : items) {
			api.addKey(bunchOfKeys, item);

			final int amount = item.getAmount();
			if(amount > 1) {
				final ItemStack clone = item.clone();
				clone.setAmount(amount - 1);
				player.getWorld().dropItemNaturally(player.getEyeLocation(), clone);
			}
		}
	}

}
