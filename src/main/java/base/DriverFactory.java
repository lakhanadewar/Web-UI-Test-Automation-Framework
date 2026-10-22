package base;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import utility.ConfigReader;

/**
 * Creates a browser. Works for local runs and for Selenium Grid (Docker).
 *
 * Local: Selenium Manager finds and downloads the matching driver automatically.
 * Grid : the browser runs inside a Docker node and we talk to it through the hub.
 */
public class DriverFactory {

	public static WebDriver createDriver(String browser) {

		String runMode = ConfigReader.get("runMode");
		boolean headless = ConfigReader.getBoolean("headless");

		MutableCapabilities options = getOptions(browser, headless);

		if (runMode.equalsIgnoreCase("grid")) {
			return createRemoteDriver(options);
		}
		return createLocalDriver(browser, options);
	}

	private static MutableCapabilities getOptions(String browser, boolean headless) {

		switch (browser.toLowerCase()) {

		case "chrome":
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--window-size=1366,900");
			if (headless) {
				chromeOptions.addArguments("--headless=new");
			}
			return chromeOptions;

		case "firefox":
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("--width=1366", "--height=900");
			if (headless) {
				firefoxOptions.addArguments("-headless");
			}
			return firefoxOptions;

		case "edge":
			EdgeOptions edgeOptions = new EdgeOptions();
			edgeOptions.addArguments("--window-size=1366,900");
			if (headless) {
				edgeOptions.addArguments("--headless=new");
			}
			return edgeOptions;

		default:
			throw new IllegalArgumentException("Browser not supported: " + browser + " (use chrome, firefox or edge)");
		}
	}

	private static WebDriver createLocalDriver(String browser, MutableCapabilities options) {

		switch (browser.toLowerCase()) {
		case "firefox":
			return new FirefoxDriver((FirefoxOptions) options);
		case "edge":
			return new EdgeDriver((EdgeOptions) options);
		default:
			return new ChromeDriver((ChromeOptions) options);
		}
	}

	private static WebDriver createRemoteDriver(MutableCapabilities options) {

		String gridUrl = ConfigReader.get("gridUrl");
		try {
			return new RemoteWebDriver(new URL(gridUrl), options);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Grid URL is not valid: " + gridUrl, e);
		}
	}

}
