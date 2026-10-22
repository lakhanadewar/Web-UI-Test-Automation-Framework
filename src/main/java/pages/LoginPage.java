package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class LoginPage extends BasePage {

	@FindBy(css = "[data-testid='login-title']")
	private WebElement pageTitle;

	@FindBy(css = "[data-testid='login-email']")
	private WebElement emailInput;

	@FindBy(css = "[data-testid='login-password']")
	private WebElement passwordInput;

	@FindBy(css = "[data-testid='login-remember']")
	private WebElement rememberMeCheckbox;

	@FindBy(css = "[data-testid='login-submit']")
	private WebElement loginButton;

	@FindBy(css = "[data-testid='login-error']")
	private WebElement loginError;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void enterEmail(String email) {
		type(emailInput, email);
	}

	public void enterPassword(String password) {
		type(passwordInput, password);
	}

	public void tickRememberMe() {
		if (!rememberMeCheckbox.isSelected()) {
			click(rememberMeCheckbox);
		}
	}

	public void clickLogin() {
		click(loginButton);
	}

	/** Fills the form and clicks Login. */
	public void login(String email, String password) {
		enterEmail(email);
		enterPassword(password);
		clickLogin();
	}

	public void loginWithRememberMe(String email, String password) {
		enterEmail(email);
		enterPassword(password);
		tickRememberMe();
		clickLogin();
	}

	public String getLoginError() {
		return getText(loginError);
	}

	/** Reads a field error, e.g. fieldName "email" reads login-email-error. */
	public String getFieldError(String fieldName) {
		return getText(testId("login-" + fieldName + "-error"));
	}

	/** Reads any error by its full test id, used by the data-driven tests. */
	public String getErrorByTestId(String errorTestId) {
		return getText(testId(errorTestId));
	}

	public boolean isLoginPageDisplayed() {
		return isDisplayed(pageTitle);
	}

}
