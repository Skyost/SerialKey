package fr.skyost.serialkey.sponge.unlocker;

import fr.skyost.serialkey.core.unlocker.PluginUnlocker;
import fr.skyost.serialkey.sponge.SerialKey;
import fr.skyost.serialkey.sponge.util.Util;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * The Sponge unlocker class.
 */

public class SpongeUnlocker extends PluginUnlocker<ItemStack> {

	/**
	 * Creates a new Sponge unlocker instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public SpongeUnlocker(final SerialKey plugin) {
		super(plugin);
	}

	@Override
	protected String randomColor() {
		return Util.randomTextColor();
	}

	@Override
	protected String stripColor(final String string) {
		return Util.stripColor(string);
	}

}