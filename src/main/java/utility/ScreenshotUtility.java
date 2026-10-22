package utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

public class ScreenshotUtility {

	/**
	 * Saves a screenshot to the screenshot folder and returns its path,
	 * relative to the report folder so the HTML report can show it.
	 */
	public static String captureScreenShot(WebDriver driver, String testName) {
		String time = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
		String fileName = testName + "_" + time + ".png";

		File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File destination = new File(ConfigReader.get("screenshotFolder"), fileName);

		try {
			destination.getParentFile().mkdirs();
			FileHandler.copy(source, destination);
		} catch (IOException e) {
			throw new RuntimeException("Could not save screenshot", e);
		}
		return "screenshots/" + fileName;
	}

}
