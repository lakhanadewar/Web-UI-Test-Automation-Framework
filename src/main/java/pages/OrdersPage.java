package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * Order history page (only for logged-in users).
 */
public class OrdersPage extends BasePage {

	@FindBy(css = "[data-testid='orders-title']")
	private WebElement pageTitle;

	@FindBy(css = "[data-testid='orders-empty']")
	private WebElement noOrdersMessage;

	public OrdersPage(WebDriver driver) {
		super(driver);
	}

	public boolean isOrdersPageDisplayed() {
		return isDisplayed(pageTitle);
	}

	public String getNoOrdersMessage() {
		return getText(noOrdersMessage);
	}

	public boolean isOrderListed(String orderId) {
		return isDisplayed(testId("order-row-" + orderId));
	}

	public String getOrderTotal(String orderId) {
		return getText(testId("order-total-" + orderId));
	}

}
