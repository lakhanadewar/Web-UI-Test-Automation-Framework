package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class SignupPage extends BasePage {

	@FindBy(css = "[data-testid='signup-name']")
	private WebElement nameInput;

	@FindBy(css = "[data-testid='signup-email']")
	private WebElement emailInput;

	@FindBy(css = "[data-testid='signup-password']")
	private WebElement passwordInput;

	@FindBy(css = "[data-testid='signup-confirm']")
	private WebElement confirmPasswordInput;

	@FindBy(css = "[data-testid='signup-submit']")
	private WebElement createAccountButton;

	@FindBy(css = "[data-testid='signup-success']")
	private WebElement successMessage;

	public SignupPage(WebDriver driver) {
		super(driver);
	}

	/** Fills every field and clicks Create account. */
	public void signup(String name, String email, String password, String confirmPassword) {
		type(nameInput, name);
		type(emailInput, email);
		type(passwordInput, password);
		type(confirmPasswordInput, confirmPassword);
		click(createAccountButton);
	}

	public void clickCreateAccount() {
		click(createAccountButton);
	}

	public String getSuccessMessage() {
		return getText(successMessage);
	}

	/** Reads any error by its test id, e.g. signup-email-error or signup-error. */
	public String getErrorByTestId(String errorTestId) {
		return getText(testId(errorTestId));
	}

}
