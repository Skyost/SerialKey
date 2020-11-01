package fr.skyost.serialkey.core.`object`

import java.util.*

/**
 * Represents a point in a 3D-Space located by a world and three coordinates.
 */
class SerialKeyLocation {
    /**
     * The world.
     */
    val world: String?

    /**
     * X coordinate.
     */
    var x: Int

    /**
     * Y coordinate.
     */
    var y: Int

    /**
     * Z coordinate.
     */
    var z: Int

    /**
     * Creates a new SerialKey location.
     *
     * @param world The world.
     * @param position The String position (<q>x, y, z</q>).
     */
    constructor(world: String, position: String) {
        this.world = world
        val rawLocation = position.split(", ").toTypedArray()
        x = rawLocation[0].toInt()
        y = rawLocation[1].toInt()
        z = rawLocation[2].toInt()
    }

    /**
     * Creates a new SerialKey location.
     *
     * @param world The world.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     */
    constructor(world: String?, x: Int, y: Int, z: Int) {
        this.world = world
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Returns the position (<q>x, y, z</q>).
     *
     * @return The position.
     */
    val position: String
        get() = "$x, $y, $z"

    /**
     * Returns a location where its coordinates are (<pre>this.x + location.x</pre>, <pre>this.y + location.y</pre>, <pre>this.z + location.z</pre>)
     * where (<pre>this.x</pre>, <pre>this.y</pre>, <pre>this.z</pre>) are the coordinates of this location
     * and (<pre>location.x</pre>, <pre>location.y</pre>, <pre>location.z</pre>) are the coordinates of the specified location.
     *
     * @param location The location.
     *
     * @return The calculated location.
     */
    fun getRelative(location: SerialKeyLocation): SerialKeyLocation {
        return getRelative(location.x, location.y, location.z)
    }

    /**
     * Returns a location where its coordinates are (<pre>this.x + x</pre>, <pre>this.y + y</pre>, <pre>this.z + z</pre>)
     * where (<pre>this.x</pre>, <pre>this.y</pre>, <pre>this.z</pre>) are the coordinates of this location
     * and (<pre>x</pre>, <pre>y</pre>, <pre>z</pre>) are the specified coordinates.
     *
     * @param x The X.
     * @param y The Y.
     * @param z The Z.
     *
     * @return The calculated location.
     */
    fun getRelative(x: Int, y: Int, z: Int): SerialKeyLocation {
        return SerialKeyLocation(world, this.x + x, this.y + y, this.z + z)
    }

    /**
     * Creates a copy of the current location.
     *
     * @return The copy.
     */
    fun copy(): SerialKeyLocation {
        return SerialKeyLocation(world, x, y, z)
    }

    override fun toString(): String {
        return "{\"" + world?.replace("\"", "\\\"") + "\", " + position + "}"
    }

    override fun hashCode(): Int {
        return Objects.hash(world, x, y, z)
    }

    fun equals(location: SerialKeyLocation): Boolean {
        return world == location.world && x == location.x && y == location.y && z == location.z
    }

    override fun equals(other: Any?): Boolean {
        return if (other is SerialKeyLocation) {
            equals(other)
        } else super.equals(other)
    }
}