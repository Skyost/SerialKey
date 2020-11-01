package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.bukkit.util.DoorUtil
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.listener.GlobalListener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.block.data.type.TrapDoor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import java.util.function.Function

/**
 * A listener that allows to globally listen plugin events.
 */
class BukkitGlobalListener(plugin: SerialKeyPlugin<ItemStack, Location>) : GlobalListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        val craftingTable = event.inventory
        val result = craftingTable.result ?: return

        val key = if (event.recipe is ShapedRecipe) (event.recipe as ShapedRecipe?)!!.key.key else null
        super.onPreviewItemCraft(
                BukkitTypeConverter.toSerialKeyPerson(event.view.player),
                craftingTable.matrix,
                key,
                result,
                if (itemManager.keyCloneItem == result) Function { item: ItemStack? -> itemManager.isBlankKey(item) } else Function { item: ItemStack -> item.type == Material.COMPASS },
                { },
                { craftingTable.result = null }
        )
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (event.action == Action.LEFT_CLICK_BLOCK) {
            super.onPlayerLeftClick(
                    event.item,
                    BukkitTypeConverter.toSerialKeyLocation(event.clickedBlock!!.location),
                    BukkitTypeConverter.toSerialKeyPerson(event.player),
                    { item: ItemStack? -> player.world.dropItemNaturally(player.location, item!!) },
                    { player.inventory.setItemInMainHand(ItemStack(Material.AIR)) },
                    { player.playSound(event.clickedBlock!!.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f) },
                    { event.isCancelled = true }
            )
            return
        }
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            super.onPlayerRightClick(
                    event.item,
                    BukkitTypeConverter.toSerialKeyLocation(event.clickedBlock!!.location),
                    BukkitTypeConverter.toSerialKeyPerson(event.player)
            ) { event.isCancelled = true }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        super.onPlayerRightClickEntity(
                if (event.hand == EquipmentSlot.HAND) event.player.inventory.itemInMainHand else event.player.inventory.itemInOffHand
        ) { event.isCancelled = true }
    }

    override fun copy(item: ItemStack): ItemStack {
        return item.clone()
    }

    override fun getAmount(itemStack: ItemStack): Int {
        return itemStack.amount
    }

    override fun setAmount(itemStack: ItemStack, amount: Int) {
        itemStack.amount = amount
    }

    override fun isPadlockLocationValid(location: SerialKeyLocation): Boolean {
        val block = BukkitTypeConverter.toBukkitLocation(location).block
        return block.state is Chest || DoorUtil.getInstance(block.blockData) != null || block.blockData is TrapDoor
    }
}