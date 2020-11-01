package fr.skyost.serialkey.bukkit.config;

import fr.skyost.serialkey.bukkit.util.Skyoconfig;
import fr.skyost.serialkey.core.config.SerialKeyConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.io.File;
import java.util.*;

/**
 * The plugin configuration class.
 */

public class BukkitPluginConfig extends Skyoconfig implements SerialKeyConfig {

	@ConfigOptions(name = "enable.updater")
	public boolean enableUpdater = true;
	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;
	
	@ConfigOptions(name = "options.reusable-keys")
	public boolean reusableKeys = true;
	@ConfigOptions(name = "options.disable-hoppers")
	public boolean disableHoppers = true;
	@ConfigOptions(name = "options.encrypt-lore")
	public boolean encryptLore = false;
	@ConfigOptions(name = "options.can-rename-items")
	public boolean canRenameItems = false;
	@ConfigOptions(name = "options.allow-lost-chests")
	public boolean allowLostChests = true;
	
	@ConfigOptions(name = "key.material")
	public Material keyMaterial = Material.TRIPWIRE_HOOK;
	@ConfigOptions(name = "key.name")
	public String keyName = ChatColor.GOLD + "Key";
	@ConfigOptions(name = "key.recipe")
	public List<String> keyShape = Arrays.asList("A", "B");
	
	@ConfigOptions(name = "master-key.material")
	public Material masterKeyMaterial = Material.NAME_TAG;
	@ConfigOptions(name = "master-key.name")
	public String masterKeyName = ChatColor.DARK_PURPLE + "Master Key";
	@ConfigOptions(name = "master-key.recipe")
	public List<String> masterKeyShape = Arrays.asList("C", "B");
	
	@ConfigOptions(name = "bunch-of-keys.material")
	public Material bunchOfKeysMaterial = Material.NAME_TAG;
	@ConfigOptions(name = "bunch-of-keys.name")
	public String bunchOfKeysName = ChatColor.BLUE + "Bunch of keys";
	@ConfigOptions(name = "bunch-of-keys.recipe")
	public List<String> bunchOfKeysShape = Arrays.asList(" D ", "DBD", " D ");
	
	@ConfigOptions(name = "padlock-finder.name")
	public String padlockFinderName = ChatColor.RED + "Padlock finder";
	
	@ConfigOptions(name = "recipe-materials-v1")
	public LinkedHashMap<String, String> shapeMaterials = new LinkedHashMap<>();

	/**
	 * Creates a new plugin configuration instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */
	
	public BukkitPluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Collections.singletonList("SerialKey Configuration"));
		
		shapeMaterials.put("A", Material.IRON_INGOT.name());
		shapeMaterials.put("B", Material.LEVER.name());
		shapeMaterials.put("C", Material.COMMAND_BLOCK.name());
		shapeMaterials.put("D", Material.STRING.name());
	}

	@Override
	public boolean areKeysReusable() {
		return reusableKeys;
	}

	@Override
	public boolean areLoresEncrypted() {
		return encryptLore;
	}

	@Override
	public boolean canRenameItems() {
		return canRenameItems;
	}

	@Override
	public List<String> getKeyShape() {
		return keyShape;
	}

	@Override
	public List<String> getMasterKeyShape() {
		return masterKeyShape;
	}

	@Override
	public List<String> getBunchOfKeysShape() {
		return bunchOfKeysShape;
	}

	@Override
	public Map<String, String> getShapeMaterials() {
		return shapeMaterials;
	}

	@Override
	public String getKeyMaterialID() {
		return keyMaterial.name();
	}

	@Override
	public String getPadlockFinderMaterialID() {
		return Material.COMPASS.name();
	}

}
