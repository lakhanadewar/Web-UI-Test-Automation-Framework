package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * Checkout step 3: review the order and place it.
 */
public class CheckoutReviewPage extends BasePage {

	@FindBy(css = "[data-testid='review-address']")
	private WebElement reviewAddress;

	@FindBy(css = "[data-testid='review-card']")
	private WebElement reviewCard;

	@FindBy(css = "[data-testid='summary-total']")
	private WebElement total;

	@FindBy(css = "[data-testid='place-order']")
	private WebElement placeOrderButton;

	public CheckoutReviewPage(WebDriver driver) {
		super(driver);
	}

	public boolean isReviewStepDisplayed() {
		return isDisplayed(testId("review-step"));
	}

	public String getReviewAddress() {
		return getText(reviewAddress);
	}

	/** e.g. "Card ending 4242" */
	public String getReviewCard() {
		return getText(reviewCard);
	}

	public boolean isProductListed(String productId) {
		return isDisplayed(testId("review-item-" + productId));
	}

	public String getTotal() {
		return getText(total);
	}

	public void clickPlaceOrder() {
		click(placeOrderButton);
	}

}
