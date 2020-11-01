package fr.skyost.serialkey.sponge.util

import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

/**
 * Contains some useful methods.
 */
object Util {
    /**
     * The available color codes.
     */
    private val COLORS = arrayOf(
            "&1",
            "&2",
            "&3",
            "&4",
            "&5",
            "&6",
            "&7",
            "&8",
            "&9",
            "&a",
            "&b",
            "&c",
            "&d",
            "&e",
            "&f"
    )

    /**
     * Strips colors from the specified string.
     *
     * @param string The string.
     *
     * @return The handled string.
     */
    @JvmStatic
    fun stripColor(string: String): String {
        return TextSerializers.FORMATTING_CODE.deserialize(string).toPlain()
    }

    /**
     * Returns a random text color.
     *
     * @return A random text color.
     */
    @JvmStatic
    fun randomTextColor(): String {
        val random = Random()
        return COLORS[random.nextInt(COLORS.size)]
    }

    /**
     * Parses a string with color codes.
     *
     * @param string The string.
     *
     * @return The parsed string (as a Text).
     */
    @JvmStatic
    fun parseString(string: String): Text {
        return TextSerializers.FORMATTING_CODE.deserialize(string)
    }

    /**
     * Creates an item with a custom name.
     *
     * @param name The name.
     * @param material The item's material.
     *
     * @return The item.
     */
    @JvmStatic
    fun createItem(name: String, material: ItemType): ItemStack {
        return ItemStack.builder()
                .add(Keys.DISPLAY_NAME, parseString(name))
                .itemType(material)
                .build()
    }

    /**
     * Drops an item at the specified location.
     *
     * @param item The item.
     * @param location The location.
     */
    @JvmStatic
    fun dropItemAt(item: ItemStack, location: Location<World>) {
        val drop = location.extent.createEntity(EntityTypes.ITEM, location.position)
        drop.offer(Keys.REPRESENTED_ITEM, item.createSnapshot())
        location.extent.spawnEntity(drop)
    }

    /**
     * Creates a blank item.
     *
     * @return The blank item.
     */
    @JvmStatic
    fun blankItem(): ItemStack {
        return ItemStack.builder().itemType(ItemTypes.AIR).build()
    }
}