package fr.skyost.serialkey.core.item;

import fr.skyost.serialkey.core.config.SerialKeyConfig;
import fr.skyost.serialkey.core.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An item manager that depends on a plugin.
 *
 * @param <T> ItemStack class.
 */

public abstract class PluginItemManager<T> extends ItemManager<T> {

	/**
	 * The key recipe ID.
	 */

	public static final String KEY_RECIPE_ID = "key";

	/**
	 * The master key recipe ID.
	 */

	public static final String MASTER_KEY_RECIPE_ID = "master_key";

	/**
	 * The key clone recipe ID.
	 */

	public static final String KEY_CLONE_RECIPE_ID = "key_clone";

	/**
	 * The bunch of keys recipe ID.
	 */

	public static final String BUNCH_OF_KEYS_RECIPE_ID = "bunch_of_keys";

	/**
	 * The padlock finder recipe ID.
	 */

	public static final String PADLOCK_FINDER_RECIPE_ID = "padlock_finder";

	/**
	 * The SerialKey configuration instance.
	 */

	protected final SerialKeyConfig config;

	/**
	 * Creates a new plugin item manager instance.
	 *
	 * @param config The SerialKey configuration instance.
	 * @param key The key item.
	 * @param masterKey The master key item.
	 * @param keyClone The key clone item.
	 * @param bunchOfKeys The bunch of keys item.
	 * @param padlockFinder The padlock finder item.
	 */

	public PluginItemManager(final SerialKeyConfig config, final T key, final T masterKey, final T keyClone, final T bunchOfKeys, final T padlockFinder) {
		super(key, masterKey, keyClone, bunchOfKeys, padlockFinder);

		this.config = config;
	}

	/**
	 * Creates all required recipes.
	 */

	public void createRecipes() {
		createRecipe(KEY_RECIPE_ID, getKeyItem(), config.getKeyShape());
		createRecipe(MASTER_KEY_RECIPE_ID, getMasterKeyItem(), config.getMasterKeyShape());
		createRecipe(KEY_CLONE_RECIPE_ID, getKeyCloneItem(), Collections.singletonList("YY"), Objects.requireNonNull(Util.createMap(new String[]{"Y"}, new String[]{config.getKeyMaterialID()})));
		createRecipe(BUNCH_OF_KEYS_RECIPE_ID, getBunchOfKeysItem(), config.getBunchOfKeysShape());
		createRecipe(PADLOCK_FINDER_RECIPE_ID, getPadlockFinderItem(), Collections.singletonList("ZY"), Objects.requireNonNull(Util.createMap(new String[]{"Z", "Y"}, new String[]{config.getPadlockFinderMaterialID(), config.getKeyMaterialID()})));
	}

	/**
	 * Creates a recipe for an item.
	 *
	 * @param id The recipe ID.
	 * @param result The item.
	 * @param shape The shape.
	 */

	private void createRecipe(final String id, final T result, final List<String> shape) {
		createRecipe(id, result, shape, config.getShapeMaterials());
	}

	/**
	 * Creates a recipe for an item.
	 *
	 * @param id The recipe ID.
	 * @param result The item.
	 * @param shape The shape.
	 * @param ingredients The ingredients needed for the craft.
	 */

	protected abstract void createRecipe(final String id, final T result, final List<String> shape, final Map<String, String> ingredients);

	@Override
	public boolean isKey(final T item) {
		return areItemsEqual(getKeyItem(), item);
	}

	@Override
	public boolean isMasterKey(final T item) {
		return areItemsEqual(getMasterKeyItem(), item);
	}

	@Override
	public boolean isBunchOfKeys(final T item) {
		return areItemsEqual(getBunchOfKeysItem(), item);
	}

	@Override
	public boolean isPadlockFinder(final T item) {
		return areItemsEqual(getPadlockFinderItem(), item);
	}

	/**
	 * Returns whether the specified items are equal.
	 *
	 * @param item1 The first item.
	 * @param item2 The second item.
	 *
	 * @return <b>true</b> Yes.
	 * <br><b>false</b> Otherwise.
	 */

	private boolean areItemsEqual(final T item1, final T item2) {
		return isItemValid(item2) && compareItemsType(item1, item2) && (config.canRenameItems() || compareItemsName(item1, item2));
	}

	/**
	 * Checks if the specified item is valid.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */

	protected abstract boolean isItemValid(final T item);

	/**
	 * Compare the specified items name.
	 *
	 * @param item1 The first item.
	 * @param item2 The second item.
	 *
	 * @return <b>true :</b> they are equal.
	 * <br /><b>false :</b> they are not equal.
	 */

	protected abstract boolean compareItemsName(final T item1, final T item2);

	/**
	 * Compare the specified items type.
	 *
	 * @param item1 The first item.
	 * @param item2 The second item.
	 *
	 * @return <b>true :</b> they are equal.
	 * <br /><b>false :</b> they are not equal.
	 */

	protected abstract boolean compareItemsType(final T item1, final T item2);

}