package fr.skyost.serialkey.config;

import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.skyost.serialkey.util.Skyoconfig;

/**
 * The plugin data class.
 */

public class PluginData extends Skyoconfig {

	@ConfigOptions(name = "padlocks")
	public List<Location> padlocks = new ArrayList<>();

	/**
	 * Creates a new plugin data instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */
	
	public PluginData(final File dataFolder) {
		super(new File(dataFolder, "data.yml"), Collections.singletonList("SerialKey Data"));
	}
	
}
