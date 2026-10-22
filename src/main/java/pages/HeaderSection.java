package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * The top navigation bar and footer that appear on every page.
 */
public class HeaderSection extends BasePage {

	@FindBy(css = "[data-testid='nav-home']")
	private WebElement homeLink;

	@FindBy(css = "[data-testid='nav-cart']")
	private WebElement cartLink;

	@FindBy(css = "[data-testid='cart-count']")
	private WebElement cartCount;

	@FindBy(css = "[data-testid='nav-login']")
	private WebElement loginLink;

	@FindBy(css = "[data-testid='nav-signup']")
	private WebElement signupLink;

	@FindBy(css = "[data-testid='nav-orders']")
	private WebElement ordersLink;

	@FindBy(css = "[data-testid='nav-user-name']")
	private WebElement userName;

	@FindBy(css = "[data-testid='logout-button']")
	private WebElement logoutButton;

	@FindBy(css = "[data-testid='reset-demo-data']")
	private WebElement resetDemoDataButton;

	public HeaderSection(WebDriver driver) {
		super(driver);
	}

	public void clickLogin() {
		click(loginLink);
	}

	public void clickSignup() {
		click(signupLink);
	}

	public void clickCart() {
		click(cartLink);
	}

	public void clickOrders() {
		click(ordersLink);
	}

	public void clickHome() {
		click(homeLink);
	}

	public void clickLogout() {
		click(logoutButton);
	}

	public void clickResetDemoData() {
		click(resetDemoDataButton);
	}

	public String getCartCount() {
		return getText(cartCount);
	}

	/** Waits until the cart badge shows the expected number. */
	public void waitForCartCount(String expectedCount) {
		waitForTextInElement(cartCount, expectedCount);
	}

	public String getLoggedInUserName() {
		return getText(userName);
	}

	/** Checks right now (no wait). Used to confirm a user is NOT logged in. */
	public boolean isUserLoggedIn() {
		return isPresentNow(testId("logout-button"));
	}

	public boolean isLoginLinkDisplayed() {
		return isDisplayed(testId("nav-login"));
	}

}
