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
	@ConfigOptions(name = "options.disable-hoppers")
	public boolean disableHoppers = true;
	@ConfigOptions(name = "options.encrypt-lore")
	public boolean encryptLore = false;
	/*@ConfigOptions(name = "options.hide-lore")
	public boolean hideLore = false; // http://forums.bukkit.org/threads/storing-hidden-data-in-itemstacks-using-colors-persistent-no-nms.319970/.*/
	@ConfigOptions(name = "options.enable-updater")
	public boolean enableUpdater = true;
	@ConfigOptions(name = "options.enable-metrics")
	public boolean enableMetrics = true;
	
	@ConfigOptions(name = "key.material")
	public Material keyMaterial = Material.TRIPWIRE_HOOK;
	@ConfigOptions(name = "key.name")
	public String keyName = ChatColor.GOLD + "Key";
	@ConfigOptions(name = "key.shape")
	public List<String> keyShape = Arrays.asList("A", "B");
	
	@ConfigOptions(name = "master-key.material")
	public Material masterKeyMaterial = Material.NAME_TAG;
	@ConfigOptions(name = "master-key.name")
	public String masterKeyName = ChatColor.DARK_PURPLE + "Master Key";
	@ConfigOptions(name = "master-key.shape")
	public List<String> masterKeyShape = Arrays.asList("C", "B");
	
	@ConfigOptions(name = "bunch-of-keys.material")
	public Material bunchOfKeysMaterial = Material.NAME_TAG;
	@ConfigOptions(name = "bunch-of-keys.name")
	public String bunchOfKeysName = ChatColor.BLUE + "Bunch of keys";
	@ConfigOptions(name = "bunch-of-keys.shape")
	public List<String> bunchOfKeysShape = Arrays.asList(" D ", "DBD", " D ");
	
	@ConfigOptions(name = "padlock-finder.name")
	public String padlockFinderName = ChatColor.RED + "Padlock finder";
	
	@ConfigOptions(name = "shape-materials-v1")
	public LinkedHashMap<String, String> shapeMaterials = new LinkedHashMap<String, String>();
	
	protected PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Arrays.asList("SerialKey configuration"));
		shapeMaterials.put("A", Material.IRON_INGOT.name());
		shapeMaterials.put("B", Material.LEVER.name());
		shapeMaterials.put("C", Material.COMMAND.name());
		shapeMaterials.put("D", Material.STRING.name());
	}

}
