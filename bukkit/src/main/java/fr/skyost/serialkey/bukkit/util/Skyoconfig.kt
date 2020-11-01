package fr.skyost.serialkey.bukkit.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import com.google.common.base.Joiner
import com.google.common.primitives.Primitives
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.util.*

/**
 * <h1>Skyoconfig</h1>
 *
 * *Handle configurations with ease !*
 *
 * **Current version :** v0.9.
 *
 * @author **Skyost** ([www.skyost.eu](http://www.skyost.eu)).
 * <br></br>Inspired from [SuperEasyConfig](https://forums.bukkit.org/threads/lib-supereasyconfig-v1-2-based-off-of-codename_bs-awesome-easyconfig-v2-1.100569/).
 */
open class Skyoconfig protected constructor(@field:Transient var file: File, @field:Transient var header: List<String?>? = null) {
    /**
     * Gets the configuration's **File**.
     *
     * @return The **File**.
     */
    /**
     * Gets the configuration's header.
     *
     * @return The header.
     */

    /**
     * Loads the configuration from the specified file.
     *
     * @throws InvalidConfigurationException If there is an error while loading the config.
     */
    @Throws(InvalidConfigurationException::class)
    fun load() {
        try {
            val config = YamlConfiguration.loadConfiguration(file)
            var clazz: Class<*> = javaClass
            while (clazz != Skyoconfig::class.java) {
                for (field in clazz.fields) {
                    loadField(field, getFieldName(field), config)
                }
                clazz = clazz.superclass
            }
            saveConfig(config)
        } catch (ex: Exception) {
            throw InvalidConfigurationException(ex)
        }
    }

    /**
     * Saves the configuration to the specified file.
     *
     * @throws InvalidConfigurationException If there is an error while saving the config.
     */
    @Throws(InvalidConfigurationException::class)
    fun save() {
        try {
            val config = YamlConfiguration.loadConfiguration(file)
            var clazz: Class<*> = javaClass
            while (clazz != Skyoconfig::class.java) {
                for (field in clazz.fields) {
                    saveField(field, getFieldName(field), config)
                }
                clazz = clazz.superclass
            }
            saveConfig(config)
        } catch (ex: Exception) {
            throw InvalidConfigurationException(ex)
        }
    }

    /**
     * Gets the formatted **Field**'s name.
     *
     * @param field The **Field**.
     *
     * @return The formatted **Field**'s name.
     */
    private fun getFieldName(field: Field): String {
        val options = field.getAnnotation(ConfigOptions::class.java)
                ?: return field.name.replace(DEFAULT_SEPARATOR, '.')
        val name: String = options.name
        return if (name == "") {
            field.name.replace(DEFAULT_SEPARATOR, '.')
        } else name
    }

    /**
     * Checks if a field should be ignored.
     *
     * @param field The **Field**.
     *
     * @return **true** Yes.
     * <br></br>**false** Otherwise.
     */
    private fun ignoreField(field: Field): Boolean {
        val options = field.getAnnotation(ConfigOptions::class.java)
        return options != null && options.ignore
    }

    /**
     * Saves the configuration.
     *
     * @param config The **YamlConfiguration**.
     *
     * @throws IOException **InputOutputException**.
     */
    @Throws(IOException::class)
    private fun saveConfig(config: YamlConfiguration) {
        if (header != null && header!!.isNotEmpty()) {
            config.options().header(Joiner.on(LINE_SEPARATOR).join(header!!))
        }
        config.save(file)
    }

    /**
     * Loads a Field from its path from the config.
     *
     * @param field The specified **Field**.
     * @param name The **Field**'s name. Will be the path.
     * @param config The **YamlConfiguration**.
     *
     * @throws IllegalAccessException If **Skyoconfig** does not have access to the **Field** or the **Method** **valueOf** of a **Primitive**.
     * @throws InvocationTargetException Invoked if the **Skyoconfig** fails to use **valueOf** for a **Primitive**.
     * @throws NoSuchMethodException Same as **InvocationTargetException**.
     * @throws InstantiationException When a **Map** cannot be created.
     */
    @Throws(IllegalAccessException::class, InvocationTargetException::class, NoSuchMethodException::class, InstantiationException::class)
    private fun loadField(field: Field, name: String, config: YamlConfiguration) {
        if (Modifier.isTransient(field.modifiers) || ignoreField(field)) {
            return
        }
        val configValue = config[getFieldName(field)]
        if (configValue == null) {
            saveField(field, name, config)
        } else {
            field[this] = deserializeObject(field.type, configValue)
        }
    }

    /**
     * Saves a **Field** to the config.
     *
     * @param field The specified **Field**.
     * @param name The **Field**'s name. The path of the value in the config.
     * @param config The **YamlConfiguration**.
     *
     * @throws IllegalAccessException If **Skyoconfig** does not have access to the **Field**.
     */
    @Throws(IllegalAccessException::class)
    private fun saveField(field: Field, name: String, config: YamlConfiguration) {
        if (Modifier.isTransient(field.modifiers) || ignoreField(field)) {
            return
        }
        config[name] = serializeObject(field[this], config)
    }

    /**
     * Deserializes an **Object** from the configuration.
     *
     * @param clazz The object's **Type**.
     * @param object The **Object**'s.
     *
     * @return The deserialized value of the specified **Object**.
     *
     * @throws IllegalAccessException If **Skyoconfig** does not have access to the **Field** or the **Method** **valueOf** of a **Primitive**.
     * @throws InvocationTargetException Invoked if the **Skyoconfig** fails to use **valueOf** for a **Primitive**.
     * @throws NoSuchMethodException Same as **InvocationTargetException**.
     * @throws InstantiationException When a **Map** cannot be created.
     */
    @Throws(IllegalAccessException::class, InvocationTargetException::class, NoSuchMethodException::class, InstantiationException::class)
    private fun deserializeObject(clazz: Class<*>, `object`: Any?): Any {
        if (clazz.isPrimitive) {
            return Primitives.wrap(clazz).getMethod("valueOf", String::class.java).invoke(this, `object`.toString())
        }
        if (Primitives.isWrapperType(clazz)) {
            return clazz.getMethod("valueOf", String::class.java).invoke(this, `object`.toString())
        }
        if (clazz.isEnum || `object` is Enum<*>) {
            return java.lang.Enum.valueOf(clazz as Class<out Enum<*>>, `object`.toString())!!
        }
        if (MutableMap::class.java.isAssignableFrom(clazz) || `object` is Map<*, *>) {
            val section = `object` as ConfigurationSection?
            val unserializedMap: MutableMap<Any, Any> = HashMap()
            for (key in section!!.getKeys(false)) {
                val value = section[key]
                unserializedMap[key] = deserializeObject(value!!.javaClass, value)
            }
            val map = clazz.newInstance()
            clazz.getMethod("putAll", MutableMap::class.java).invoke(map, unserializedMap)
            return map
        }
        if (MutableList::class.java.isAssignableFrom(clazz) || `object` is List<*>) {
            val result: MutableList<Any> = ArrayList()
            for (value in (`object` as List<*>?)!!) {
                result.add(deserializeObject(value!!.javaClass, value))
            }
            return result
        }
        if (Location::class.java.isAssignableFrom(clazz) || `object` is Location) {
            val jsonObject = Json.parse(`object`.toString()).asObject()
            return Location(Bukkit.getWorld(jsonObject["world"].asString()), jsonObject["x"].asString().toDouble(), jsonObject["y"].asString().toDouble(), jsonObject["z"].asString().toDouble(), jsonObject["yaw"].asString().toFloat(), jsonObject["pitch"].asString().toFloat())
        }
        if (Vector::class.java.isAssignableFrom(clazz) || `object` is Vector) {
            val jsonObject = Json.parse(`object`.toString()).asObject()
            return Vector(jsonObject["x"].asString().toDouble(), jsonObject["y"].asString().toDouble(), jsonObject["z"].asString().toDouble())
        }
        return ChatColor.translateAlternateColorCodes('&', `object`.toString())
    }

    /**
     * Serializes an **Object** to the configuration.
     *
     * @param object The specified **Object**.
     * @param config The **YamlConfiguration**. Used to temporally save **Map**s.
     *
     * @return The serialized **Object**.
     */
    private fun serializeObject(`object`: Any?, config: YamlConfiguration): Any {
        if(`object` == null) {
            return "null"
        }
        if (`object` is String) {
            return `object`.toString().replace(ChatColor.COLOR_CHAR, '&')
        }
        if (`object` is Enum<*>) {
            return `object`.name
        }
        if (`object` is Map<*, *>) {
            val section = config.createSection(TEMP_CONFIG_SECTION)
            for ((key, value) in `object`) {
                section[key.toString()] = serializeObject(value!!, config)
            }
            config[TEMP_CONFIG_SECTION] = null
            return section
        }
        if (`object` is List<*>) {
            val result: MutableList<Any> = ArrayList()
            for (value in `object`) {
                result.add(serializeObject(value, config))
            }
            return result
        }
        if (`object` is Location) {
            val jsonObject = JsonObject()
            jsonObject.add("world", `object`.world!!.name)
            jsonObject.add("x", `object`.x)
            jsonObject.add("y", `object`.y)
            jsonObject.add("z", `object`.z)
            jsonObject.add("yaw", `object`.yaw)
            jsonObject.add("pitch", `object`.pitch)
            return jsonObject.toString()
        }
        if (`object` is Vector) {
            val jsonObject = JsonObject()
            jsonObject.add("x", `object`.x)
            jsonObject.add("y", `object`.y)
            jsonObject.add("z", `object`.z)
            return jsonObject.toString()
        }
        return `object`
    }

    /**
     * Extra params for configuration fields.
     */
    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FIELD)
    protected annotation class ConfigOptions(
            /**
             * The key's name.
             *
             * @return The key's name.
             */
            val name: String = "",
            /**
             * If Skyoconfig should ignore this field.
             *
             * @return **true** Yes.
             * <br></br>**false** Otherwise.
             */
            val ignore: Boolean = false
    )

    companion object {
        @Transient
        private val DEFAULT_SEPARATOR = '_'

        @Transient
        private val LINE_SEPARATOR = System.lineSeparator()

        @Transient
        private val TEMP_CONFIG_SECTION = "temp"
    }
}