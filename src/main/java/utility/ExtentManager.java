package utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Creates one Extent HTML report per run, e.g. reports/TestKart_Report_20261022_220500.html
 * Each test thread keeps its own ExtentTest, so parallel runs log to the right test.
 */
public class ExtentManager {

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	public static synchronized ExtentReports getReport() {
		if (extent == null) {
			String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String reportPath = ConfigReader.get("reportFolder") + "/TestKart_Report_" + time + ".html";

			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
			spark.config().setDocumentTitle("TestKart Automation Report");
			spark.config().setReportName("TestKart UI Test Results");
			spark.config().setTheme(Theme.STANDARD);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			extent.setSystemInfo("Application", ConfigReader.get("baseUrl"));
			extent.setSystemInfo("Run mode", ConfigReader.get("runMode"));
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Java", System.getProperty("java.version"));
		}
		return extent;
	}

	public static void setTest(ExtentTest extentTest) {
		test.set(extentTest);
	}

	public static ExtentTest getTest() {
		return test.get();
	}

	public static void removeTest() {
		test.remove();
	}

}
