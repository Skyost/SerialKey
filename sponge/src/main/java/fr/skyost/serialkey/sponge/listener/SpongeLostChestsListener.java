package fr.skyost.serialkey.sponge.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.LostChestsListener;
import fr.skyost.serialkey.sponge.SpongeTypeConverter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A listener that allows to listen chests related events.
 */

public class SpongeLostChestsListener extends LostChestsListener<ItemStack, Location<World>> {

	/**
	 * Creates a new lost chests listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongeLostChestsListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Listener(order = Order.EARLY)
	public void onInventoryClick(final ClickInventoryEvent event, @First final Player player) {
		final Inventory holder = event.getTargetInventory();
		if(!(holder instanceof CarriedInventory)) {
			return;
		}

		((CarriedInventory)holder).getCarrier().ifPresent(carrier -> {
			if(!(carrier instanceof BlockCarrier)) {
				return;
			}

			super.onInventoryClick(
					SpongeTypeConverter.toSerialKeyPerson(player),
					SpongeTypeConverter.toSerialKeyLocation(((BlockCarrier)carrier).getLocation()),
					event.getCursorTransaction().getFinal().createStack(),
					() -> event.setCancelled(true)
			);
		});
	}

}