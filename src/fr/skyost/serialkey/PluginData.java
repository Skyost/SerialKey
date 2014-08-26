package fr.skyost.serialkey;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;

import fr.skyost.serialkey.utils.Skyoconfig;

public class PluginData extends Skyoconfig {

	@ConfigOptions(name = "padlocks")
	public List<Location> padlocks = new ArrayList<Location>();
	
	protected PluginData(final File dataFolder) {
		super(new File(dataFolder, "data.yml"), Arrays.asList("SerialKey Data"));
	}
	
}
