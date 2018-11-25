package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.bukkit.util.DoorUtil;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.GlobalListener;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * A listener that allows to globally listen plugin events.
 */

public class BukkitGlobalListener extends GlobalListener<ItemStack, Location> implements Listener {

	/**
	 * Creates a new global listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitGlobalListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onPrepareItemCraft(final PrepareItemCraftEvent event) {
		final CraftingInventory craftingTable = event.getInventory();
		final ItemStack result = craftingTable.getResult();
		final String key = event.getRecipe() instanceof ShapedRecipe ? ((ShapedRecipe)event.getRecipe()).getKey().getNamespace() : null;
		super.onPreviewItemCraft(
				BukkitTypeConverter.toSerialKeyPerson(event.getView().getPlayer()),
				craftingTable.getMatrix(),
				key,
				result,
				itemManager.getKeyCloneItem().equals(result) ? (item -> itemManager.isBlankKey(item)) : (item -> item.getType() == Material.COMPASS),
				item -> {},
				() -> craftingTable.setResult(null)
		);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			super.onPlayerLeftClick(
					event.getItem(),
					BukkitTypeConverter.toSerialKeyLocation(event.getClickedBlock().getLocation()),
					BukkitTypeConverter.toSerialKeyPerson(event.getPlayer()),
					BukkitTypeConverter.toSerialKeyLocation(event.getPlayer().getLocation()),
					item -> player.getWorld().dropItemNaturally(player.getLocation(), item),
					() -> player.getInventory().setItemInMainHand(new ItemStack(Material.AIR)),
					() -> player.playSound(event.getClickedBlock().getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f),
					() -> event.setCancelled(true)
			);
			return;
		}

		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			super.onPlayerRightClick(
					event.getItem(),
					BukkitTypeConverter.toSerialKeyLocation(event.getClickedBlock().getLocation()),
					BukkitTypeConverter.toSerialKeyPerson(event.getPlayer()),
					() -> event.setCancelled(true)
			);
		}
	}


	@Override
	protected ItemStack copy(final ItemStack item) {
		return item.clone();
	}

	@Override
	protected int getAmount(final ItemStack item) {
		return item.getAmount();
	}

	@Override
	protected void setAmount(final ItemStack item, final int amount) {
		item.setAmount(amount);
	}

	@Override
	protected boolean isPadlockLocationValid(final SerialKeyLocation location) {
		final Block block = BukkitTypeConverter.toBukkitLocation(location).getBlock();
		return block.getState() instanceof Chest || DoorUtil.getInstance(block.getBlockData()) != null || block.getBlockData() instanceof TrapDoor;
	}

}