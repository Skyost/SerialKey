package fr.skyost.serialkey.sponge.command;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.command.GetKeyCommand;
import fr.skyost.serialkey.sponge.SpongeTypeConverter;
import fr.skyost.serialkey.sponge.util.Util;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represents the <pre>/serialkey getkey</pre> command executor.
 */

public class SpongeGetKeyCommand extends GetKeyCommand<ItemStack> implements CommandExecutor {

	/**
	 * Creates a new <pre>/serialkey getkey</pre> command executor instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public SpongeGetKeyCommand(final SerialKeyPlugin<ItemStack, ?> plugin) {
		super(plugin);
	}

	@Override
	@Nonnull
	public CommandResult execute(@Nonnull final CommandSource source, @Nonnull final CommandContext args) {
		final Player player = source instanceof Player ? (Player)source : null;
		final Optional<BlockRayHit<World>> hit = player == null ? Optional.empty() : BlockRay.from(player).distanceLimit(100d).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build().end();

		final int affectedEntities = super.execute(
				SpongeTypeConverter.toSerialKeyPerson(source),
				hit.map(hitBlock -> SpongeTypeConverter.toSerialKeyLocation(hitBlock.getLocation())).orElse(null),
				player == null ? item -> {} : (item -> Util.dropItemAt(item, player.getLocation()))
		);

		return affectedEntities > 0 ? CommandResult.affectedEntities(affectedEntities) : CommandResult.empty();
	}

	@Override
	protected ItemStack copyItem(final ItemStack item) {
		return item.copy();
	}

}