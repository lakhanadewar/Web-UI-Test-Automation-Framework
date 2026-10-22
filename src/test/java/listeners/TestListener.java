package listeners;

import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import base.DriverManager;
import utility.ConfigReader;
import utility.ExtentManager;
import utility.ScreenshotUtility;

/**
 * Listens to every test and writes the result into the Extent HTML report.
 * On failure it takes a screenshot and attaches it to the report.
 */
public class TestListener implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
		createReportTest(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentManager.getTest().pass("Test passed");
		ExtentManager.removeTest();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentTest test = ExtentManager.getTest();
		if (test == null) {
			test = createReportTest(result);
		}

		test.fail(result.getThrowable());

		if (DriverManager.getDriver() != null) {
			try {
				String screenshot = ScreenshotUtility.captureScreenShot(DriverManager.getDriver(),
						result.getMethod().getMethodName());
				test.fail("Screenshot at failure",
						MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
			} catch (Exception e) {
				test.warning("Could not take screenshot: " + e.getMessage());
			}
		}
		ExtentManager.removeTest();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		ExtentTest test = ExtentManager.getTest();
		if (test == null) {
			test = createReportTest(result);
		}
		test.skip(result.getThrowable() != null ? result.getThrowable().getMessage() : "Test skipped");
		ExtentManager.removeTest();
	}

	@Override
	public void onFinish(ITestContext context) {
		ExtentManager.getReport().flush();
	}

	// ---------------- helpers ----------------

	private ExtentTest createReportTest(ITestResult result) {
		String name = result.getMethod().getMethodName();

		// For data-driven tests, show the test case id from the first Excel column
		Object[] params = result.getParameters();
		if (params.length > 0 && params[0] != null) {
			name = name + " [" + params[0] + "]";
		}

		ExtentTest test = ExtentManager.getReport().createTest(name, result.getMethod().getDescription());
		test.assignCategory(result.getMethod().getGroups());
		test.assignDevice(getBrowser(result));
		if (params.length > 1) {
			test.info("Test data: " + Arrays.toString(params));
		}

		ExtentManager.setTest(test);
		return test;
	}

	private String getBrowser(ITestResult result) {
		String fromXml = result.getTestContext().getCurrentXmlTest().getParameter("browser");
		return (fromXml != null && !fromXml.isEmpty()) ? fromXml : ConfigReader.get("browser");
	}

}
