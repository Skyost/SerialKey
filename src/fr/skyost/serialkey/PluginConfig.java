package fr.skyost.serialkey;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.skyost.serialkey.utils.Skyoconfig;

public class PluginConfig extends Skyoconfig {
	
	@ConfigOptions(name = "options.reusable-keys")
	public boolean reusableKeys = true;
	@ConfigOptions(name = "options.enable-updater")
	public boolean enableUpdater = true;
	
	@ConfigOptions(name = "key.material")
	public Material keyMaterial = Material.TRIPWIRE_HOOK;
	@ConfigOptions(name = "key.name")
	public String keyName = ChatColor.GOLD + "Key";
	@ConfigOptions(name = "key.shape")
	public List<String> keyShape = Arrays.asList("A", "B");
	@ConfigOptions(name = "key.shape-materials")
	public LinkedHashMap<String, String> keyShapeMaterials = new LinkedHashMap<String, String>();
	
	@ConfigOptions(name = "master-key.material")
	public Material masterKeyMaterial = Material.NAME_TAG;
	@ConfigOptions(name = "master-key.name")
	public String masterKeyName = ChatColor.DARK_PURPLE + "Master Key";
	@ConfigOptions(name = "master-key.shape")
	public List<String> masterKeyShape = Arrays.asList("C", "D");
	@ConfigOptions(name = "master-key.shape-materials")
	public LinkedHashMap<String, String> masterKeyShapeMaterials = new LinkedHashMap<String, String>();
	
	protected PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Arrays.asList("SerialKey configuration"));
		keyShapeMaterials.put("A", Material.IRON_INGOT.name());
		keyShapeMaterials.put("B", Material.LEVER.name());
		masterKeyShapeMaterials.put("C", Material.COMMAND.name());
		masterKeyShapeMaterials.put("D", Material.TRIPWIRE_HOOK.name());
	}

}
