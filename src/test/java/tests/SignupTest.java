package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import dataproviders.TestDataProvider;
import utility.TestDataUtility;

/**
 * TS-03 Sign up
 */
public class SignupTest extends BaseTest {

	@Test(groups = { "smoke", "regression" }, description = "TC-SIG-01 New user can sign up and then log in")
	public void validSignupAndLogin() {
		String email = TestDataUtility.uniqueEmail();
		String password = "Test@1234";

		step("Sign up with " + email);
		header.clickSignup();
		signupPage.signup("New User", email, password, password);

		Assert.assertEquals(signupPage.getSuccessMessage(), "Account created successfully. You can now log in.");

		step("Log in with the new account");
		header.clickLogin();
		loginPage.login(email, password);

		Assert.assertEquals(header.getLoggedInUserName(), "Hi, New User");
	}

	@Test(groups = { "regression" }, description = "TC-SIG-02 Empty form shows every required field error")
	public void emptySignupForm() {
		header.clickSignup();
		signupPage.clickCreateAccount();

		Assert.assertEquals(signupPage.getErrorByTestId("signup-name-error"), "Name is required");
		Assert.assertEquals(signupPage.getErrorByTestId("signup-email-error"), "Email is required");
		Assert.assertEquals(signupPage.getErrorByTestId("signup-password-error"), "Password is required");
		Assert.assertEquals(signupPage.getErrorByTestId("signup-confirm-error"), "Confirm password is required");
	}

	@Test(groups = { "regression" }, dataProvider = "signupValidation", dataProviderClass = TestDataProvider.class,
			description = "TC-SIG-03 Sign-up validation rules (data-driven)")
	public void signupValidation(String testCase, String name, String email, String password,
			String confirmPassword, String errorTestId, String expectedMessage) {
		header.clickSignup();

		step("Sign up with email '" + email + "' and password '" + password + "'");
		signupPage.signup(name, email, password, confirmPassword);

		Assert.assertEquals(signupPage.getErrorByTestId(errorTestId), expectedMessage);
	}

	@Test(groups = { "regression" }, description = "TC-SIG-04 Password of exactly 8 characters is accepted (boundary)")
	public void passwordMinimumLengthAccepted() {
		header.clickSignup();
		signupPage.signup("Boundary User", TestDataUtility.uniqueEmail(), "Test@123", "Test@123");

		Assert.assertEquals(signupPage.getSuccessMessage(), "Account created successfully. You can now log in.");
	}

}
