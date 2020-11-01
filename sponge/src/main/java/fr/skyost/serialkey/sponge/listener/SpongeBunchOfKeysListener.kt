package fr.skyost.serialkey.sponge.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.BunchOfKeysListener;
import fr.skyost.serialkey.sponge.item.SpongeItemManager;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A listener that allows to listen bunch of keys related events.
 */

public class SpongeBunchOfKeysListener extends BunchOfKeysListener<ItemStack, Location<World>> {

	/**
	 * The item manager.
	 */

	private SpongeItemManager itemManager;

	/**
	 * Creates a new bunch of keys listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongeBunchOfKeysListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Override
	public void setPlugin(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		if(!(plugin.getItemManager() instanceof SpongeItemManager)) {
			throw new IllegalArgumentException("Invalid item manager provided.");
		}

		super.setPlugin(plugin);
		itemManager = (SpongeItemManager)plugin.getItemManager();
	}

	@Listener(order = Order.EARLY)
	public void onPlayerRightClick(final InteractBlockEvent.Secondary event, @First final Player player) {
		final ItemStack item = player.getItemInHand(event.getHandType()).orElse(null);
		if(item == null) {
			return;
		}

		final BlockSnapshot block = event.getTargetBlock();
		block.getLocation().ifPresent(location -> {
			final Runnable cancelIfCreateInventory = () -> cancelIfCreateInventory(event, item, player);
			if(location.getBlockType() == BlockTypes.AIR) {
				super.onPlayerRightClickOnAir(item, cancelIfCreateInventory);
				return;
			}

			super.onPlayerRightClickOnBlock(location, item, cancelIfCreateInventory);
		});
	}

	/**
	 * Cancels the specified event if the inventory has been successfully created.
	 *
	 * @param event The event.
	 */

	private void cancelIfCreateInventory(final Cancellable event, final ItemStack item, final Player player) {
		if(itemManager.createInventory(unlocker, item, player) != null) {
			event.setCancelled(true);
		}
	}

}