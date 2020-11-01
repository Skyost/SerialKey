package fr.skyost.serialkey.sponge.unlocker

import fr.skyost.serialkey.core.unlocker.PluginUnlocker
import fr.skyost.serialkey.sponge.SerialKey
import fr.skyost.serialkey.sponge.util.Util
import fr.skyost.serialkey.sponge.util.Util.randomTextColor
import org.spongepowered.api.item.inventory.ItemStack

/**
 * The Sponge unlocker class.
 */
class SpongeUnlocker(plugin: SerialKey) : PluginUnlocker<ItemStack>(plugin) {
    override fun randomColor(): String {
        return randomTextColor()
    }

    override fun stripColor(string: String): String {
        return Util.stripColor(string)
    }
}