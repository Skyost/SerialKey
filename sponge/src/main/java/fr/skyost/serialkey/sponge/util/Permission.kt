package fr.skyost.serialkey.sponge.util;

import org.spongepowered.api.text.Text;

/**
 * Represents a simple permission.
 */

public class Permission {

	/**
	 * The permission.
	 */

	private final String permission;

	/**
	 * The role.
	 */

	private final String role;

	/**
	 * The description.
	 */

	private final Text description;

	/**
	 * Creates a new permission instance.
	 *
	 * @param permission The permission.
	 * @param role The role.
	 * @param description The description.
	 */

	public Permission(final String permission, final String role, final String description) {
		this.permission = permission;
		this.role = role;
		this.description = Util.parseString(description);
	}

	/**
	 * Returns the permission.
	 *
	 * @return The permission.
	 */

	public String getPermission() {
		return permission;
	}

	/**
	 * Returns the role.
	 *
	 * @return The role.
	 */

	public String getRole() {
		return role;
	}

	/**
	 * Returns the description.
	 *
	 * @return The description.
	 */

	public Text getDescription() {
		return description;
	}

}