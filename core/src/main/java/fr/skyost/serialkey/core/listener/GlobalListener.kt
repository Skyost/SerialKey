package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import fr.skyost.serialkey.core.item.PluginItemManager
import java.util.function.Consumer
import java.util.function.Function

/**
 * A listener that allows to globally listen plugin events.
 */
abstract class GlobalListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when a craft is previewed.
     *
     * @param player The involved player.
     * @param craftingTable The crafting table items.
     * @param shapeId The shape ID.
     * @param result The result item.
     * @param isIngredient Function to check whether an item is an ingredient.
     * @param setPreview Function to change the preview.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onPreviewItemCraft(
            player: SerialKeyPerson,
            craftingTable: Array<I>,
            shapeId: String?,
            result: I,
            isIngredient: Function<I, Boolean>,
            setPreview: Consumer<I>,
            cancelEvent: Runnable
    ) {
        val permission = hasPermission(player, result) ?: return
        if (!permission) {
            plugin.sendMessage(player, plugin.pluginMessages.permissionMessage)
            cancelEvent.run()
            return
        }
        if (shapeId != null && (shapeId == PluginItemManager.KEY_CLONE_RECIPE_ID || shapeId == PluginItemManager.PADLOCK_FINDER_RECIPE_ID)) {
            cloneLore(craftingTable, result, isIngredient, setPreview, cancelEvent)
        }
    }

    /**
     * Triggered when a player left clicks.
     *
     * @param item The item that was used.
     * @param location The click location.
     * @param player The involved player.
     * @param dropItem Function that allows to drop an item.
     * @param clearHand Clears player's hand.
     * @param playBreakSound Play an item break sound.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onPlayerLeftClick(item: I?, location: SerialKeyLocation, player: SerialKeyPerson, dropItem: Consumer<I>, clearHand: Runnable, playBreakSound: Runnable, cancelEvent: Runnable) {
        if (padlockManager.hasPadlock(location)) {
            if (unlocker.canUnlock(item, location, player)) {
                if (!itemManager.isMasterKey(item)) {
                    var amount = 0
                    if (itemManager.isUsedKey(item)) {
                        amount = getAmount(item!!)
                        clearHand.run()
                    } else if (itemManager.isBunchOfKeys(item)) {
                        amount = unlocker.removeLocation(item!!, location).toInt()
                    }
                    if (amount > 0 && plugin.pluginConfig.reusableKeys) {
                        val copy = copy(itemManager.keyItem)
                        setAmount(copy, amount)
                        dropItem.accept(copy)
                    } else {
                        playBreakSound.run()
                    }
                }
                padlockManager.unregisterPadlock(location)
                plugin.sendMessage(player, plugin.pluginMessages.padlockRemovedMessage)
            } else {
                plugin.sendMessage(player, plugin.pluginMessages.blockHasPadlockMessage)
            }
            cancelEvent.run()
            return
        }
        val isMasterKey = itemManager.isMasterKey(item)
        if (!isPadlockLocationValid(location) || !itemManager.isBlankKey(item) && !isMasterKey) {
            return
        }
        cancelEvent.run()
        if (!isMasterKey && getAmount(item!!) > 1) {
            val copy = copy(item)
            setAmount(copy, getAmount(item) - 1)
            dropItem.accept(copy)
            setAmount(item, 1)
        }
        padlockManager.registerPadlock(location, item)
        plugin.sendMessage(player, plugin.pluginMessages.padlockPlacedMessage)
    }

    /**
     * Triggered when a player right clicks.
     *
     * @param item The item that was used.
     * @param location The click location.
     * @param player The involved player.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onPlayerRightClick(item: I?, location: SerialKeyLocation, player: SerialKeyPerson, cancelEvent: Runnable) {
        if (!isPadlockLocationValid(location) || !padlockManager.hasPadlock(location)) {
            return
        }
        if (!unlocker.canUnlock(item, location, player)) {
            plugin.sendMessage(player, plugin.pluginMessages.blockHasPadlockMessage)
            cancelEvent.run()
        }
    }

    /**
     * Triggered when a player right clicks an entity.
     *
     * @param item The item that was used.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onPlayerRightClickEntity(item: I, cancelEvent: Runnable) {
        if (itemManager.isKey(item) || itemManager.isMasterKey(item) || itemManager.isBunchOfKeys(item)) {
            cancelEvent.run()
        }
    }

    /**
     * Checks whether the specified player has the permission to craft the specified item.
     *
     * @param player The player.
     * @param result The item.
     *
     * @return Whether the specified player has the permission to craft the specified item (can be null if the item does not correspond to a plugin item).
     */
    private fun hasPermission(player: SerialKeyPerson, result: I?): Boolean? {
        if (itemManager.isKey(result)) {
            return player.hasPermission(if (itemManager.keyCloneItem == result) "serialkey.craft.keyclone" else "serialkey.craft.key")
        }
        if (itemManager.isMasterKey(result)) {
            return player.hasPermission("serialkey.craft.masterkey")
        }
        if (itemManager.isBlankBunchOfKeys(result)) {
            return player.hasPermission("serialkey.craft.bunchofkeys")
        }
        return if (itemManager.isBlankPadlockFinder(result)) {
            player.hasPermission("serialkey.craft.padlockfinder")
        } else null
    }

    /**
     * Applies the lore of an used key to the craft result.
     *
     * @param craftingTable The crafting table items.
     * @param result The result item.
     * @param isIngredient Function to check whether an item is an ingredient.
     * @param setPreview Function to change the preview.
     * @param cancelEvent The runnable that cancels the event.
     */
    private fun cloneLore(
            craftingTable: Array<I>,
            result: I,
            isIngredient: Function<I, Boolean>,
            setPreview: Consumer<I>,
            cancelEvent: Runnable
    ) {
        var key: I? = null
        var ingredient: I? = null
        for (item in craftingTable) {
            if (item == null || getAmount(item) >= 2) {
                continue
            }
            if (itemManager.isUsedKey(item)) {
                key = item
                continue
            }
            if (isIngredient.apply(item)) {
                ingredient = item
            }
        }
        if (key == null || ingredient == null) {
            cancelEvent.run()
            return
        }
        itemManager.setLore(result, itemManager.getLore(key))
        setPreview.accept(result)
    }

    /**
     * Copies an item.
     *
     * @param item The item.
     *
     * @return The copy.
     */
    protected abstract fun copy(item: I): I

    /**
     * Returns the amount of items in the specified stack.
     *
     * @param itemStack The item stack.
     *
     * @return The amount of items in the specified stack.
     */
    protected abstract fun getAmount(itemStack: I): Int

    /**
     * Sets the amount of items in the specified stack.
     *
     * @param itemStack The item stack.
     * @param amount The amount.
     */
    protected abstract fun setAmount(itemStack: I, amount: Int)

    /**
     * Returns whether a padlock can be placed at the specified location.
     *
     * @param location The location.
     *
     * @return Whether a padlock can be placed at the specified location.
     */
    protected abstract fun isPadlockLocationValid(location: SerialKeyLocation): Boolean
}