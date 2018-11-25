package fr.skyost.serialkey.core.object;

/**
 * Represents a SerialKey person identity.
 */

public class PersonIdentity {

	/**
	 * Identity types.
	 */

	public enum Type {

		/**
		 * Console type.
		 */

		CONSOLE,

		/**
		 * Player type.
		 */

		PLAYER

	}

	/**
	 * The type of the person.
	 */

	private final Type type;

	/**
	 * The name of the person.
	 */

	private final String name;

	/**
	 * Creates a new person identity instance.
	 *
	 * @param type The type of the person.
	 * @param name The name of the person.
	 */

	public PersonIdentity(final Type type, final String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Returns the type of the person.
	 *
	 * @return The type of the person.
	 */

	public Type getType() {
		return type;
	}

	/**
	 * Returns the name of the person.
	 *
	 * @return The name of the person.
	 */

	public String getName() {
		return name;
	}

}