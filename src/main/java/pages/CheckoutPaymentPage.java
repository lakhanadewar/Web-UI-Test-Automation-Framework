package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * Checkout step 2: mock card payment.
 */
public class CheckoutPaymentPage extends BasePage {

	@FindBy(css = "[data-testid='payment-form']")
	private WebElement paymentForm;

	@FindBy(css = "[data-testid='payment-card']")
	private WebElement cardNumberInput;

	@FindBy(css = "[data-testid='payment-expiry']")
	private WebElement expiryInput;

	@FindBy(css = "[data-testid='payment-cvv']")
	private WebElement cvvInput;

	@FindBy(css = "[data-testid='payment-continue']")
	private WebElement continueToReviewButton;

	@FindBy(css = "[data-testid='payment-back']")
	private WebElement backButton;

	@FindBy(css = "[data-testid='payment-error']")
	private WebElement paymentError;

	public CheckoutPaymentPage(WebDriver driver) {
		super(driver);
	}

	public boolean isPaymentStepDisplayed() {
		return isDisplayed(paymentForm);
	}

	/** Fills the card details and clicks Continue to review. */
	public void enterCardDetails(String cardNumber, String expiry, String cvv) {
		type(cardNumberInput, cardNumber);
		type(expiryInput, expiry);
		type(cvvInput, cvv);
		click(continueToReviewButton);
	}

	public void clickBack() {
		click(backButton);
	}

	public String getPaymentError() {
		return getText(paymentError);
	}

	/** Reads any error by its test id, e.g. payment-cvv-error or payment-error. */
	public String getErrorByTestId(String errorTestId) {
		return getText(testId(errorTestId));
	}

}
