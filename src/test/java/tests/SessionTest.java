package tests;

import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import utility.ConfigReader;

/**
 * TS-02 Session handling and protected pages
 *
 * TestKart keeps a normal login in sessionStorage (one tab only)
 * and a "Remember me" login in localStorage (survives new tabs).
 */
public class SessionTest extends BaseTest {

	@Test(groups = { "regression" }, description = "TC-SES-01 Session survives a page refresh")
	public void sessionSurvivesRefresh() {
		loginAsStandardUser();

		step("Refresh the page");
		driver.navigate().refresh();

		Assert.assertEquals(header.getLoggedInUserName(), "Hi, Standard User");
	}

	@Test(groups = { "regression" }, description = "TC-SES-02 Without Remember me, a new tab is logged out")
	public void newTabWithoutRememberMe() {
		loginAsStandardUser();

		step("Open the app in a new tab");
		driver.switchTo().newWindow(WindowType.TAB);
		openPage("/");

		Assert.assertTrue(header.isLoginLinkDisplayed(), "New tab should not share the session");
		Assert.assertFalse(header.isUserLoggedIn());
	}

	@Test(groups = { "regression" }, description = "TC-SES-03 With Remember me, a new tab stays logged in")
	public void newTabWithRememberMe() {
		openPage("/login");
		loginPage.loginWithRememberMe(ConfigReader.get("standardUser.email"),
				ConfigReader.get("standardUser.password"));
		header.getLoggedInUserName();

		step("Open the app in a new tab");
		driver.switchTo().newWindow(WindowType.TAB);
		openPage("/");

		Assert.assertEquals(header.getLoggedInUserName(), "Hi, Standard User");
	}

	@Test(groups = { "smoke", "regression" }, description = "TC-SES-04 Protected page redirects to login when logged out")
	public void protectedPageRedirectsToLogin() {
		step("Open the orders page without logging in");
		openPage("/orders");

		Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be shown");
		Assert.assertTrue(loginPage.getCurrentUrl().contains("redirect=%2Forders"),
				"URL should remember where the user was going: " + loginPage.getCurrentUrl());
	}

	@Test(groups = { "regression" }, description = "TC-SES-05 After login the user returns to the page they asked for")
	public void loginReturnsToRequestedPage() {
		openPage("/orders");

		loginPage.login(ConfigReader.get("standardUser.email"), ConfigReader.get("standardUser.password"));

		Assert.assertTrue(ordersPage.isOrdersPageDisplayed(), "User should be back on the orders page");
		Assert.assertEquals(ordersPage.getNoOrdersMessage(), "You have no orders yet");
	}

	@Test(groups = { "regression" }, description = "TC-SES-06 Logged-out user cannot open checkout")
	public void checkoutNeedsLogin() {
		addProductToCart("p1", "1");

		openPage("/checkout");

		Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Checkout must ask the user to log in");
		Assert.assertTrue(loginPage.getCurrentUrl().contains("redirect=%2Fcheckout"));
	}

}
