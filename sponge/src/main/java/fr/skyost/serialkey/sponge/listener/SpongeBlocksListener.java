package fr.skyost.serialkey.sponge.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.BlocksListener;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.List;
import java.util.Set;

/**
 * A listener that allows to listen blocks related events.
 */

public class SpongeBlocksListener extends BlocksListener<ItemStack, Location<World>> {

	/**
	 * Creates a new blocks listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongeBlocksListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Listener(order = Order.LATE)
	public void onBlockPlace(final ChangeBlockEvent.Place event, final @First Player player) {
		player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> super.onBlockPlace(item, () -> event.setCancelled(true)));
	}

	@Listener(order = Order.LATE)
	public void onBlockBreak(final ChangeBlockEvent event, @First final Creature creature) {
		final List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
		for(final Transaction<BlockSnapshot> transaction : transactions) {
			if(super.onBlockBreakByCreature(transaction.getOriginal().getLocation().orElse(null), () -> event.setCancelled(true))) {
				break;
			}
		}
	}

	@Listener(order = Order.LATE)
	public void onBlockExplode(final ChangeBlockEvent event, @First final Explosion explosion) {
		final List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
		for(final Transaction<BlockSnapshot> transaction : transactions) {
			if(super.onBlockExplode(transaction.getOriginal().getLocation().orElse(null), () -> event.setCancelled(true))) {
				break;
			}
		}
	}

	@Listener(order = Order.LATE)
	public void onNotifyNeighborBlock(final NotifyNeighborBlockEvent event, @First final LocatableBlock source) {
		final Location<World> location = source.getLocation();
		final Set<Direction> directions = event.getNeighbors().keySet();
		for(final Direction direction : directions) {
			if(super.onBlockPoweredByRedstone(location.getBlockRelative(direction), () -> event.setCancelled(true))) {
				break;
			}
		}
	}

}