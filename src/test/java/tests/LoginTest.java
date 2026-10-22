package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import dataproviders.TestDataProvider;
import utility.ConfigReader;

/**
 * TS-01 Login and logout
 */
public class LoginTest extends BaseTest {

	@Test(groups = { "smoke", "regression" }, description = "TC-LOG-01 Standard user can log in")
	public void validLogin() {
		step("Open the login page");
		header.clickLogin();

		step("Log in as the standard user");
		loginPage.login(ConfigReader.get("standardUser.email"), ConfigReader.get("standardUser.password"));

		Assert.assertEquals(header.getLoggedInUserName(), "Hi, Standard User");
		Assert.assertTrue(productsPage.isProductsPageDisplayed(), "User should land on the products page");
	}

	@Test(groups = { "regression" }, description = "TC-LOG-02 Locked user cannot log in")
	public void lockedUserLogin() {
		header.clickLogin();
		loginPage.login(ConfigReader.get("lockedUser.email"), ConfigReader.get("lockedUser.password"));

		Assert.assertEquals(loginPage.getLoginError(), "Your account is locked");
		Assert.assertFalse(header.isUserLoggedIn(), "Locked user must not be logged in");
	}

	@Test(groups = { "regression" }, dataProvider = "invalidLogin", dataProviderClass = TestDataProvider.class,
			description = "TC-LOG-03 Invalid login shows the right error (data-driven)")
	public void invalidLogin(String testCase, String email, String password, String errorTestId,
			String expectedMessage) {
		header.clickLogin();

		step("Try to log in with: " + email + " / " + password);
		loginPage.login(email, password);

		Assert.assertEquals(loginPage.getErrorByTestId(errorTestId), expectedMessage);
		Assert.assertFalse(header.isUserLoggedIn());
	}

	@Test(groups = { "smoke", "regression" }, description = "TC-LOG-04 User can log out")
	public void logout() {
		loginAsStandardUser();

		step("Click Logout");
		header.clickLogout();

		Assert.assertTrue(loginPage.isLoginPageDisplayed(), "User should be sent to the login page");
		Assert.assertTrue(header.isLoginLinkDisplayed(), "Login link should be back in the header");
	}

}
