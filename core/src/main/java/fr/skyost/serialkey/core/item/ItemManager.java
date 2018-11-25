package fr.skyost.serialkey.core.item;

import java.util.List;

/**
 * The class that allows to manage plugin items.
 *
 * @param <T> ItemStack class.
 */

public abstract class ItemManager<T> {

	/**
	 * The key item.
	 */

	private T key;

	/**
	 * The master key item.
	 */

	private T masterKey;

	/**
	 * The key clone item.
	 */

	private T keyClone;

	/**
	 * The bunch of keys item.
	 */

	private T bunchOfKeys;

	/**
	 * The padlock finder item.
	 */

	private T padlockFinder;

	/**
	 * Creates a new item manager instance.
	 *
	 * @param key The key item.
	 * @param masterKey The master key item.
	 * @param keyClone The key clone item.
	 * @param bunchOfKeys The bunch of keys item.
	 * @param padlockFinder The padlock finder item.
	 */

	protected ItemManager(final T key, final T masterKey, final T keyClone, final T bunchOfKeys, final T padlockFinder) {
		this.key = key;
		this.masterKey = masterKey;
		this.keyClone = keyClone;
		this.bunchOfKeys = bunchOfKeys;
		this.padlockFinder = padlockFinder;
	}

	/**
	 * Returns the key item.
	 *
	 * @return The key item.
	 */

	public T getKeyItem() {
		return key;
	}

	/**
	 * Sets the key item.
	 *
	 * @param key The key item.
	 */

	public void setKeyItem(final T key) {
		this.key = key;
	}

	/**
	 * Returns the master key item.
	 *
	 * @return The master key item.
	 */

	public T getMasterKeyItem() {
		return masterKey;
	}

	/**
	 * Sets the master key item.
	 *
	 * @param masterKey The master key item.
	 */

	public void setMasterKeyItem(final T masterKey) {
		this.masterKey = masterKey;
	}

	/**
	 * Returns the key clone item.
	 *
	 * @return The key clone item.
	 */

	public T getKeyCloneItem() {
		return keyClone;
	}

	/**
	 * Sets the key clone item.
	 *
	 * @param keyClone The key clone item.
	 */

	public void setKeyCloneItem(final T keyClone) {
		this.keyClone = keyClone;
	}

	/**
	 * Returns the bunch of keys item.
	 *
	 * @return The bunch of keys item.
	 */

	public T getBunchOfKeysItem() {
		return bunchOfKeys;
	}

	/**
	 * Sets the bunch of keys item.
	 *
	 * @param bunchOfKeys The bunch of keys item.
	 */

	public void setBunchOfKeysItem(final T bunchOfKeys) {
		this.bunchOfKeys = bunchOfKeys;
	}

	/**
	 * Returns the padlock finder item.
	 *
	 * @return The padlock finder item.
	 */

	public T getPadlockFinderItem() {
		return padlockFinder;
	}

	/**
	 * Sets the padlock finder item.
	 *
	 * @param padlockFinder The padlock finder item.
	 */

	public void setPadlockFinderItem(final T padlockFinder) {
		this.padlockFinder = padlockFinder;
	}

	/**
	 * Checks if the specified item is a key (blank or used).
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public abstract boolean isKey(final T item);

	/**
	 * Checks if the specified item is a blank key.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isBlankKey(final T item) {
		return isKey(item) && !isUsedKey(item);
	}

	/**
	 * Checks if the specified item is an used key.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isUsedKey(final T item) {
		return isKey(item) && isLoreValid(item);
	}

	/**
	 * Checks if the specified item is a master key.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public abstract boolean isMasterKey(final T item);

	/**
	 * Checks if the specified item is a bunch of keys (blank or used).
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public abstract boolean isBunchOfKeys(final T item);

	/**
	 * Checks if the specified item is a blank bunch of keys.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isBlankBunchOfKeys(final T item) {
		return isBunchOfKeys(item) && !isUsedBunchOfKeys(item);
	}

	/**
	 * Checks if the specified item is an used bunch of keys.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isUsedBunchOfKeys(final T item) {
		return isBunchOfKeys(item) && isLoreValid(item);
	}

	/**
	 * Checks if the specified item is a padlock finder (blank or used).
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public abstract boolean isPadlockFinder(final T item);

	/**
	 * Checks if the specified item is a blank padlock finder.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isBlankPadlockFinder(final T item) {
		return isPadlockFinder(item) && !isUsedPadlockFinder(item);
	}

	/**
	 * Checks if the specified item is an used padlock finder.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isUsedPadlockFinder(final T item) {
		return isPadlockFinder(item) && isLoreValid(item);
	}

	/**
	 * Returns the specified item lore (must not be null).
	 *
	 * @param object The item.
	 *
	 * @return The specified item lore.
	 */

	public abstract List<String> getLore(final T object);

	/**
	 * Applies the lore to the specified item.
	 *
	 * @param object The item.
	 * @param lore The lore.
	 */

	public abstract void setLore(final T object, final List<String> lore);

	/**
	 * Returns whether the lore of the specified item is valid or not.
	 *
	 * @param object The iteù.
	 *
	 * @return <b>true</b> Yes.
	 * <br><b>false</b> Otherwise.
	 */

	public boolean isLoreValid(final T object) {
		final List<String> lore = getLore(object);
		return !lore.isEmpty() && lore.size() % 2 == 0;
	}

}