package fr.skyost.serialkey.sponge.config;

import com.google.common.base.Joiner;
import com.google.common.primitives.Primitives;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A useful class that allows to serialize (and deserialize) its inheriting classes to a Sponge configuration.
 */

public class SpongeConfig {

	/**
	 * The node separator.
	 */

	private static final String SEPARATOR = ".";

	/**
	 * The file.
	 */

	private final File file;

	/**
	 * The comments.
	 */

	private final String[] comments;

	/**
	 * The configuration loader.
	 */

	private final HoconConfigurationLoader loader;

	/**
	 * Creates a new Sponge config instance.
	 *
	 * @param file The file.
	 * @param comments The comments.
	 */

	SpongeConfig(final Path file, final String... comments) {
		this.file = file.toFile();
		this.comments = comments;
		loader = HoconConfigurationLoader.builder().setPath(file).build();
	}

	/**
	 * Loads the fields from the config file.
	 *
	 * @throws InvalidConfigurationException If any error occurs.
	 */

	public void load() throws InvalidConfigurationException {
		try {
			if(file.exists()) {
				final CommentedConfigurationNode node = loader.load();
				node.setComment(Joiner.on(System.lineSeparator()).join(comments));

				final Field[] fields = getClass().getDeclaredFields();
				for(final Field field : fields) {
					field.setAccessible(true);
					field.set(this, deserializeField(field, field.get(this), node));
				}
			}
			save();
		}
		catch(final Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
	}

	/**
	 * Saves the fields to the config file.
	 *
	 * @throws InvalidConfigurationException If any error occurs.
	 */

	public void save() throws InvalidConfigurationException {
		try {
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			final ConfigurationNode node = loader.load();
			final Field[] fields = getClass().getDeclaredFields();
			for(final Field field : fields) {
				field.setAccessible(true);
				getFieldNode(field, node).setValue(serializeField(field.get(this)));
			}
			loader.save(node);
		}
		catch(final Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
	}

	/**
	 * Deserializes a field.
	 *
	 * @param field The field.
	 * @param defaultValue The default value.
	 * @param node The target node.
	 *
	 * @return The value.
	 *
	 * @throws IllegalAccessException If field access is not permitted.
	 * @throws InstantiationException If an object cannot be instantiated.
	 * @throws NoSuchMethodException If a specified method doesn't exist.
	 * @throws InvocationTargetException If a method cannot be invoked for the specified arguments.
	 */

	private Object deserializeField(final Field field, final Object defaultValue, final ConfigurationNode node) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		final ConfigurationNode fieldNode = getFieldNode(field, node);

		if(defaultValue instanceof Map) {
			final Map result = (Map)defaultValue.getClass().newInstance();
			final Map<?, ? extends ConfigurationNode> childrenMap = fieldNode.getChildrenMap();
			for(final Map.Entry<?, ? extends ConfigurationNode> child : childrenMap.entrySet()) {
				result.put(child.getKey(), child.getValue().getValue());
			}

			return result;
		}

		if(defaultValue instanceof List) {
			final List result = (List)defaultValue.getClass().newInstance();
			result.addAll(fieldNode.getList(object -> object, (List)defaultValue));
			return result;
		}

		final Class<?> clazz = defaultValue.getClass();
		if(clazz.isPrimitive()) {
			return Primitives.wrap(clazz).getMethod("valueOf", String.class).invoke(this, fieldNode.getValue(defaultValue).toString());
		}

		if(Primitives.isWrapperType(clazz)) {
			return clazz.getMethod("valueOf", String.class).invoke(this, fieldNode.getValue(defaultValue).toString());
		}

		if(defaultValue instanceof ItemType) {
			return Sponge.getRegistry().getType(ItemType.class, fieldNode.getString(((ItemType)defaultValue).getId())).orElse(null);
		}

		if(clazz.isEnum()) {
			return Enum.valueOf((Class<? extends Enum>)clazz, fieldNode.getValue(defaultValue).toString());
		}

		if(defaultValue instanceof String) {
			return fieldNode.getString((String)defaultValue);
		}

		return defaultValue;
	}

	/**
	 * Serializes a field.
	 *
	 * @param value The field value.
	 *
	 * @return The serialized value.
	 */

	private Object serializeField(final Object value) {
		if(value instanceof Map || value instanceof List || value instanceof String) {
			return value;
		}

		if(value instanceof ItemType) {
			return ((ItemType)value).getId();
		}

		final Class<?> clazz = value.getClass();
		if(clazz.isPrimitive() || Primitives.isWrapperType(clazz)) {
			return value;
		}

		if(clazz.isEnum()) {
			return ((Enum<?>)value).name();
		}

		return value;
	}

	/**
	 * Returns the config node that corresponds to the specified field.
	 *
	 * @param field The field.
	 * @param parent The node parent.
	 *
	 * @return The config node that corresponds to the specified field.
	 */

	private ConfigurationNode getFieldNode(final Field field, final ConfigurationNode parent) {
		ConfigurationNode fieldNode = parent;
		final String[] parts = getFieldName(field).split(Pattern.quote(SEPARATOR));
		for(final String part : parts) {
			fieldNode = fieldNode.getNode(part);
		}

		return fieldNode;
	}

	/**
	 * Returns the string identifier that corresponds to the specified field.
	 *
	 * @param field The field.
	 *
	 * @return The string identifier that corresponds to the specified field.
	 */

	private String getFieldName(final Field field) {
		final ConfigOptions options = field.getAnnotation(ConfigOptions.class);
		if(options == null || options.name().isEmpty()) {
			return field.getName();
		}
		return options.name();
	}

	/**
	 * Represents some config options that can be applied to a field.
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	protected @interface ConfigOptions {

		/**
		 * The key's name.
		 *
		 * @return The key's name.
		 */

		String name() default "";

	}

	/**
	 * Represents an invalid configuration exception.
	 */

	public class InvalidConfigurationException extends Exception {

		/**
		 * Creates a new invalid configuration exception instance.
		 *
		 * @param message The message.
		 */

		private InvalidConfigurationException(final String message) {
			super(message);
		}

		/**
		 * Creates a new invalid configuration exception instance.
		 *
		 * @param throwable The throwable child.
		 */

		private InvalidConfigurationException(final Throwable throwable) {
			super(throwable);
		}

	}

}