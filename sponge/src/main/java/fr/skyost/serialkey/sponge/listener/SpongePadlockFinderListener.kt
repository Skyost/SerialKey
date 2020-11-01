package fr.skyost.serialkey.sponge.listener;

import com.flowpowered.math.vector.Vector3d;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.PadlockFinderListener;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.sponge.SpongeTypeConverter;
import fr.skyost.serialkey.sponge.util.Util;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A listener that allows to listen padlock finder related events.
 */

public class SpongePadlockFinderListener extends PadlockFinderListener<ItemStack, Location<World>> {

	/**
	 * Creates a new padlock finder listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongePadlockFinderListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Listener(order = Order.LATE)
	public void onPlayerRightClick(final InteractBlockEvent.Secondary event, @First final Player player) {
		final SerialKeyLocation spawn = SpongeTypeConverter.toSerialKeyLocation(player.getWorld().getSpawnLocation());
		super.onPlayerRightClick(
				player.getItemInHand(event.getHandType()).orElse(Util.blankItem()),
				SpongeTypeConverter.toSerialKeyPerson(player),
				spawn,
				player.get(Keys.TARGETED_LOCATION).map(position -> new SerialKeyLocation(player.getWorld().getName(), (int)position.getX(), (int)position.getY(), (int)position.getZ())).orElse(spawn),
				location -> player.offer(Keys.TARGETED_LOCATION, new Vector3d(location.getX(), location.getY(), location.getZ())),
				() -> event.setCancelled(true)
		);
	}

}