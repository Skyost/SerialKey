package fr.skyost.serialkey.sponge.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.AnvilListener;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A listener that allows to listen anvil related events.
 */

public class SpongeAnvilListener extends AnvilListener<ItemStack, Location<World>> {

	/**
	 * Creates a new anvil listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongeAnvilListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Listener(order = Order.EARLY)
	public void onInventoryClick(final ClickInventoryEvent event, @First final Player player) {
		if(event.getTargetInventory().getArchetype() != InventoryArchetypes.ANVIL) {
			return;
		}
		super.onItemClicked(event.getCursorTransaction().getFinal().createStack(), () -> event.setCancelled(true));
	}

}
