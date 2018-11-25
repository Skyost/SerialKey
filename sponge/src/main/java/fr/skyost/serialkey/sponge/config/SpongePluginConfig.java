package fr.skyost.serialkey.sponge.config;

import fr.skyost.serialkey.core.config.SerialKeyConfig;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.nio.file.Path;
import java.util.*;

/**
 * The plugin configuration class.
 */

public class SpongePluginConfig extends SpongeConfig implements SerialKeyConfig {

	@ConfigOptions(name = "enable.updater")
	public boolean enableUpdater = true;

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
	public ItemType keyMaterial = ItemTypes.TRIPWIRE_HOOK;
	@ConfigOptions(name = "key.name")
	public String keyName = "&6Key";
	@ConfigOptions(name = "key.recipe")
	public List<String> keyShape = new ArrayList<>(Arrays.asList("A", "B"));

	@ConfigOptions(name = "master-key.material")
	public ItemType masterKeyMaterial = ItemTypes.NAME_TAG;
	@ConfigOptions(name = "master-key.name")
	public String masterKeyName = "&5Master Key";
	@ConfigOptions(name = "master-key.recipe")
	public List<String> masterKeyShape = new ArrayList<>(Arrays.asList("C", "B"));

	@ConfigOptions(name = "bunch-of-keys.material")
	public ItemType bunchOfKeysMaterial = ItemTypes.NAME_TAG;
	@ConfigOptions(name = "bunch-of-keys.name")
	public String bunchOfKeysName = "&9Bunch of keys";
	@ConfigOptions(name = "bunch-of-keys.recipe")
	public List<String> bunchOfKeysShape = new ArrayList<>(Arrays.asList(" D ", "DBD", " D "));

	@ConfigOptions(name = "padlock-finder.name")
	public String padlockFinderName = "&cPadlock finder";

	@ConfigOptions(name = "recipe-materials-v1")
	public LinkedHashMap<String, String> shapeMaterials = new LinkedHashMap<>();

	/**
	 * Creates a new plugin configuration instance.
	 *
	 * @param file The config file.
	 */

	public SpongePluginConfig(final Path file) {
		super(file, "SerialKey Configuration");

		shapeMaterials.put("A", ItemTypes.IRON_INGOT.getId());
		shapeMaterials.put("B", ItemTypes.LEVER.getId());
		shapeMaterials.put("C", ItemTypes.COMMAND_BLOCK.getId());
		shapeMaterials.put("D", ItemTypes.STRING.getId());
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
		return keyMaterial.getId();
	}

	@Override
	public String getPadlockFinderMaterialID() {
		return ItemTypes.COMPASS.getId();
	}

}