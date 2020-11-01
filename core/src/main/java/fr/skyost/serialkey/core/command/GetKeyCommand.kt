package fr.skyost.serialkey.core.command

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.PersonIdentity
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import java.util.function.Consumer

/**
 * Represents the <pre>/serialkey getkey</pre> command executor.
 */
abstract class GetKeyCommand<I>(private val plugin: SerialKeyPlugin<I, *>) {
    /**
     * Executes the command.
     *
     * @param executor The executor.
     * @param targetedBlock The targeted block.
     * @param dropItem The function that allows to drop an item.
     *
     * @return Number of affected entities.
     */
    protected fun execute(executor: SerialKeyPerson, targetedBlock: SerialKeyLocation?, dropItem: Consumer<I>): Int {
        if (executor.identity.type !== PersonIdentity.Type.PLAYER) {
            plugin.sendMessage(executor, "&cYou must be a player to run this command.")
            return 0
        }
        if (!executor.hasPermission("serialkey.command.getkey")) {
            plugin.sendMessage(executor, plugin.pluginMessages.permissionMessage)
            return 0
        }
        if (targetedBlock != null && plugin.padlockManager.hasPadlock(targetedBlock)) {
            val key = copyItem(plugin.itemManager.keyItem)
            plugin.unlocker.setLocation(key, targetedBlock)
            dropItem.accept(key)
            return 1
        }
        plugin.sendMessage(executor, "&cYou must be looking at a valid block.")
        return 0
    }

    /**
     * Returns the plugin instance.
     *
     * @return The plugin instance.
     */
    fun getPlugin(): SerialKeyPlugin<*, *> {
        return plugin
    }

    /**
     * Copies an item.
     *
     * @param item The item.
     *
     * @return The copied item.
     */
    protected abstract fun copyItem(item: I): I
}