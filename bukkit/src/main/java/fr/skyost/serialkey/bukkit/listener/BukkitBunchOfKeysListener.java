package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.bukkit.item.BukkitItemManager;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.BunchOfKeysListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A listener that allows to listen bunch of keys related events.
 */

public class BukkitBunchOfKeysListener extends BunchOfKeysListener<ItemStack, Location> implements Listener {

	/**
	 * The item manager.
	 */

	private BukkitItemManager itemManager;

	/**
	 * Creates a new bunch of keys listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitBunchOfKeysListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@Override
	public void setPlugin(final SerialKeyPlugin<ItemStack, Location> plugin) {
		if(!(plugin.getItemManager() instanceof BukkitItemManager)) {
			throw new IllegalArgumentException("Invalid item manager provided.");
		}

		super.setPlugin(plugin);
		itemManager = (BukkitItemManager)plugin.getItemManager();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerInteract(final PlayerInteractEvent event) {
		switch(event.getAction()) {
		case RIGHT_CLICK_BLOCK:
			super.onPlayerRightClickOnBlock(event.getClickedBlock().getLocation(), event.getItem(), () -> cancelIfCreateInventory(event));
			break;
		case RIGHT_CLICK_AIR:
			super.onPlayerRightClickOnAir(event.getItem(), () -> cancelIfCreateInventory(event));
			break;
		}
	}
	
	@EventHandler
	private void onInventoryClick(final InventoryClickEvent event) {
		if(!itemManager.isBunchOfKeys(event.getInventory())) {
			return;
		}

		final ItemStack item = event.getCurrentItem();
		if(item.getType() != Material.AIR && !itemManager.isUsedKey(item)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onInventoryClose(final InventoryCloseEvent event) {
		final Inventory inventory = event.getInventory();
		if(!itemManager.isBunchOfKeys(inventory)) {
			return;
		}

		final HumanEntity player = event.getPlayer();
		ItemStack bunchOfKeys = player.getInventory().getItemInOffHand();
		if(!itemManager.isBunchOfKeys(bunchOfKeys)) {
			bunchOfKeys = player.getInventory().getItemInMainHand();
		}

		if(bunchOfKeys.getAmount() > 1) {
			final ItemStack clone = bunchOfKeys.clone();
			clone.setAmount(bunchOfKeys.getAmount() - 1);
			player.getWorld().dropItemNaturally(player.getEyeLocation(), clone);

			bunchOfKeys.setAmount(1);
		}

		unlocker.clearLocations(bunchOfKeys);

		final int n = inventory.getSize();
		for(int i = 0; i < n; i++) {
			final ItemStack item = inventory.getItem(i);
			if(item == null) {
				continue;
			}

			if(!itemManager.isUsedKey(item)) {
				player.getWorld().dropItemNaturally(player.getLocation(), item);
				continue;
			}

			unlocker.addLocation(bunchOfKeys, item);
			if(item.getAmount() > 1) {
				final ItemStack clone = item.clone();
				clone.setAmount(item.getAmount() - 1);
				player.getWorld().dropItemNaturally(player.getEyeLocation(), clone);
			}
		}
	}

	/**
	 * Cancels the specified event if the inventory has been successfully created.
	 *
	 * @param event The event.
	 */

	private void cancelIfCreateInventory(final PlayerInteractEvent event) {
		if(itemManager.createInventory(event.getItem(), event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}

}
