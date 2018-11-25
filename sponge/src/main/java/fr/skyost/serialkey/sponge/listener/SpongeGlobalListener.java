package fr.skyost.serialkey.sponge.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.GlobalListener;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.sponge.BuildConfig;
import fr.skyost.serialkey.sponge.SpongeTypeConverter;
import fr.skyost.serialkey.sponge.util.ChestUtil;
import fr.skyost.serialkey.sponge.util.DoorUtil;
import fr.skyost.serialkey.sponge.util.Util;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener that allows to globally listen plugin events.
 */

public class SpongeGlobalListener extends GlobalListener<ItemStack, Location<World>> {

	/**
	 * Creates a new global listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public SpongeGlobalListener(final SerialKeyPlugin<ItemStack, Location<World>> plugin) {
		super(plugin);
	}

	@Listener(order = Order.EARLY)
	public void onPreviewItemCraft(final CraftItemEvent.Preview event, @First final Player player) {
		final SlotTransaction preview = event.getPreview();
		final ItemStack result = preview.getFinal().createStack();

		final List<ItemStack> items = new ArrayList<>();
		event.getCraftingInventory().slots().forEach(slot -> items.add(slot.peek().orElse(null)));

		final String id = event.getRecipe().isPresent() ? event.getRecipe().get().getId().replaceFirst(BuildConfig.PLUGIN_ID + ":", "") : null;
		super.onPreviewItemCraft(
				SpongeTypeConverter.toSerialKeyPerson(player),
				items.toArray(new ItemStack[0]),
				id,
				result,
				itemManager.isBlankKey(result) ? (item -> itemManager.isBlankKey(item)) : (item -> item.getType() == ItemTypes.COMPASS),
				item -> event.getPreview().setCustom(item),
				() -> preview.setCustom(Util.blankItem())
		);
	}

	@Listener(order = Order.LATE)
	public void onPlayerLeftClick(final InteractBlockEvent.Primary event, @First final Player player) {
		event.getTargetBlock().getLocation().ifPresent(
				location -> super.onPlayerLeftClick(
						player.getItemInHand(event.getHandType()).orElse(Util.blankItem()),
						SpongeTypeConverter.toSerialKeyLocation(location),
						SpongeTypeConverter.toSerialKeyPerson(player),
						SpongeTypeConverter.toSerialKeyLocation(player.getLocation()),
						item -> Util.dropItemAt(item, player.getLocation()),
						() -> player.setItemInHand(event.getHandType(), Util.blankItem()),
						() -> location.getExtent().playSound(SoundTypes.ENTITY_ITEM_BREAK, location.getPosition(), 1f),
						() -> event.setCancelled(true)
				)
		);
	}

	@Listener(order = Order.LATE)
	public void onPlayerRightClick(final InteractBlockEvent.Secondary event, @First final Player player) {
		event.getTargetBlock().getLocation().ifPresent(location -> super.onPlayerRightClick(
			player.getItemInHand(event.getHandType()).orElse(Util.blankItem()),
			SpongeTypeConverter.toSerialKeyLocation(location),
			SpongeTypeConverter.toSerialKeyPerson(player),
			() -> event.setCancelled(true)
		));
	}

	@Override
	protected ItemStack copy(final ItemStack item) {
		return item.copy();
	}

	@Override
	protected int getAmount(final ItemStack item) {
		return item.getQuantity();
	}

	@Override
	protected void setAmount(final ItemStack item, final int amount) {
		item.setQuantity(amount);
	}

	@Override
	protected boolean isPadlockLocationValid(final SerialKeyLocation location) {
		final BlockType type = SpongeTypeConverter.toSpongeLocation(location).getBlockType();
		return ChestUtil.isChest(type) || DoorUtil.isDoor(type) || DoorUtil.isTrapdoor(type);
	}

}