package fr.skyost.serialkey.core.config

/**
 * Represents a SerialKey configuration.
 */
interface SerialKeyConfig {
    /**
     * Whether keys are reusable or not.
     *
     * @return **true** Yes.
     * <br></br>**false** Otherwise.
     */
    val reusableKeys: Boolean

    /**
     * Whether lores are encrypted or not.
     *
     * @return **true** Yes.
     * <br></br>**false** Otherwise.
     */
    val encryptLore: Boolean

    /**
     * Whether players can rename items or not.
     *
     * @return **true** Yes.
     * <br></br>**false** Otherwise.
     */
    val canRenameItems: Boolean

    /**
     * The key shape.
     *
     * @return The key shape.
     */
    val keyShape: List<String>

    /**
     * The master key shape.
     *
     * @return The master key shape.
     */
    val masterKeyShape: List<String>

    /**
     * The bunch of keys shape.
     *
     * @return The bunch of keys shape.
     */
    val bunchOfKeysShape: List<String>

    /**
     * Returns the shape materials.
     *
     * @return The shape materials.
     */
    val shapeMaterials: Map<String, String>

    /**
     * Returns the key material ID.
     *
     * @return The key material ID.
     */
    fun getKeyMaterialID(): String

    /**
     * Returns the padlock finder material ID.
     *
     * @return The padlock finder material ID.
     */
    fun getPadlockFinderMaterialID(): String
}