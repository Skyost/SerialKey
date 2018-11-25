package fr.skyost.serialkey.sponge.padlock;

import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.padlock.PluginPadlockManager;
import fr.skyost.serialkey.sponge.SerialKey;
import fr.skyost.serialkey.sponge.SpongeTypeConverter;
import fr.skyost.serialkey.sponge.util.ChestUtil;
import fr.skyost.serialkey.sponge.util.DoorUtil;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Set;

/**
 * The Sponge padlock manager.
 */

public class SpongePadlockManager extends PluginPadlockManager<ItemStack, Location<World>> {

	/**
	 * Creates a new Sponge padlock manager instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongePadlockManager(final SerialKey plugin) {
		super(plugin);
	}

	@Override
	public boolean fixLocation(final SerialKeyLocation location) {
		if(hasPadlock(location, false)) {
			return false;
		}

		final Location<World> spongeLocation = SpongeTypeConverter.toSpongeLocation(location);
		final Chest chest;
		if((chest = ChestUtil.getChest(spongeLocation)) != null) {
			final Set<Chest> connectedChests = chest.getConnectedChests();
			for(final Chest connectedChest : connectedChests) {
				final Location<World> connectedLocation = connectedChest.getLocation();
				if(hasPadlock(pluginLocationToSerialKeyLocation(connectedLocation), false)) {
					location.setX(connectedLocation.getBlockX());
					location.setZ(connectedLocation.getBlockZ());
					return true;
				}
			}
		}
		else if(DoorUtil.isDoor(spongeLocation)) {
			location.setY(DoorUtil.getBlockBelow(spongeLocation).getBlockY());
			if(hasPadlock(location, false)) {
				return true;
			}
			final Location<World> doubleDoor = DoorUtil.getDoubleDoor(SpongeTypeConverter.toSpongeLocation(location));
			if(doubleDoor != null) {
				location.setX(doubleDoor.getBlockX());
				location.setZ(doubleDoor.getBlockZ());
				return true;
			}
		}

		return false;
	}

	@Override
	protected SerialKeyLocation pluginLocationToSerialKeyLocation(final Location<World> location) {
		return SpongeTypeConverter.toSerialKeyLocation(location);
	}
	
}