package fr.skyost.serialkey.sponge.config

import com.google.common.base.Joiner
import com.google.common.primitives.Primitives
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.api.Sponge
import org.spongepowered.api.item.ItemType
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.nio.file.Path
import java.util.function.Function
import java.util.regex.Pattern

/**
 * A useful class that allows to serialize (and deserialize) its inheriting classes to a Sponge configuration.
 */
open class SpongeConfig internal constructor(file: Path, private val comments: Collection<String>) {
    /**
     * The file.
     */
    private val file: File = file.toFile()

    /**
     * The configuration loader.
     */
    private val loader: HoconConfigurationLoader = HoconConfigurationLoader.builder().setPath(file).build()

    /**
     * Loads the fields from the config file.
     *
     * @throws InvalidConfigurationException If any error occurs.
     */
    @Throws(InvalidConfigurationException::class)
    fun load() {
        try {
            if (file.exists()) {
                val node = loader.load()
                node.setComment(Joiner.on(System.lineSeparator()).join(comments))
                val fields = javaClass.declaredFields
                for (field in fields) {
                    field.isAccessible = true
                    field[this] = deserializeField(field, field[this], node)
                }
            }
            save()
        } catch (ex: Exception) {
            throw InvalidConfigurationException(ex)
        }
    }

    /**
     * Saves the fields to the config file.
     *
     * @throws InvalidConfigurationException If any error occurs.
     */
    @Throws(InvalidConfigurationException::class)
    fun save() {
        try {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            val node: ConfigurationNode = loader.load()
            val fields = javaClass.declaredFields
            for (field in fields) {
                field.isAccessible = true
                getFieldNode(field, node).value = serializeField(field[this])
            }
            loader.save(node)
        } catch (ex: Exception) {
            throw InvalidConfigurationException(ex)
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
    @Throws(IllegalAccessException::class, InstantiationException::class, NoSuchMethodException::class, InvocationTargetException::class)
    private fun deserializeField(field: Field, defaultValue: Any, node: ConfigurationNode): Any? {
        val fieldNode = getFieldNode(field, node)
        if (defaultValue is Map<*, *>) {
            val result = defaultValue.javaClass.newInstance() as MutableMap<Any?, Any?>
            val childrenMap: Map<*, ConfigurationNode> = fieldNode.childrenMap
            for ((key, value) in childrenMap) {
                result[key] = value.value
            }
            return result
        }
        if (defaultValue is List<*>) {
            val result = defaultValue.javaClass.newInstance() as MutableList<Any?>
            result.addAll(fieldNode.getList(Function { `object`: Any? -> `object` }, defaultValue))
            return result
        }
        val clazz: Class<*> = defaultValue.javaClass
        if (clazz.isPrimitive) {
            return Primitives.wrap(clazz).getMethod("valueOf", String::class.java).invoke(this, fieldNode.getValue(defaultValue).toString())
        }
        if (Primitives.isWrapperType(clazz)) {
            return clazz.getMethod("valueOf", String::class.java).invoke(this, fieldNode.getValue(defaultValue).toString())
        }
        if (defaultValue is ItemType) {
            return Sponge.getRegistry().getType(ItemType::class.java, fieldNode.getString(defaultValue.id)).orElse(null)
        }
        if (clazz.isEnum) {
            return java.lang.Enum.valueOf(clazz as Class<out Enum<*>?>, fieldNode.getValue(defaultValue).toString())
        }
        return if (defaultValue is String) {
            fieldNode.getString(defaultValue)
        } else defaultValue
    }

    /**
     * Serializes a field.
     *
     * @param value The field value.
     *
     * @return The serialized value.
     */
    private fun serializeField(value: Any): Any {
        if (value is Map<*, *> || value is List<*> || value is String) {
            return value
        }
        if (value is ItemType) {
            return value.id
        }
        val clazz: Class<*> = value.javaClass
        if (clazz.isPrimitive || Primitives.isWrapperType(clazz)) {
            return value
        }
        return if (clazz.isEnum) {
            (value as Enum<*>).name
        } else value
    }

    /**
     * Returns the config node that corresponds to the specified field.
     *
     * @param field The field.
     * @param parent The node parent.
     *
     * @return The config node that corresponds to the specified field.
     */
    private fun getFieldNode(field: Field, parent: ConfigurationNode): ConfigurationNode {
        var fieldNode = parent
        val parts = getFieldName(field).split(Pattern.quote(SEPARATOR)).toTypedArray()
        for (part in parts) {
            fieldNode = fieldNode.getNode(part)
        }
        return fieldNode
    }

    /**
     * Returns the string identifier that corresponds to the specified field.
     *
     * @param field The field.
     *
     * @return The string identifier that corresponds to the specified field.
     */
    private fun getFieldName(field: Field): String {
        val options = field.getAnnotation(ConfigOptions::class.java)
        return if (options == null || options.name.isEmpty()) {
            field.name
        } else options.name
    }

    /**
     * Represents some config options that can be applied to a field.
     */
    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FIELD)
    protected annotation class ConfigOptions(
            /**
             * The key's name.
             *
             * @return The key's name.
             */
            val name: String = ""
    )

    /**
     * Represents an invalid configuration exception.
     */
    inner class InvalidConfigurationException(throwable: Throwable) : Exception(throwable)

    companion object {
        /**
         * The node separator.
         */
        private const val SEPARATOR = "."
    }

}