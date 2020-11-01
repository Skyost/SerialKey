package fr.skyost.serialkey.sponge.util;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Allows to perform some operations on chests.
 */

public class ChestUtil {

	/**
	 * Returns the chest instance at the specified location.
	 *
	 * @param location The location.
	 *
	 * @return The chest instance.
	 */

	public static Chest getChest(final Location<World> location) {
		final Optional<TileEntity> chest = location.getTileEntity();
		return (Chest)chest.filter(entity -> entity instanceof Chest).orElse(null);
	}

	/**
	 * Returns whether the specified block type is a chest.
	 *
	 * @param type The block type.
	 *
	 * @return Whether the specified block type is a chest.
	 */

	public static boolean isChest(final BlockType type) {
		return type.equals(BlockTypes.CHEST) || type.equals(BlockTypes.TRAPPED_CHEST);
	}

}