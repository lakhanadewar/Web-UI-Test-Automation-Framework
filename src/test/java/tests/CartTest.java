package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import dataproviders.TestDataProvider;

/**
 * TS-05 Cart: add, update, remove, quantity limits and shipping
 */
public class CartTest extends BaseTest {

	@Test(groups = { "smoke", "regression" }, description = "TC-CRT-01 Add a product to the cart")
	public void addProductToCart() {
		step("Add Wireless Headphones (p1)");
		addProductToCart("p1", "1");

		header.clickCart();

		Assert.assertTrue(cartPage.isProductInCart("p1"));
		Assert.assertEquals(cartPage.getQuantity("p1"), "1");
		Assert.assertEquals(cartPage.getSubtotal(), "$79.99");
		Assert.assertEquals(cartPage.getShipping(), "$5.00");
		Assert.assertEquals(cartPage.getTotal(), "$84.99");
	}

	@Test(groups = { "regression" }, description = "TC-CRT-02 Increasing quantity updates the totals")
	public void increaseQuantity() {
		addProductToCart("p1", "1");
		header.clickCart();

		step("Click + once");
		cartPage.increaseQuantity("p1");
		cartPage.waitForQuantity("p1", "2");

		Assert.assertEquals(cartPage.getLineTotal("p1"), "$159.98");
		Assert.assertEquals(header.getCartCount(), "2");
		Assert.assertEquals(cartPage.getShipping(), "Free", "Orders over $100 should ship free");
	}

	@Test(groups = { "regression" }, description = "TC-CRT-03 Quantity cannot go below 1 (boundary)")
	public void quantityMinimumIsOne() {
		addProductToCart("p1", "1");
		header.clickCart();

		Assert.assertFalse(cartPage.isDecreaseEnabled("p1"), "The - button should be disabled at quantity 1");
		Assert.assertEquals(cartPage.getQuantity("p1"), "1");
	}

	@Test(groups = { "regression" }, description = "TC-CRT-04 Quantity cannot go above 10 per item (boundary)")
	public void quantityMaximumIsTen() {
		openPage("/product/p9"); // Ceramic Mug, stock 40
		productDetailsPage.addToCart("10");
		header.waitForCartCount("10");

		header.clickCart();

		Assert.assertEquals(cartPage.getQuantity("p9"), "10");
		Assert.assertFalse(cartPage.isIncreaseEnabled("p9"), "The + button should be disabled at quantity 10");
	}

	@Test(groups = { "regression" }, dataProvider = "productQuantity", dataProviderClass = TestDataProvider.class,
			description = "TC-CRT-05 Quantity rules on the product page (data-driven)")
	public void productQuantityRules(String testCase, String productId, String quantity, String expectedMessage) {
		openPage("/product/" + productId);

		step("Add " + quantity + " of " + productId);
		productDetailsPage.addToCart(quantity);

		Assert.assertEquals(productDetailsPage.getMessage(), expectedMessage);
	}

	@Test(groups = { "regression" }, description = "TC-CRT-06 Removing the last item shows the empty cart")
	public void removeProduct() {
		addProductToCart("p1", "1");
		header.clickCart();

		cartPage.removeProduct("p1");

		Assert.assertEquals(cartPage.getEmptyCartMessage(), "Your cart is empty");
		Assert.assertEquals(header.getCartCount(), "0");
	}

	@Test(groups = { "regression" }, description = "TC-CRT-07 Free shipping only applies over $100 (boundary)")
	public void freeShippingBoundary() {
		openPage("/product/p9"); // Ceramic Mug, $12.50
		productDetailsPage.addToCart("8");
		header.waitForCartCount("8");
		header.clickCart();

		step("Subtotal exactly $100.00 still pays shipping");
		Assert.assertEquals(cartPage.getSubtotal(), "$100.00");
		Assert.assertEquals(cartPage.getShipping(), "$5.00");
		Assert.assertEquals(cartPage.getTotal(), "$105.00");

		step("Subtotal $112.50 ships free");
		cartPage.increaseQuantity("p9");
		cartPage.waitForTotal("$112.50");
		Assert.assertEquals(cartPage.getShipping(), "Free");
	}

}
