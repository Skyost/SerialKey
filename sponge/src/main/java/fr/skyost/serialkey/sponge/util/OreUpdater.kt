package fr.skyost.serialkey.sponge.util;

import com.google.gson.JsonParser;
import fr.skyost.serialkey.sponge.BuildConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * Ore update checker.
 */

public class OreUpdater extends Thread {

	/**
	 * Plugin ID.
	 */

	private static final String PLUGIN_ID = BuildConfig.PLUGIN_ID;

	/**
	 * The plugin's logger instance.
	 */

	private final Logger logger;

	/**
	 * Creates a new updater instance.
	 *
	 * @param logger The plugin's logger.
	 */

	public OreUpdater(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public final void run() {
		try {
			logger.info("Checking for updates...");

			// Let's get the plugin.
			final Optional<PluginContainer> optionalPluginContainer = Sponge.getPluginManager().getPlugin(PLUGIN_ID);
			if(!optionalPluginContainer.isPresent()) {
				throw new NullPointerException("No plugin found for \"" + PLUGIN_ID + "\".");
			}

			// We have to get the plugin's version.
			final Optional<String> optionalLocalVersion = optionalPluginContainer.get().getVersion();
			if(!optionalLocalVersion.isPresent()) {
				return;
			}

			// Now we can request the remote version.
			final URL url = new URL(String.format("https://ore.spongepowered.org/api/v1/projects/%s", PLUGIN_ID));
			final String version = new JsonParser().parse(httpGet(url)).getAsJsonObject().get("recommended").getAsJsonObject().get("name").getAsString();

			// And we can compare them.
			if(versionCompare(optionalLocalVersion.get(), version) >= 0) {
				logger.info("No update found.");
				return;
			}

			logger.info("Found an update !");
			logger.info("Head to \"https://ore.spongepowered.org/api/projects/" + PLUGIN_ID + "/versions/recommended/download\" to download " + version + "...");
		}
		catch(final Exception ex) {
			logger.error("Cannot check for updates :", ex);
		}
	}

	/**
	 * Compares two version strings.
	 *
	 * Use this instead of String.compareTo() for a non-lexicographical
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 *
	 * @param str1 a string of ordinal numbers separated by decimal points.
	 * @param str2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if str1 is _numerically_ less than str2.
	 * The result is a positive integer if str1 is _numerically_ greater than str2.
	 * The result is zero if the strings are _numerically_ equal.
	 *
	 * @author Alex Gitelman.
	 */

	private static int versionCompare(final String str1, final String str2) {
		final String[] vals1 = str1.split("\\.");
		final String[] vals2 = str2.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while(i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}
		// compare first non-equal ordinal number
		if(i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return Integer.signum(vals1.length - vals2.length);
	}

	/**
	 * Sends an HTTP GET request.
	 *
	 * @param url The URL.
	 *
	 * @return The response body.
	 *
	 * @throws IOException If any I/O exception occurs.
	 */

	private static String httpGet(final URL url) throws IOException {
		final StringBuilder result = new StringBuilder();
		final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");

		final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while((line = reader.readLine()) != null) {
			result.append(line);
		}

		reader.close();
		return result.toString();
	}

}