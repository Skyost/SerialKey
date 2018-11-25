package fr.skyost.serialkey.core.config;

import java.util.List;
import java.util.Map;

/**
 * Represents a SerialKey configuration.
 */

public interface SerialKeyConfig {

	/**
	 * Whether keys are reusable or not.
	 *
	 * @return <b>true</b> Yes.
	 * <br><b>false</b> Otherwise.
	 */

	boolean areKeysReusable();

	/**
	 * Whether lores are encrypted or not.
	 *
	 * @return <b>true</b> Yes.
	 * <br><b>false</b> Otherwise.
	 */

	boolean areLoresEncrypted();

	/**
	 * Whether players can rename items or not.
	 *
	 * @return <b>true</b> Yes.
	 * <br><b>false</b> Otherwise.
	 */

	boolean canRenameItems();

	/**
	 * The key shape.
	 *
	 * @return The key shape.
	 */

	List<String> getKeyShape();

	/**
	 * The master key shape.
	 *
	 * @return The master key shape.
	 */

	List<String> getMasterKeyShape();

	/**
	 * The bunch of keys shape.
	 *
	 * @return The bunch of keys shape.
	 */

	List<String> getBunchOfKeysShape();

	/**
	 * Returns the shape materials.
	 *
	 * @return The shape materials.
	 */

	Map<String, String> getShapeMaterials();

	/**
	 * Returns the key material ID.
	 *
	 * @return The key material ID.
	 */

	String getKeyMaterialID();

	/**
	 * Returns the padlock finder material ID.
	 *
	 * @return The padlock finder material ID.
	 */

	String getPadlockFinderMaterialID();

}