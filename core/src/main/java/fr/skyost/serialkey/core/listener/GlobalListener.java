package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.item.PluginItemManager;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A listener that allows to globally listen plugin events.
 */

public abstract class GlobalListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Creates a new global listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public GlobalListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

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

	protected void onPreviewItemCraft(
			final SerialKeyPerson player,
			final I[] craftingTable,
			final String shapeId,
			final I result,
			final Function<I, Boolean> isIngredient,
			final Consumer<I> setPreview,
			final Runnable cancelEvent
	) {
		final Boolean permission = hasPermission(player, result);
		if(permission == null) {
			return;
		}

		if(!permission) {
			getPlugin().sendMessage(player, getPlugin().getPluginMessages().getPermissionMessage());
			cancelEvent.run();
			return;
		}

		if(shapeId != null && (shapeId.equals(PluginItemManager.KEY_CLONE_RECIPE_ID) || shapeId.equals(PluginItemManager.PADLOCK_FINDER_RECIPE_ID))) {
			cloneLore(craftingTable, result, isIngredient, setPreview, cancelEvent);
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

	protected void onPlayerLeftClick(final I item, final SerialKeyLocation location, final SerialKeyPerson player, final Consumer<I> dropItem, final Runnable clearHand, final Runnable playBreakSound, final Runnable cancelEvent) {
		if(padlockManager.hasPadlock(location)) {
			if(unlocker.canUnlock(item, location, player)) {
				if(!itemManager.isMasterKey(item)) {
					int amount = 0;
					if(itemManager.isUsedKey(item)) {
						amount = getAmount(item);
						clearHand.run();
					}
					else if(itemManager.isBunchOfKeys(item)) {
						amount = unlocker.removeLocation(item, location);
					}
					if(amount > 0 && plugin.getPluginConfig().areKeysReusable()) {
						final I copy = copy(itemManager.getKeyItem());
						setAmount(copy, amount);
						dropItem.accept(copy);
					}
					else {
						playBreakSound.run();
					}
				}
				padlockManager.unregisterPadlock(location);
				plugin.sendMessage(player, plugin.getPluginMessages().getPadlockRemovedMessage());
			}
			else {
				plugin.sendMessage(player, plugin.getPluginMessages().getBlockHasPadlockMessage());
			}
			cancelEvent.run();
			return;
		}

		boolean isMasterKey = itemManager.isMasterKey(item);
		if(!isPadlockLocationValid(location) || (!itemManager.isBlankKey(item) && !isMasterKey)) {
			return;
		}

		cancelEvent.run();
		if(!isMasterKey && getAmount(item) > 1) {
			final I copy = copy(item);
			setAmount(copy, getAmount(item) - 1);
			dropItem.accept(copy);
			setAmount(item, 1);
		}

		padlockManager.registerPadlock(location, item);
		plugin.sendMessage(player, plugin.getPluginMessages().getPadlockPlacedMessage());
	}

	/**
	 * Triggered when a player right clicks.
	 *
	 * @param item The item that was used.
	 * @param location The click location.
	 * @param player The involved player.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected void onPlayerRightClick(final I item, final SerialKeyLocation location, final SerialKeyPerson player, final Runnable cancelEvent) {
		if(!isPadlockLocationValid(location) || !padlockManager.hasPadlock(location)) {
			return;
		}

		if(!unlocker.canUnlock(item, location, player)) {
			plugin.sendMessage(player, plugin.getPluginMessages().getBlockHasPadlockMessage());
			cancelEvent.run();
		}
	}

	/**
	 * Triggered when a player right clicks an entity.
	 *
	 * @param item The item that was used.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected void onPlayerRightClickEntity(final I item, final Runnable cancelEvent) {
		if(itemManager.isKey(item) || itemManager.isMasterKey(item) || itemManager.isBunchOfKeys(item)) {
			cancelEvent.run();
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

	private Boolean hasPermission(final SerialKeyPerson player, final I result) {
		if(itemManager.isKey(result)) {
			return player.hasPermission(itemManager.getKeyCloneItem().equals(result) ? "serialkey.craft.keyclone" : "serialkey.craft.key");
		}

		if(itemManager.isMasterKey(result)) {
			return player.hasPermission("serialkey.craft.masterkey");
		}

		if(itemManager.isBlankBunchOfKeys(result)) {
			return player.hasPermission("serialkey.craft.bunchofkeys");
		}

		if(itemManager.isBlankPadlockFinder(result)) {
			return player.hasPermission("serialkey.craft.padlockfinder");
		}

		return null;
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

	private void cloneLore(
			final I[] craftingTable,
			final I result,
			final Function<I, Boolean> isIngredient,
			final Consumer<I> setPreview,
			final Runnable cancelEvent
	) {
		I key = null;
		I ingredient = null;
		for(final I item : craftingTable) {
			if(item == null || getAmount(item) >= 2) {
				continue;
			}

			if(itemManager.isUsedKey(item)) {
				key = item;
				continue;
			}

			if(isIngredient.apply(item)) {
				ingredient = item;
			}
		}

		if(key == null || ingredient == null) {
			cancelEvent.run();
			return;
		}

		itemManager.setLore(result, itemManager.getLore(key));
		setPreview.accept(result);
	}

	/**
	 * Copies an item.
	 *
	 * @param item The item.
	 *
	 * @return The copy.
	 */

	protected abstract I copy(final I item);

	/**
	 * Returns the amount of items in the specified stack.
	 *
	 * @param itemStack The item stack.
	 *
	 * @return The amount of items in the specified stack.
	 */

	protected abstract int getAmount(final I itemStack);

	/**
	 * Sets the amount of items in the specified stack.
	 *
	 * @param itemStack The item stack.
	 * @param amount The amount.
	 */

	protected abstract void setAmount(final I itemStack, final int amount);

	/**
	 * Returns whether a padlock can be placed at the specified location.
	 *
	 * @param location The location.
	 *
	 * @return Whether a padlock can be placed at the specified location.
	 */

	protected abstract boolean isPadlockLocationValid(final SerialKeyLocation location);

}