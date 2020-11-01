package fr.skyost.serialkey.bukkit.util;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.google.common.base.Joiner;
import com.google.common.primitives.Primitives;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <h1>Skyoconfig</h1>
 * <p><i>Handle configurations with ease !</i></p>
 * <p><b>Current version :</b> v0.9.
 *
 * @author <b>Skyost</b> (<a href="http://www.skyost.eu">www.skyost.eu</a>).
 * <br>Inspired from <a href="https://forums.bukkit.org/threads/lib-supereasyconfig-v1-2-based-off-of-codename_bs-awesome-easyconfig-v2-1.100569/">SuperEasyConfig</a>.</br>
 */

public class Skyoconfig {

	private static final transient char DEFAULT_SEPARATOR = '_';
	private static final transient String LINE_SEPARATOR = System.lineSeparator();
	private static final transient String TEMP_CONFIG_SECTION = "temp";

	private transient File configFile;
	private transient List<String> header;

	/**
	 * Creates a new instance of Skyoconfig without header.
	 *
	 * @param configFile The file where the configuration will be loaded an saved.
	 */

	protected Skyoconfig(final File configFile) {
		this(configFile, null);
	}

	/**
	 * Creates a new instance of Skyoconfig.
	 *
	 * @param configFile The file where the configuration will be loaded an saved.
	 * @param header The configuration's header.
	 */

	protected Skyoconfig(final File configFile, final List<String> header) {
		this.configFile = configFile;
		this.header = header;
	}

	/**
	 * Loads the configuration from the specified file.
	 *
	 * @throws InvalidConfigurationException If there is an error while loading the config.
	 */

	public final void load() throws InvalidConfigurationException {
		try {
			final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			Class<?> clazz = getClass();
			while(clazz != Skyoconfig.class) {
				for(final Field field : clazz.getFields()) {
					loadField(field, getFieldName(field), config);
				}
				clazz = clazz.getSuperclass();
			}
			saveConfig(config);
		}
		catch(final Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
	}

	/**
	 * Saves the configuration to the specified file.
	 *
	 * @throws InvalidConfigurationException If there is an error while saving the config.
	 */

	public final void save() throws InvalidConfigurationException {
		try {
			final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			Class<?> clazz = getClass();
			while(clazz != Skyoconfig.class) {
				for(final Field field : clazz.getFields()) {
					saveField(field, getFieldName(field), config);
				}
				clazz = clazz.getSuperclass();
			}
			saveConfig(config);
		}
		catch(final Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
	}

	/**
	 * Gets the formatted <b>Field</b>'s name.
	 *
	 * @param field The <b>Field</b>.
	 *
	 * @return The formatted <b>Field</b>'s name.
	 */

	private String getFieldName(final Field field) {
		final ConfigOptions options = field.getAnnotation(ConfigOptions.class);
		if(options == null) {
			return field.getName().replace(DEFAULT_SEPARATOR, '.');
		}
		final String name = options.name();
		if(name.equals("")) {
			return field.getName().replace(DEFAULT_SEPARATOR, '.');
		}
		return name;
	}

	/**
	 * Checks if a field should be ignored.
	 *
	 * @param field The <b>Field</b>.
	 *
	 * @return <b>true</b> Yes.
	 * <br><b>false</b> Otherwise.
	 */

	private boolean ignoreField(final Field field) {
		final ConfigOptions options = field.getAnnotation(ConfigOptions.class);
		return options != null && options.ignore();
	}

	/**
	 * Saves the configuration.
	 *
	 * @param config The <b>YamlConfiguration</b>.
	 *
	 * @throws IOException <b>InputOutputException</b>.
	 */

	private void saveConfig(final YamlConfiguration config) throws IOException {
		if(header != null && header.size() > 0) {
			config.options().header(Joiner.on(LINE_SEPARATOR).join(header));
		}
		config.save(configFile);
	}

	/**
	 * Loads a Field from its path from the config.
	 *
	 * @param field The specified <b>Field</b>.
	 * @param name The <b>Field</b>'s name. Will be the path.
	 * @param config The <b>YamlConfiguration</b>.
	 *
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b> or the <b>Method</b> <b>valueOf</b> of a <b>Primitive</b>.
	 * @throws InvocationTargetException Invoked if the <b>Skyoconfig</b> fails to use <b>valueOf</b> for a <b>Primitive</b>.
	 * @throws NoSuchMethodException Same as <b>InvocationTargetException</b>.
	 * @throws InstantiationException When a <b>Map</b> cannot be created.
	 */

	private void loadField(final Field field, final String name, final YamlConfiguration config) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		if(Modifier.isTransient(field.getModifiers()) || ignoreField(field)) {
			return;
		}
		final Object configValue = config.get(getFieldName(field));
		if(configValue == null) {
			saveField(field, name, config);
		}
		else {
			field.set(this, deserializeObject(field.getType(), configValue));
		}
	}

	/**
	 * Saves a <b>Field</b> to the config.
	 *
	 * @param field The specified <b>Field</b>.
	 * @param name The <b>Field</b>'s name. The path of the value in the config.
	 * @param config The <b>YamlConfiguration</b>.
	 *
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b>.
	 */

	private void saveField(final Field field, final String name, final YamlConfiguration config) throws IllegalAccessException {
		if(Modifier.isTransient(field.getModifiers()) || ignoreField(field)) {
			return;
		}
		config.set(name, serializeObject(field.get(this), config));
	}

	/**
	 * Deserializes an <b>Object</b> from the configuration.
	 *
	 * @param clazz The object's <b>Type</b>.
	 * @param object The <b>Object</b>'s.
	 *
	 * @return The deserialized value of the specified <b>Object</b>.
	 *
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b> or the <b>Method</b> <b>valueOf</b> of a <b>Primitive</b>.
	 * @throws InvocationTargetException Invoked if the <b>Skyoconfig</b> fails to use <b>valueOf</b> for a <b>Primitive</b>.
	 * @throws NoSuchMethodException Same as <b>InvocationTargetException</b>.
	 * @throws InstantiationException When a <b>Map</b> cannot be created.
	 */

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Object deserializeObject(final Class<?> clazz, final Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		if(clazz.isPrimitive()) {
			return Primitives.wrap(clazz).getMethod("valueOf", String.class).invoke(this, object.toString());
		}
		if(Primitives.isWrapperType(clazz)) {
			return clazz.getMethod("valueOf", String.class).invoke(this, object.toString());
		}
		if(clazz.isEnum() || object instanceof Enum<?>) {
			return Enum.valueOf((Class<? extends Enum>)clazz, object.toString());
		}
		if(Map.class.isAssignableFrom(clazz) || object instanceof Map) {
			final ConfigurationSection section = (ConfigurationSection)object;
			final Map<Object, Object> unserializedMap = new HashMap<>();
			for(final String key : section.getKeys(false)) {
				final Object value = section.get(key);
				unserializedMap.put(key, deserializeObject(value.getClass(), value));
			}
			final Object map = clazz.newInstance();
			clazz.getMethod("putAll", Map.class).invoke(map, unserializedMap);
			return map;
		}
		if(List.class.isAssignableFrom(clazz) || object instanceof List) {
			final List<Object> result = new ArrayList<>();
			for(final Object value : (List<?>)object) {
				result.add(deserializeObject(value.getClass(), value));
			}
			return result;
		}
		if(Location.class.isAssignableFrom(clazz) || object instanceof Location) {
			final JsonObject jsonObject = Json.parse(object.toString()).asObject();
			return new Location(Bukkit.getWorld(jsonObject.get("world").asString()), Double.parseDouble(jsonObject.get("x").asString()), Double.parseDouble(jsonObject.get("y").asString()), Double.parseDouble(jsonObject.get("z").asString()), Float.parseFloat(jsonObject.get("yaw").asString()), Float.parseFloat(jsonObject.get("pitch").asString()));
		}
		if(Vector.class.isAssignableFrom(clazz) || object instanceof Vector) {
			final JsonObject jsonObject = Json.parse(object.toString()).asObject();
			return new Vector(Double.parseDouble(jsonObject.get("x").asString()), Double.parseDouble(jsonObject.get("y").asString()), Double.parseDouble(jsonObject.get("z").asString()));
		}
		return ChatColor.translateAlternateColorCodes('&', object.toString());
	}

	/**
	 * Serializes an <b>Object</b> to the configuration.
	 *
	 * @param object The specified <b>Object</b>.
	 * @param config The <b>YamlConfiguration</b>. Used to temporally save <b>Map</b>s.
	 *
	 * @return The serialized <b>Object</b>.
	 */

	@SuppressWarnings("unchecked")
	private Object serializeObject(final Object object, final YamlConfiguration config) {
		if(object instanceof String) {
			return object.toString().replace(ChatColor.COLOR_CHAR, '&');
		}
		if(object instanceof Enum) {
			return ((Enum<?>)object).name();
		}
		if(object instanceof Map) {
			final ConfigurationSection section = config.createSection(TEMP_CONFIG_SECTION);
			for(final Entry<?, ?> entry : ((Map<?, ?>)object).entrySet()) {
				section.set(entry.getKey().toString(), serializeObject(entry.getValue(), config));
			}
			config.set(TEMP_CONFIG_SECTION, null);
			return section;
		}
		if(object instanceof List) {
			final List<Object> result = new ArrayList<>();
			for(final Object value : (List<?>)object) {
				result.add(serializeObject(value, config));
			}
			return result;
		}
		if(object instanceof Location) {
			final Location location = (Location)object;
			final JsonObject jsonObject = new JsonObject();
			jsonObject.add("world", location.getWorld().getName());
			jsonObject.add("x", location.getX());
			jsonObject.add("y", location.getY());
			jsonObject.add("z", location.getZ());
			jsonObject.add("yaw", location.getYaw());
			jsonObject.add("pitch", location.getPitch());
			return jsonObject.toString();
		}
		if(object instanceof Vector) {
			final Vector vector = (Vector)object;
			final JsonObject jsonObject = new JsonObject();
			jsonObject.add("x", vector.getX());
			jsonObject.add("y", vector.getY());
			jsonObject.add("z", vector.getZ());
			return jsonObject.toString();
		}
		return object;
	}

	/**
	 * Gets the configuration's header.
	 *
	 * @return The header.
	 */

	public final List<String> getHeader() {
		return header;
	}

	/**
	 * Gets the configuration's <b>File</b>.
	 *
	 * @return The <b>File</b>.
	 */

	public final File getFile() {
		return configFile;
	}

	/**
	 * Sets the configuration's header.
	 *
	 * @param header The header.
	 */

	public final void setHeader(final List<String> header) {
		this.header = header;
	}

	/**
	 * Sets the configuration's <b>File</b>.
	 *
	 * @param configFile The <b>File</b>.
	 */

	public final void setFile(final File configFile) {
		this.configFile = configFile;
	}

	/**
	 * Extra params for configuration fields.
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

		/**
		 * If Skyoconfig should ignore this field.
		 *
		 * @return <b>true</b> Yes.
		 * <br><b>false</b> Otherwise.
		 */

		boolean ignore() default false;

	}

}