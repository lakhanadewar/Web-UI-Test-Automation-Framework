package utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings from config.properties.
 * A value passed on the command line (-Dkey=value) always wins over the file.
 */
public class ConfigReader {

	private static Properties properties = new Properties();

	static {
		try (InputStream file = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (file == null) {
				throw new RuntimeException("config.properties not found in src/main/resources");
			}
			properties.load(file);
		} catch (IOException e) {
			throw new RuntimeException("Could not read config.properties", e);
		}
	}

	public static String get(String key) {
		String fromCommandLine = System.getProperty(key);
		if (fromCommandLine != null && !fromCommandLine.trim().isEmpty()) {
			return fromCommandLine.trim();
		}

		String fromFile = properties.getProperty(key);
		if (fromFile == null) {
			throw new RuntimeException("Missing key in config.properties: " + key);
		}
		return fromFile.trim();
	}

	public static int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}

}
