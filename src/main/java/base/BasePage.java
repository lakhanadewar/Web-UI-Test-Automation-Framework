package base;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utility.ConfigReader;

/**
 * Parent class for every page object.
 * It holds the common actions (click, type, read text) so each page only has its own locators and steps.
 * Every action waits for the element first, so we never need Thread.sleep().
 */
public class BasePage {

	protected WebDriver driver;
	protected WebDriverWait wait;

	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout")));
		PageFactory.initElements(driver, this);
	}

	// ---------- Locator helper ----------

	/** Every element in TestKart has a data-testid, so this builds a locator from it. */
	protected By testId(String id) {
		return By.cssSelector("[data-testid='" + id + "']");
	}

	// ---------- Actions on @FindBy elements ----------

	protected void click(WebElement element) {
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
	}

	protected void type(WebElement element, String text) {
		wait.until(ExpectedConditions.visibilityOf(element));
		clearField(element);
		if (text != null && !text.isEmpty()) {
			element.sendKeys(text);
		}
	}

	protected String getText(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element)).getText().trim();
	}

	/** Waits up to the timeout for the element to show. Use it for "should be shown" checks. */
	protected boolean isDisplayed(WebElement element) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	protected void selectByValue(WebElement element, String value) {
		wait.until(ExpectedConditions.visibilityOf(element));
		new Select(element).selectByValue(value);
	}

	// ---------- Actions on dynamic elements (e.g. product id in the test id) ----------

	protected void click(By locator) {
		wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
	}

	protected String getText(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
	}

	/** Waits up to the timeout for the element to show. Use it for "should be shown" checks. */
	protected boolean isDisplayed(By locator) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	/** Checks right now, without waiting. Use it for "should NOT be shown" checks so tests stay fast. */
	protected boolean isPresentNow(By locator) {
		List<WebElement> elements = driver.findElements(locator);
		return !elements.isEmpty() && elements.get(0).isDisplayed();
	}

	protected boolean isEnabled(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isEnabled();
	}

	protected WebElement find(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// ---------- Waits ----------

	protected void waitForUrlContains(String text) {
		wait.until(ExpectedConditions.urlContains(text));
	}

	protected void waitForTextInElement(WebElement element, String text) {
		wait.until(ExpectedConditions.textToBePresentInElement(element, text));
	}

	// ---------- Small helpers ----------

	/**
	 * Clears a field. React apps sometimes put the old value back after clear(),
	 * so if anything is left we select it and delete it like a real user would.
	 */
	private void clearField(WebElement element) {
		element.clear();
		String leftOver = element.getAttribute("value");
		if (leftOver != null && !leftOver.isEmpty()) {
			((JavascriptExecutor) driver).executeScript("arguments[0].select();", element);
			element.sendKeys(Keys.BACK_SPACE);
		}
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	public String getPageTitle() {
		return driver.getTitle();
	}

}
