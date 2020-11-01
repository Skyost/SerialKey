package fr.skyost.serialkey.sponge.command

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.command.GetKeyCommand
import fr.skyost.serialkey.sponge.SpongeTypeConverter
import fr.skyost.serialkey.sponge.util.Util
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.blockray.BlockRay
import org.spongepowered.api.util.blockray.BlockRayHit
import org.spongepowered.api.world.World
import java.util.*
import java.util.function.Consumer

/**
 * Represents the <pre>/serialkey getkey</pre> command executor.
 */
class SpongeGetKeyCommand(plugin: SerialKeyPlugin<ItemStack, *>) : GetKeyCommand<ItemStack>(plugin), CommandExecutor {
    override fun execute(source: CommandSource, args: CommandContext): CommandResult {
        val player = if (source is Player) source else null
        val hit = if (player == null) Optional.empty() else BlockRay.from(player).distanceLimit(100.0).whilst(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build().end()
        val affectedEntities = super.execute(
                SpongeTypeConverter.toSerialKeyPerson(source),
                hit.map { hitBlock: BlockRayHit<World> -> SpongeTypeConverter.toSerialKeyLocation(hitBlock.location) }.orElse(null),
                if (player == null) Consumer { } else Consumer { item: ItemStack -> Util.dropItemAt(item, player.location) }
        )
        return if (affectedEntities > 0) CommandResult.affectedEntities(affectedEntities) else CommandResult.empty()
    }

    override fun copyItem(item: ItemStack): ItemStack {
        return item.copy()
    }
}