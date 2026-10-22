package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import dataproviders.TestDataProvider;
import utility.TestDataUtility;

/**
 * TS-07 Checkout: address and payment validation
 */
public class CheckoutTest extends BaseTest {

	/** Logs in, adds Wireless Headphones and opens the checkout. */
	private void goToCheckout() {
		loginAsStandardUser();
		addProductToCart("p1", "1");
		header.clickCart();
		cartPage.clickProceedToCheckout();
	}

	/** Same as above, then saves an address and moves to the payment step. */
	private void goToPaymentStep() {
		goToCheckout();
		addDefaultAddress();
		addressPage.clickContinueToPayment();
	}

	@Test(groups = { "regression" }, description = "TC-CHK-01 Cannot continue without a delivery address")
	public void continueWithoutAddress() {
		goToCheckout();

		addressPage.clickContinueToPayment();

		Assert.assertEquals(addressPage.getCheckoutError(), "Please select a delivery address");
	}

	@Test(groups = { "regression" }, description = "TC-CHK-02 Empty address form shows required field errors")
	public void emptyAddressForm() {
		goToCheckout();

		addressPage.clickSaveAddress();

		Assert.assertEquals(addressPage.getFieldError("fullName"), "Full name is required");
		Assert.assertEquals(addressPage.getFieldError("phone"), "Phone is required");
		Assert.assertEquals(addressPage.getFieldError("line"), "Address line is required");
		Assert.assertEquals(addressPage.getFieldError("city"), "City is required");
		Assert.assertEquals(addressPage.getFieldError("postcode"), "Postcode is required");
		Assert.assertEquals(addressPage.getFieldError("country"), "Country is required");
	}

	@Test(groups = { "regression" }, dataProvider = "addressPhone", dataProviderClass = TestDataProvider.class,
			description = "TC-CHK-03 Phone must be exactly 10 digits (data-driven, boundary)")
	public void phoneValidation(String testCase, String phone, String expectedMessage) {
		goToCheckout();

		step("Save an address with phone '" + phone + "'");
		addressPage.addAddress("Test User", phone, "1 Test Street", "London", "SW1A 1AA", "United Kingdom");

		Assert.assertEquals(addressPage.getFieldError("phone"), expectedMessage);
	}

	@Test(groups = { "smoke", "regression" }, description = "TC-CHK-04 Valid address is saved and selected")
	public void saveValidAddress() {
		goToCheckout();

		addDefaultAddress();

		Assert.assertTrue(addressPage.isAnAddressSelected(), "Saved address should be selected");
		Assert.assertTrue(addressPage.getAddressBookText().contains("Test User"));
	}

	@Test(groups = { "regression" }, dataProvider = "paymentValidation", dataProviderClass = TestDataProvider.class,
			description = "TC-CHK-05 Card validation and declined payment (data-driven)")
	public void paymentValidation(String testCase, String cardNumber, String expiry, String cvv, String errorTestId,
			String expectedMessage) {
		goToPaymentStep();

		step("Pay with card '" + cardNumber + "', expiry '" + expiry + "', CVV '" + cvv + "'");
		paymentPage.enterCardDetails(cardNumber, expiry, cvv);

		Assert.assertEquals(paymentPage.getErrorByTestId(errorTestId), expectedMessage);
		Assert.assertTrue(paymentPage.isPaymentStepDisplayed(), "User must stay on the payment step");
	}

	@Test(groups = { "regression" }, description = "TC-CHK-06 Card expiring this month is accepted (boundary)")
	public void currentMonthExpiryAccepted() {
		goToPaymentStep();

		paymentPage.enterCardDetails("4242 4242 4242 4242", TestDataUtility.currentMonthExpiry(), "123");

		Assert.assertTrue(reviewPage.isReviewStepDisplayed(), "Review step should open");
	}

	@Test(groups = { "regression" }, description = "TC-CHK-07 Card that expired last month is rejected (boundary)")
	public void lastMonthExpiryRejected() {
		goToPaymentStep();

		paymentPage.enterCardDetails("4242 4242 4242 4242", TestDataUtility.lastMonthExpiry(), "123");

		Assert.assertEquals(paymentPage.getErrorByTestId("payment-expiry-error"), "Card has expired");
	}

}
