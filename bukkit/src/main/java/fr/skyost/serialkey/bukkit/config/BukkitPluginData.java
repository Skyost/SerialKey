package fr.skyost.serialkey.bukkit.config;

import fr.skyost.serialkey.bukkit.util.Skyoconfig;
import fr.skyost.serialkey.core.config.SerialKeyData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The plugin data class.
 */

public class BukkitPluginData extends Skyoconfig implements SerialKeyData {

	@ConfigOptions(name = "padlocks")
	public List<String> padlocks = new ArrayList<>();

	/**
	 * Creates a new plugin data instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */
	
	public BukkitPluginData(final File dataFolder) {
		super(new File(dataFolder, "data.yml"), Collections.singletonList("SerialKey Data"));
	}

	@Override
	public Collection<String> getData() {
		return padlocks;
	}

}
