package fr.skyost.serialkey.sponge.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.HopperListener;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;
import fr.skyost.serialkey.sponge.SpongeTypeConverter;
import fr.skyost.serialkey.sponge.util.ChestUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A listener that allows to listen hoppers related events.
 */

public class SpongeHopperListener extends HopperListener<ItemStack, Location<World>> {

	/**
	 * Creates a new hoppers listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongeHopperListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Listener(order = Order.LATE)
	public void onBlockPlace(final ChangeBlockEvent.Place event, final @First Player player) {
		final SerialKeyPerson person = SpongeTypeConverter.toSerialKeyPerson(player);
		event.getTransactions().forEach(transaction -> {
			final BlockSnapshot block = transaction.getFinal();
			if(block.getState().getType() != BlockTypes.HOPPER) {
				return;
			}

			block.getLocation().ifPresent(location -> super.onBlockPlace(SpongeTypeConverter.toSerialKeyLocation(location), person, () -> transaction.setValid(false)));
		});
	}

	@Override
	protected boolean isChest(final SerialKeyLocation location) {
		return ChestUtil.isChest(SpongeTypeConverter.toSpongeLocation(location).getBlockType());
	}

}