package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import utility.TestDataUtility;

/**
 * TS-08 End-to-end order journeys
 */
public class EndToEndTest extends BaseTest {

	@Test(groups = { "smoke", "regression", "e2e" }, description = "TC-E2E-01 Logged-in user places an order with a coupon")
	public void placeOrderWithCoupon() {
		step("1. Log in");
		loginAsStandardUser();

		step("2. Add Wireless Headphones to the cart");
		addProductToCart("p1", "1");
		header.clickCart();

		step("3. Apply SAVE10");
		cartPage.applyCoupon("SAVE10");
		cartPage.waitForTotal("$76.99");
		cartPage.clickProceedToCheckout();

		step("4. Add a delivery address");
		addDefaultAddress();
		addressPage.clickContinueToPayment();

		step("5. Pay with the test card");
		paymentPage.enterCardDetails("4242 4242 4242 4242", TestDataUtility.futureExpiry(), "123");

		step("6. Review and place the order");
		Assert.assertEquals(reviewPage.getReviewCard(), "Card ending 4242");
		Assert.assertTrue(reviewPage.isProductListed("p1"));
		Assert.assertEquals(reviewPage.getTotal(), "$76.99");
		reviewPage.clickPlaceOrder();

		step("7. Check the confirmation");
		Assert.assertEquals(confirmationPage.getConfirmationTitle(), "Thank you! Your order is confirmed.");
		Assert.assertEquals(confirmationPage.getOrderTotal(), "$76.99");
		Assert.assertTrue(confirmationPage.getDeliveryAddress().contains("1 Test Street"));
		Assert.assertEquals(header.getCartCount(), "0", "Cart should be empty after the order");

		String orderId = confirmationPage.getOrderId();
		step("Order id: " + orderId);

		step("8. The order appears in the order history");
		confirmationPage.clickViewAllOrders();
		Assert.assertTrue(ordersPage.isOrderListed(orderId));
		Assert.assertEquals(ordersPage.getOrderTotal(orderId), "$76.99");
	}

	@Test(groups = { "regression", "e2e" }, description = "TC-E2E-02 New user signs up and places an order with free shipping")
	public void newUserOrderWithFreeShipping() {
		String email = TestDataUtility.uniqueEmail();

		step("1. Sign up and log in as a new user");
		header.clickSignup();
		signupPage.signup("New User", email, "Test@1234", "Test@1234");
		signupPage.getSuccessMessage();
		header.clickLogin();
		loginPage.login(email, "Test@1234");
		header.getLoggedInUserName();

		step("2. Add the Coffee Maker ($210.00) and apply FLAT50");
		addProductToCart("p12", "1");
		header.clickCart();
		cartPage.applyCoupon("FLAT50");
		cartPage.waitForTotal("$160.00");
		cartPage.clickProceedToCheckout();

		step("3. Address and payment");
		addDefaultAddress();
		addressPage.clickContinueToPayment();
		paymentPage.enterCardDetails("4242 4242 4242 4242", TestDataUtility.futureExpiry(), "123");
		reviewPage.clickPlaceOrder();

		step("4. Check the confirmation");
		Assert.assertEquals(confirmationPage.getConfirmationTitle(), "Thank you! Your order is confirmed.");
		Assert.assertEquals(confirmationPage.getOrderTotal(), "$160.00");
	}

	@Test(groups = { "regression", "e2e" }, description = "TC-E2E-03 Placing an order reduces the stock")
	public void orderReducesStock() {
		loginAsStandardUser();
		Assert.assertEquals(productsPage.getStockText("p1"), "In stock: 15");

		addProductToCart("p1", "1");
		header.clickCart();
		cartPage.clickProceedToCheckout();
		addDefaultAddress();
		addressPage.clickContinueToPayment();
		paymentPage.enterCardDetails("4242 4242 4242 4242", TestDataUtility.futureExpiry(), "123");
		reviewPage.clickPlaceOrder();
		confirmationPage.getOrderId();

		step("Go back to the products and check the stock");
		header.clickHome();
		Assert.assertEquals(productsPage.getStockText("p1"), "In stock: 14");
	}

}
