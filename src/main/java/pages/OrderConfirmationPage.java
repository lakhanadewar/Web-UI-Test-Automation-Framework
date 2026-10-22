package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class OrderConfirmationPage extends BasePage {

	@FindBy(css = "[data-testid='order-confirmation-title']")
	private WebElement confirmationTitle;

	@FindBy(css = "[data-testid='order-id']")
	private WebElement orderId;

	@FindBy(css = "[data-testid='order-total']")
	private WebElement orderTotal;

	@FindBy(css = "[data-testid='order-address']")
	private WebElement orderAddress;

	@FindBy(css = "[data-testid='view-orders']")
	private WebElement viewOrdersLink;

	public OrderConfirmationPage(WebDriver driver) {
		super(driver);
	}

	public String getConfirmationTitle() {
		return getText(confirmationTitle);
	}

	public String getOrderId() {
		return getText(orderId);
	}

	public String getOrderTotal() {
		return getText(orderTotal);
	}

	public String getDeliveryAddress() {
		return getText(orderAddress);
	}

	public void clickViewAllOrders() {
		click(viewOrdersLink);
	}

}
