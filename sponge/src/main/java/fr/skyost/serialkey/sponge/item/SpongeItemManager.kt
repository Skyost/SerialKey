package fr.skyost.serialkey.sponge.item;

import fr.skyost.serialkey.core.item.PluginItemManager;
import fr.skyost.serialkey.core.unlocker.LoreUnlocker;
import fr.skyost.serialkey.sponge.BuildConfig;
import fr.skyost.serialkey.sponge.SerialKey;
import fr.skyost.serialkey.sponge.config.SpongePluginConfig;
import fr.skyost.serialkey.sponge.util.Util;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Sponge item manager class.
 */

public class SpongeItemManager extends PluginItemManager<ItemStack> {

	/**
	 * The plugin instance.
	 */

	private final SerialKey plugin;

	/**
	 * Creates a new Sponge item manager instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public SpongeItemManager(final SerialKey plugin) {
		this(plugin, plugin.getPluginConfig(), Util.createItem(plugin.getPluginConfig().keyName, plugin.getPluginConfig().keyMaterial));
	}

	/**
	 * Creates a new Bukkit item manager instance.
	 *
	 * @param plugin The plugin instance.
	 * @param key The key item.
	 */

	private SpongeItemManager(final SerialKey plugin, final SpongePluginConfig config, final ItemStack key) {
		super(plugin.getPluginConfig(), key, Util.createItem(config.masterKeyName, config.masterKeyMaterial), key.copy(), Util.createItem(config.bunchOfKeysName, config.bunchOfKeysMaterial), Util.createItem(config.padlockFinderName, ItemTypes.COMPASS));

		this.plugin = plugin;
		getKeyCloneItem().setQuantity(2);
	}

	/**
	 * Creates a recipe for an item.
	 *
	 * @param id The recipe ID.
	 * @param result The item.
	 * @param shape The shape.
	 * @param ingredients The ingredients needed for the craft.
	 */

	@Override
	public void createRecipe(final String id, final ItemStack result, final List<String> shape, Map<String, String> ingredients) {
		if(ingredients.equals(plugin.getPluginConfig().shapeMaterials)) {
			ingredients = fr.skyost.serialkey.core.util.Util.keepAll(ingredients, shape);
		}

		final GameRegistry registry = Sponge.getRegistry();
		final Map<Character, Ingredient> where = new HashMap<>();
		ingredients.forEach((character, type) -> registry.getType(ItemType.class, type).ifPresent(itemType -> where.put(character.charAt(0), Ingredient.of(itemType))));

		final CraftingRecipe recipe = ShapedCraftingRecipe.builder()
				.aisle(shape.toArray(new String[0]))
				.where(where)
				.result(result)
				.build(id, plugin);

		registry.getCraftingRecipeRegistry().register(recipe);
	}

	@Override
	protected boolean isItemValid(final ItemStack item) {
		return item != null && item.get(Keys.DISPLAY_NAME).isPresent();
	}

	@Override
	protected boolean compareItemsName(final ItemStack item1, final ItemStack item2) {
		return item1.get(Keys.DISPLAY_NAME).equals(item2.get(Keys.DISPLAY_NAME));
	}

	@Override
	protected boolean compareItemsType(final ItemStack item1, final ItemStack item2) {
		return item1.getType() == item2.getType();
	}

	@Override
	public List<String> getLore(final ItemStack object) {
		if(object == null) {
			return new ArrayList<>();
		}

		final Optional<List<Text>> lore = object.get(Keys.ITEM_LORE);
		return lore.map(texts -> texts.stream().map(TextSerializers.FORMATTING_CODE::serialize).collect(Collectors.toList())).orElseGet(ArrayList::new);
	}

	@Override
	public void setLore(final ItemStack object, final List<String> lore) {
		object.offer(Keys.ITEM_LORE, lore == null ? new ArrayList<>() : lore.stream().map(Util::parseString).collect(Collectors.toList()));
	}

	/**
	 * Creates an inventory for the specified bunch of keys and opens it for the specified players.
	 *
	 * @param unlocker The unlocker.
	 * @param bunchOfKeys The bunch of keys item.
	 * @param players The players.
	 *
	 * @return The inventory.
	 */

	public Inventory createInventory(final LoreUnlocker<ItemStack> unlocker, final ItemStack bunchOfKeys, final Player... players) {
		final Inventory inventory = Inventory.builder()
				.property(InventoryTitle.of(bunchOfKeys.get(Keys.DISPLAY_NAME).orElse(Util.parseString(BuildConfig.PLUGIN_NAME))))
				.property(InventoryDimension.of(9, 1))
				.listener(InteractInventoryEvent.Close.class, event -> event.getCause().first(Player.class).ifPresent(player -> onBunchOfKeysInventoryClose(unlocker, event.getTargetInventory().first(), bunchOfKeys, player)))
				.build(plugin);

		final List<Text> lore = bunchOfKeys.get(Keys.ITEM_LORE).orElse(new ArrayList<>());
		final int n = lore.size();
		for(int i = 0; i < n; i++) {
			final ItemStack key = getKeyItem().copy();
			key.offer(Keys.ITEM_LORE, Arrays.asList(lore.get(i), lore.get(++i)));
			inventory.offer(key);
		}

		if(players != null) {
			for(final Player player : players) {
				player.openInventory(inventory);
			}
		}

		return inventory;
	}

	/**
	 * Triggered when a bunch of keys inventory is closed.
	 *
	 * @param unlocker The unlocker.
	 * @param inventory The inventory.
	 * @param bunchOfKeys The bunch of keys item.
	 * @param player The involved player.
	 */

	private void onBunchOfKeysInventoryClose(final LoreUnlocker<ItemStack> unlocker, final Inventory inventory, final ItemStack bunchOfKeys, final Player player) {
		if(bunchOfKeys.getQuantity() > 1) {
			final ItemStack clone = bunchOfKeys.copy();
			clone.setQuantity(bunchOfKeys.getQuantity() - 1);
			Util.dropItemAt(clone, player.getLocation());
			bunchOfKeys.setQuantity(1);
		}

		unlocker.clearLocations(bunchOfKeys);

		while(inventory.size() > 0) {
			inventory.poll().ifPresent(
					item -> {
						if(!isUsedKey(item)) {
							Util.dropItemAt(item, player.getLocation());
							return;
						}

						unlocker.addLocation(bunchOfKeys, item);
						if(item.getQuantity() > 1) {
							final ItemStack clone = item.copy();
							clone.setQuantity(item.getQuantity() - 1);
							Util.dropItemAt(clone, player.getLocation());
						}
					}
			);
		}
	}

}
