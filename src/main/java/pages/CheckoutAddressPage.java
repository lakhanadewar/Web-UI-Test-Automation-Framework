package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

/**
 * Checkout step 1: add, select or edit a delivery address.
 */
public class CheckoutAddressPage extends BasePage {

	@FindBy(css = "[data-testid='checkout-title']")
	private WebElement pageTitle;

	@FindBy(css = "[data-testid='address-fullName']")
	private WebElement fullNameInput;

	@FindBy(css = "[data-testid='address-phone']")
	private WebElement phoneInput;

	@FindBy(css = "[data-testid='address-line']")
	private WebElement addressLineInput;

	@FindBy(css = "[data-testid='address-city']")
	private WebElement cityInput;

	@FindBy(css = "[data-testid='address-postcode']")
	private WebElement postcodeInput;

	@FindBy(css = "[data-testid='address-country']")
	private WebElement countryInput;

	@FindBy(css = "[data-testid='address-save']")
	private WebElement saveAddressButton;

	@FindBy(css = "[data-testid='address-add']")
	private WebElement addNewAddressButton;

	@FindBy(css = "[data-testid='checkout-address-continue']")
	private WebElement continueToPaymentButton;

	@FindBy(css = "[data-testid='checkout-error']")
	private WebElement checkoutError;

	@FindBy(css = "[data-testid='address-book']")
	private WebElement addressBook;

	public CheckoutAddressPage(WebDriver driver) {
		super(driver);
	}

	public boolean isCheckoutPageDisplayed() {
		return isDisplayed(pageTitle);
	}

	public void fillAddress(String fullName, String phone, String addressLine, String city, String postcode,
			String country) {
		type(fullNameInput, fullName);
		type(phoneInput, phone);
		type(addressLineInput, addressLine);
		type(cityInput, city);
		type(postcodeInput, postcode);
		type(countryInput, country);
	}

	public void clickSaveAddress() {
		click(saveAddressButton);
	}

	/** Fills the form and saves it. */
	public void addAddress(String fullName, String phone, String addressLine, String city, String postcode,
			String country) {
		fillAddress(fullName, phone, addressLine, city, postcode, country);
		clickSaveAddress();
	}

	public void clickAddNewAddress() {
		click(addNewAddressButton);
	}

	public void clickContinueToPayment() {
		click(continueToPaymentButton);
	}

	public String getCheckoutError() {
		return getText(checkoutError);
	}

	/** Reads a field error, e.g. fieldName "phone" reads address-phone-error. */
	public String getFieldError(String fieldName) {
		return getText(testId("address-" + fieldName + "-error"));
	}

	public String getAddressBookText() {
		return getText(addressBook);
	}

	/** True when a saved address is shown with its radio button selected. */
	public boolean isAnAddressSelected() {
		return isDisplayed(By.cssSelector("[data-testid^='address-select-']:checked"));
	}

}
