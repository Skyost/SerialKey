package fr.skyost.serialkey.core.object;

import java.util.Objects;

/**
 * Represents a point in a 3D-Space located by a world and three coordinates.
 */

public class SerialKeyLocation {

	/**
	 * The world.
	 */

	private String world;

	/**
	 * X coordinate.
	 */

	private int x;

	/**
	 * Y coordinate.
	 */

	private int y;

	/**
	 * Z coordinate.
	 */

	private int z;

	/**
	 * Creates a new SerialKey location.
	 *
	 * @param world The world.
	 * @param position The String position (<q>x, y, z</q>).
	 */

	public SerialKeyLocation(final String world, final String position) {
		this.world = world;

		final String[] rawLocation = position.split(", ");
		x = Integer.parseInt(rawLocation[0]);
		y = Integer.parseInt(rawLocation[1]);
		z = Integer.parseInt(rawLocation[2]);
	}

	/**
	 * Creates a new SerialKey location.
	 *
	 * @param world The world.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 */

	public SerialKeyLocation(final String world, final int x, final int y, final int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns the world name.
	 *
	 * @return The world name.
	 */

	public String getWorld() {
		return world;
	}

	/**
	 * Sets the world name.
	 *
	 * @param world The world name.
	 */

	public void setWorld(final String world) {
		this.world = world;
	}

	/**
	 * Returns the X coordinate.
	 *
	 * @return The X coordinate.
	 */

	public int getX() {
		return x;
	}

	/**
	 * Sets the X coordinate.
	 *
	 * @param x The X coordinate.
	 */

	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * Returns the Y coordinate.
	 *
	 * @return The Y coordinate.
	 */

	public int getY() {
		return y;
	}

	/**
	 * Sets the Y coordinate.
	 *
	 * @param y The Y coordinate.
	 */

	public void setY(final int y) {
		this.y = y;
	}

	/**
	 * Returns the Z coordinate.
	 *
	 * @return The Z coordinate.
	 */

	public int getZ() {
		return z;
	}

	/**
	 * Sets the Z coordinate.
	 *
	 * @param z The Z coordinate.
	 */

	public void setZ(final int z) {
		this.z = z;
	}

	/**
	 * Returns the position (<q>x, y, z</q>).
	 *
	 * @return The position.
	 */

	public String getPosition() {
		return x + ", " + y + ", " + z;
	}

	/**
	 * Returns a location where its coordinates are (<pre>this.x + location.x</pre>, <pre>this.y + location.y</pre>, <pre>this.z + location.z</pre>)
	 * where (<pre>this.x</pre>, <pre>this.y</pre>, <pre>this.z</pre>) are the coordinates of this location
	 * and (<pre>location.x</pre>, <pre>location.y</pre>, <pre>location.z</pre>) are the coordinates of the specified location.
	 *
	 * @param location The location.
	 *
	 * @return The calculated location.
	 */

	public SerialKeyLocation getRelative(final SerialKeyLocation location) {
		return getRelative(location.x, location.y, location.z);
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

	public SerialKeyLocation getRelative(final int x, final int y, final int z) {
		return new SerialKeyLocation(world, this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Creates a copy of the current location.
	 *
	 * @return The copy.
	 */

	public SerialKeyLocation copy() {
		return new SerialKeyLocation(world, x, y, z);
	}

	@Override
	public String toString() {
		return "{\"" + world.replace("\"", "\\\"") + "\", " + getPosition() + "}";
	}

	@Override
	public int hashCode() {
		return Objects.hash(world, x, y ,z);
	}

	public boolean equals(final SerialKeyLocation location) {
		return world.equals(location.world) && x == location.x && y == location.y && z == location.z;
	}

	@Override
	public boolean equals(final Object object) {
		if(object instanceof SerialKeyLocation) {
			return equals((SerialKeyLocation)object);
		}

		return super.equals(object);
	}

}