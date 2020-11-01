package fr.skyost.serialkey.sponge.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.listener.GlobalListener
import fr.skyost.serialkey.sponge.BuildConfig
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyLocation
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyPerson
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSpongeLocation
import fr.skyost.serialkey.sponge.util.ChestUtil.isChest
import fr.skyost.serialkey.sponge.util.DoorUtil.isDoor
import fr.skyost.serialkey.sponge.util.DoorUtil.isTrapdoor
import fr.skyost.serialkey.sponge.util.Util.blankItem
import fr.skyost.serialkey.sponge.util.Util.dropItemAt
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.game.GameRegistryEvent
import org.spongepowered.api.event.item.inventory.CraftItemEvent.Preview
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

/**
 * A listener that allows to globally listen plugin events.
 */
class SpongeGlobalListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : GlobalListener<ItemStack, Location<World>>(plugin) {
    @Listener(order = Order.EARLY)
    fun onPreviewItemCraft(event: Preview, @First player: Player?) {
        val preview = event.preview
        val result = preview.final.createStack()
        val items: MutableList<ItemStack> = ArrayList()
        event.craftingInventory.slots<Inventory>().forEach(Consumer { slot: Inventory -> items.add(slot.peek().orElse(null)) })
        val id = if (event.recipe.isPresent) event.recipe.get().id.replaceFirst(BuildConfig.PLUGIN_ID + ":".toRegex(), "") else null
        super.onPreviewItemCraft(
                toSerialKeyPerson(player!!),
                items.toTypedArray(),
                id,
                result,
                if (itemManager.isBlankKey(result)) Function { item: ItemStack? -> itemManager.isBlankKey(item) } else Function { item: ItemStack -> item.type === ItemTypes.COMPASS },
                { item: ItemStack -> event.preview.setCustom(item) },
                { preview.setCustom(blankItem()) }
        )
    }

    @Listener(order = Order.LATE)
    fun onPlayerLeftClick(event: InteractBlockEvent.Primary, @First player: Player) {
        event.targetBlock.location.ifPresent { location: Location<World> ->
            super.onPlayerLeftClick(
                    player.getItemInHand(event.handType).orElse(blankItem()),
                    toSerialKeyLocation(location),
                    toSerialKeyPerson(player),
                    { item: ItemStack? -> dropItemAt(item!!, player.location) },
                    { player.setItemInHand(event.handType, blankItem()) },
                    { location.extent.playSound(SoundTypes.ENTITY_ITEM_BREAK, location.position, 1.0) },
                    { event.isCancelled = true }
            )
        }
    }

    @Listener(order = Order.LATE)
    fun onPlayerRightClick(event: InteractBlockEvent.Secondary, @First player: Player) {
        event.targetBlock.location.ifPresent { location: Location<World> ->
            super.onPlayerRightClick(
                    player.getItemInHand(event.handType).orElse(blankItem()),
                    toSerialKeyLocation(location),
                    toSerialKeyPerson(player)
            ) { event.isCancelled = true }
        }
    }

    @Listener(order = Order.LATE)
    fun onPlayerRightClickEntity(event: InteractEntityEvent.Secondary, @First player: Player) {
        super.onPlayerRightClickEntity(
                player.getItemInHand(event.handType).orElse(blankItem())
        ) { event.isCancelled = true }
    }

    @Listener
    fun onCraftRecipeRegister(event: GameRegistryEvent.Register<CraftingRecipe>) {
        itemManager.createRecipes<CraftingRecipe>(event::register)
    }

    override fun copy(item: ItemStack): ItemStack {
        return item.copy()
    }

    override fun getAmount(itemStack: ItemStack): Int {
        return itemStack.quantity
    }

    override fun setAmount(itemStack: ItemStack, amount: Int) {
        itemStack.quantity = amount
    }

    override fun isPadlockLocationValid(location: SerialKeyLocation): Boolean {
        val type = toSpongeLocation(location).blockType
        return isChest(type) || isDoor(type) || isTrapdoor(type)
    }
}