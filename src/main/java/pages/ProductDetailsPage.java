package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class ProductDetailsPage extends BasePage {

	@FindBy(css = "[data-testid='product-detail-name']")
	private WebElement productName;

	@FindBy(css = "[data-testid='product-detail-price']")
	private WebElement productPrice;

	@FindBy(css = "[data-testid='product-detail-stock']")
	private WebElement productStock;

	@FindBy(css = "[data-testid='product-detail-qty']")
	private WebElement quantityInput;

	@FindBy(css = "[data-testid='product-detail-add-to-cart']")
	private WebElement addToCartButton;

	@FindBy(css = "[data-testid='product-detail-message']")
	private WebElement message;

	@FindBy(css = "[data-testid='back-to-products']")
	private WebElement backToProductsLink;

	public ProductDetailsPage(WebDriver driver) {
		super(driver);
	}

	public String getProductName() {
		return getText(productName);
	}

	public String getProductPrice() {
		return getText(productPrice);
	}

	public String getStockText() {
		return getText(productStock);
	}

	public void enterQuantity(String quantity) {
		type(quantityInput, quantity);
	}

	public void clickAddToCart() {
		click(addToCartButton);
	}

	/** Sets the quantity and clicks Add to cart. */
	public void addToCart(String quantity) {
		enterQuantity(quantity);
		clickAddToCart();
	}

	public String getMessage() {
		return getText(message);
	}

	public void clickBackToProducts() {
		click(backToProductsLink);
	}

}
