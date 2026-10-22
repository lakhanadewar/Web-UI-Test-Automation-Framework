package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * Cart page: quantities, coupons and the order summary.
 */
public class CartPage extends BasePage {

	@FindBy(css = "[data-testid='cart-title']")
	private WebElement pageTitle;

	@FindBy(css = "[data-testid='cart-empty']")
	private WebElement emptyCartMessage;

	@FindBy(css = "[data-testid='coupon-input']")
	private WebElement couponInput;

	@FindBy(css = "[data-testid='coupon-apply']")
	private WebElement applyCouponButton;

	@FindBy(css = "[data-testid='coupon-message']")
	private WebElement couponMessage;

	@FindBy(css = "[data-testid='coupon-applied']")
	private WebElement appliedCoupon;

	@FindBy(css = "[data-testid='coupon-remove']")
	private WebElement removeCouponButton;

	@FindBy(css = "[data-testid='summary-subtotal']")
	private WebElement subtotal;

	@FindBy(css = "[data-testid='summary-discount']")
	private WebElement discount;

	@FindBy(css = "[data-testid='summary-shipping']")
	private WebElement shipping;

	@FindBy(css = "[data-testid='summary-total']")
	private WebElement total;

	@FindBy(css = "[data-testid='checkout-button']")
	private WebElement checkoutButton;

	public CartPage(WebDriver driver) {
		super(driver);
	}

	public boolean isCartPageDisplayed() {
		return isDisplayed(pageTitle);
	}

	// ---------- Cart items ----------

	public boolean isProductInCart(String productId) {
		return isDisplayed(testId("cart-item-" + productId));
	}

	public String getQuantity(String productId) {
		return find(testId("cart-qty-" + productId)).getAttribute("value");
	}

	public void increaseQuantity(String productId) {
		click(testId("cart-increase-" + productId));
	}

	public void decreaseQuantity(String productId) {
		click(testId("cart-decrease-" + productId));
	}

	public boolean isIncreaseEnabled(String productId) {
		return isEnabled(testId("cart-increase-" + productId));
	}

	public boolean isDecreaseEnabled(String productId) {
		return isEnabled(testId("cart-decrease-" + productId));
	}

	/** Waits until the quantity box shows the expected number. */
	public void waitForQuantity(String productId, String expected) {
		wait.until(d -> expected.equals(getQuantity(productId)));
	}

	public String getLineTotal(String productId) {
		return getText(testId("cart-line-total-" + productId));
	}

	public void removeProduct(String productId) {
		click(testId("cart-remove-" + productId));
	}

	public String getEmptyCartMessage() {
		return getText(emptyCartMessage);
	}

	// ---------- Coupons ----------

	public void applyCoupon(String code) {
		type(couponInput, code);
		click(applyCouponButton);
	}

	public String getCouponMessage() {
		return getText(couponMessage);
	}

	public String getAppliedCoupon() {
		return getText(appliedCoupon);
	}

	/** Checks right now (no wait). Read the coupon message first so the page has updated. */
	public boolean isCouponApplied() {
		return isPresentNow(testId("coupon-applied"));
	}

	public void removeCoupon() {
		click(removeCouponButton);
	}

	// ---------- Order summary ----------

	public String getSubtotal() {
		return getText(subtotal);
	}

	public String getDiscount() {
		return getText(discount);
	}

	public String getShipping() {
		return getText(shipping);
	}

	public String getTotal() {
		return getText(total);
	}

	/** Waits for the total to update after a change, e.g. "$76.99". */
	public void waitForTotal(String expected) {
		waitForTextInElement(total, expected);
	}

	public void clickProceedToCheckout() {
		click(checkoutButton);
	}

}
