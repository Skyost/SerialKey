package fr.skyost.serialkey.sponge.util;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

/**
 * Contains some useful methods.
 */

public class Util {

	/**
	 * The available color codes.
	 */

	private static final String[] COLORS = new String[]{
			"&1",
			"&2",
			"&3",
			"&4",
			"&5",
			"&6",
			"&7",
			"&8",
			"&9",
			"&a",
			"&b",
			"&c",
			"&d",
			"&e",
			"&f"
	};

	/**
	 * Strips colors from the specified string.
	 *
	 * @param string The string.
	 *
	 * @return The handled string.
	 */

	public static String stripColor(final String string) {
		return TextSerializers.FORMATTING_CODE.deserialize(string).toPlain();
	}

	/**
	 * Returns a random text color.
	 *
	 * @return A random text color.
	 */

	public static String randomTextColor() {
		final Random random = new Random();
		return COLORS[random.nextInt(COLORS.length)];
	}

	/**
	 * Parses a string with color codes.
	 *
	 * @param string The string.
	 *
	 * @return The parsed string (as a Text).
	 */

	public static Text parseString(final String string) {
		return TextSerializers.FORMATTING_CODE.deserialize(string);
	}

	/**
	 * Creates an item with a custom name.
	 *
	 * @param name The name.
	 * @param material The item's material.
	 *
	 * @return The item.
	 */

	public static ItemStack createItem(final String name, final ItemType material) {
		return ItemStack.builder()
				.add(Keys.DISPLAY_NAME, Util.parseString(name))
				.itemType(material)
				.build();
	}

	/**
	 * Drops an item at the specified location.
	 *
	 * @param item The item.
	 * @param location The location.
	 */

	public static void dropItemAt(final ItemStack item, final Location<World> location) {
		final Entity drop = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
		drop.offer(Keys.REPRESENTED_ITEM, item.createSnapshot());
		location.getExtent().spawnEntity(drop);
	}

	/**
	 * Creates a blank item.
	 *
	 * @return The blank item.
	 */

	public static ItemStack blankItem() {
		return ItemStack.builder().itemType(ItemTypes.AIR).build();
	}

}