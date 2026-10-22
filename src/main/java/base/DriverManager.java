package base;

import org.openqa.selenium.WebDriver;

/**
 * Keeps one WebDriver per thread.
 * This is what makes parallel execution safe: each test thread gets its own browser.
 */
public class DriverManager {

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	public static WebDriver getDriver() {
		return driver.get();
	}

	public static void setDriver(WebDriver webDriver) {
		driver.set(webDriver);
	}

	public static void quitDriver() {
		if (driver.get() != null) {
			driver.get().quit();
			driver.remove();
		}
	}

}
