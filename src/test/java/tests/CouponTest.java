package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import dataproviders.TestDataProvider;

/**
 * TS-06 Coupons
 */
public class CouponTest extends BaseTest {

	@Test(groups = { "smoke", "regression" }, description = "TC-CPN-01 SAVE10 gives 10% off")
	public void save10Coupon() {
		addProductToCart("p1", "1"); // $79.99
		header.clickCart();

		cartPage.applyCoupon("SAVE10");

		Assert.assertEquals(cartPage.getCouponMessage(), "Coupon applied");
		Assert.assertEquals(cartPage.getAppliedCoupon(), "Coupon SAVE10 applied");
		Assert.assertEquals(cartPage.getDiscount(), "-$8.00");
		Assert.assertEquals(cartPage.getTotal(), "$76.99");
	}

	@Test(groups = { "regression" }, description = "TC-CPN-02 Coupon codes are not case sensitive")
	public void couponCodeLowerCase() {
		addProductToCart("p1", "1");
		header.clickCart();

		cartPage.applyCoupon("save10");

		Assert.assertEquals(cartPage.getCouponMessage(), "Coupon applied");
		Assert.assertEquals(cartPage.getDiscount(), "-$8.00");
	}

	@Test(groups = { "regression" }, dataProvider = "invalidCoupons", dataProviderClass = TestDataProvider.class,
			description = "TC-CPN-03 Invalid, expired and empty coupons are rejected (data-driven)")
	public void invalidCoupons(String testCase, String couponCode, String expectedMessage) {
		addProductToCart("p1", "1");
		header.clickCart();

		step("Apply coupon '" + couponCode + "'");
		cartPage.applyCoupon(couponCode);

		Assert.assertEquals(cartPage.getCouponMessage(), expectedMessage);
		Assert.assertEquals(cartPage.getDiscount(), "-$0.00");
		Assert.assertFalse(cartPage.isCouponApplied());
	}

	@Test(groups = { "regression" }, description = "TC-CPN-04 FLAT50 gives $50 off orders of $200 or more")
	public void flat50OnQualifyingOrder() {
		addProductToCart("p12", "1"); // Coffee Maker, $210.00
		header.clickCart();

		cartPage.applyCoupon("FLAT50");

		Assert.assertEquals(cartPage.getDiscount(), "-$50.00");
		Assert.assertEquals(cartPage.getShipping(), "Free");
		Assert.assertEquals(cartPage.getTotal(), "$160.00");
	}

	/**
	 * Known defect DEF-001: the app accepts FLAT50 below $200 and shows "Coupon applied" with a $0.00 discount.
	 * This test is expected to fail until the defect is fixed.
	 */
	@Test(groups = { "regression" }, description = "TC-CPN-05 FLAT50 is rejected below $200 (known defect DEF-001)")
	public void flat50BelowMinimumRejected() {
		addProductToCart("p1", "1"); // $79.99
		header.clickCart();

		cartPage.applyCoupon("FLAT50");

		Assert.assertNotEquals(cartPage.getCouponMessage(), "Coupon applied",
				"DEF-001: FLAT50 should not be accepted on a $79.99 order");
		Assert.assertFalse(cartPage.isCouponApplied(), "DEF-001: coupon should not be applied");
	}

	@Test(groups = { "regression" }, description = "TC-CPN-06 Removing a coupon restores the total")
	public void removeCoupon() {
		addProductToCart("p1", "1");
		header.clickCart();
		cartPage.applyCoupon("SAVE10");
		cartPage.waitForTotal("$76.99");

		step("Remove the coupon");
		cartPage.removeCoupon();
		cartPage.waitForTotal("$84.99");

		Assert.assertEquals(cartPage.getDiscount(), "-$0.00");
		Assert.assertFalse(cartPage.isCouponApplied());
	}

}
