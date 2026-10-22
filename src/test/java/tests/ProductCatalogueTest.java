package tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

/**
 * TS-04 Product catalogue: list, search, filter, sort, product details
 */
public class ProductCatalogueTest extends BaseTest {

	@Test(groups = { "smoke", "regression" }, description = "TC-CAT-01 All 12 products are listed")
	public void allProductsListed() {
		Assert.assertTrue(productsPage.isProductsPageDisplayed());
		Assert.assertEquals(productsPage.getResultCount(), "12 products");
		Assert.assertEquals(productsPage.getProductNames().size(), 12);
	}

	@Test(groups = { "regression" }, description = "TC-CAT-02 Search finds a product, ignoring upper or lower case")
	public void searchIsCaseInsensitive() {
		step("Search for 'mug'");
		productsPage.searchFor("mug");
		productsPage.waitForResultCount("1 products");
		Assert.assertEquals(productsPage.getProductNames().get(0), "Ceramic Mug");

		step("Search for 'MUG'");
		productsPage.searchFor("MUG");
		productsPage.waitForResultCount("1 products");
		Assert.assertEquals(productsPage.getProductNames().get(0), "Ceramic Mug");
	}

	@Test(groups = { "regression" }, description = "TC-CAT-03 Search with no match shows a message")
	public void searchNoResults() {
		productsPage.searchFor("zzzz");

		Assert.assertEquals(productsPage.getNoResultsMessage(), "No products found");
		Assert.assertEquals(productsPage.getResultCount(), "0 products");
	}

	@Test(groups = { "regression" }, description = "TC-CAT-04 Category filter shows only that category")
	public void filterByCategory() {
		productsPage.filterByCategory("Clothing");
		productsPage.waitForResultCount("4 products");

		List<String> expected = List.of("Cotton T-Shirt", "Denim Jacket", "Running Shoes", "Wool Scarf");
		Assert.assertEquals(productsPage.getProductNames(), expected);
	}

	@Test(groups = { "regression" }, description = "TC-CAT-05 Sort by price low to high")
	public void sortPriceLowToHigh() {
		productsPage.sortByPrice("asc");

		List<Double> actual = productsPage.getProductPrices();
		List<Double> expected = new ArrayList<>(actual);
		Collections.sort(expected);

		Assert.assertEquals(actual, expected, "Prices should be in ascending order");
	}

	@Test(groups = { "regression" }, description = "TC-CAT-06 Sort by price high to low")
	public void sortPriceHighToLow() {
		productsPage.sortByPrice("desc");

		List<Double> actual = productsPage.getProductPrices();
		List<Double> expected = new ArrayList<>(actual);
		expected.sort(Collections.reverseOrder());

		Assert.assertEquals(actual, expected, "Prices should be in descending order");
	}

	@Test(groups = { "smoke", "regression" }, description = "TC-CAT-07 Out-of-stock products cannot be added")
	public void outOfStockProductsDisabled() {
		// p3 = Bluetooth Speaker, p8 = Wool Scarf (seeded as out of stock)
		String[] outOfStock = { "p3", "p8" };

		for (String productId : outOfStock) {
			Assert.assertFalse(productsPage.isAddToCartEnabled(productId), productId + " button should be disabled");
			Assert.assertEquals(productsPage.getAddToCartButtonText(productId), "Out of stock");
		}
	}

	@Test(groups = { "regression" }, description = "TC-CAT-08 Product details page shows the right product")
	public void productDetails() {
		productsPage.openProduct("p10");

		Assert.assertEquals(productDetailsPage.getProductName(), "Desk Lamp");
		Assert.assertEquals(productDetailsPage.getProductPrice(), "$39.99");
		Assert.assertEquals(productDetailsPage.getStockText(), "In stock: 3");
	}

}
