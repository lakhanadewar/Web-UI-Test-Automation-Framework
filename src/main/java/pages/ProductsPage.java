package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * The product list (home page): search, filter, sort and add to cart.
 * Product ids are p1 to p12, so dynamic locators use testId("...-" + productId).
 */
public class ProductsPage extends BasePage {

	@FindBy(css = "[data-testid='catalogue-title']")
	private WebElement pageTitle;

	@FindBy(css = "[data-testid='search-input']")
	private WebElement searchInput;

	@FindBy(css = "[data-testid='category-filter']")
	private WebElement categoryFilter;

	@FindBy(css = "[data-testid='sort-select']")
	private WebElement sortSelect;

	@FindBy(css = "[data-testid='result-count']")
	private WebElement resultCount;

	@FindBy(css = "[data-testid='no-results']")
	private WebElement noResultsMessage;

	@FindBy(css = "[data-testid^='product-name-']")
	private List<WebElement> productNames;

	@FindBy(css = "[data-testid^='product-price-']")
	private List<WebElement> productPrices;

	public ProductsPage(WebDriver driver) {
		super(driver);
	}

	public boolean isProductsPageDisplayed() {
		return isDisplayed(pageTitle);
	}

	public void searchFor(String text) {
		type(searchInput, text);
	}

	/** value: all, Electronics, Clothing or Home */
	public void filterByCategory(String category) {
		selectByValue(categoryFilter, category);
	}

	/** value: asc (low to high) or desc (high to low) */
	public void sortByPrice(String order) {
		selectByValue(sortSelect, order);
	}

	public String getResultCount() {
		return getText(resultCount);
	}

	/** Waits for the result count to change, e.g. "1 products". */
	public void waitForResultCount(String expected) {
		waitForTextInElement(resultCount, expected);
	}

	public String getNoResultsMessage() {
		return getText(noResultsMessage);
	}

	public List<String> getProductNames() {
		List<String> names = new ArrayList<>();
		for (WebElement name : productNames) {
			names.add(name.getText().trim());
		}
		return names;
	}

	/** Returns the prices as numbers, e.g. "$79.99" becomes 79.99 */
	public List<Double> getProductPrices() {
		List<Double> prices = new ArrayList<>();
		for (WebElement price : productPrices) {
			prices.add(Double.parseDouble(price.getText().replace("$", "").trim()));
		}
		return prices;
	}

	public void addToCart(String productId) {
		click(testId("add-to-cart-" + productId));
	}

	public boolean isAddToCartEnabled(String productId) {
		return isEnabled(testId("add-to-cart-" + productId));
	}

	public String getAddToCartButtonText(String productId) {
		return getText(testId("add-to-cart-" + productId));
	}

	public String getStockText(String productId) {
		return getText(testId("product-stock-" + productId));
	}

	public void openProduct(String productId) {
		click(testId("product-link-" + productId));
	}

}
