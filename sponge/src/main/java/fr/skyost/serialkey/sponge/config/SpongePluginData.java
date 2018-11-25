package fr.skyost.serialkey.sponge.config;

import fr.skyost.serialkey.core.config.SerialKeyData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The plugin data class.
 */

public class SpongePluginData extends SpongeConfig implements SerialKeyData {

	@ConfigOptions(name = "padlocks")
	public List<String> padlocks = new ArrayList<>();

	/**
	 * Creates a new plugin data instance.
	 *
	 * @param file The config file.
	 */

	public SpongePluginData(final Path file) {
		super(file, "SerialKey Data");
	}

	@Override
	public Collection<String> getData() {
		return padlocks;
	}

}