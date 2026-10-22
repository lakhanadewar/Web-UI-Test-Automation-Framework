package base;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import listeners.TestListener;
import pages.CartPage;
import pages.CheckoutAddressPage;
import pages.CheckoutPaymentPage;
import pages.CheckoutReviewPage;
import pages.HeaderSection;
import pages.LoginPage;
import pages.OrderConfirmationPage;
import pages.OrdersPage;
import pages.ProductDetailsPage;
import pages.ProductsPage;
import pages.SignupPage;
import utility.ConfigReader;
import utility.ExtentManager;

/**
 * Every test class extends this class.
 * Before each test it opens a fresh browser, resets the app data and creates the page objects.
 * After each test it closes the browser.
 */
@Listeners(TestListener.class)
public class BaseTest {

	protected WebDriver driver;

	protected HeaderSection header;
	protected LoginPage loginPage;
	protected SignupPage signupPage;
	protected ProductsPage productsPage;
	protected ProductDetailsPage productDetailsPage;
	protected CartPage cartPage;
	protected CheckoutAddressPage addressPage;
	protected CheckoutPaymentPage paymentPage;
	protected CheckoutReviewPage reviewPage;
	protected OrderConfirmationPage confirmationPage;
	protected OrdersPage ordersPage;

	/**
	 * The browser comes from the TestNG XML parameter first.
	 * If the XML has none, it comes from -Dbrowser or config.properties.
	 */
	@Parameters("browser")
	@BeforeMethod(alwaysRun = true)
	public void setUp(@Optional("") String browser) {

		String browserName = browser.isEmpty() ? ConfigReader.get("browser") : browser;

		driver = DriverFactory.createDriver(browserName);
		DriverManager.setDriver(driver);

		driver.get(ConfigReader.get("baseUrl"));
		resetAppData();

		header = new HeaderSection(driver);
		loginPage = new LoginPage(driver);
		signupPage = new SignupPage(driver);
		productsPage = new ProductsPage(driver);
		productDetailsPage = new ProductDetailsPage(driver);
		cartPage = new CartPage(driver);
		addressPage = new CheckoutAddressPage(driver);
		paymentPage = new CheckoutPaymentPage(driver);
		reviewPage = new CheckoutReviewPage(driver);
		confirmationPage = new OrderConfirmationPage(driver);
		ordersPage = new OrdersPage(driver);
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		DriverManager.quitDriver();
	}

	// ---------------- Helpers used by many tests ----------------

	/**
	 * TestKart keeps its data in the browser (localStorage / sessionStorage).
	 * Clearing both puts the stock, users and cart back to the seed data, so every test starts clean.
	 */
	protected void resetAppData() {
		((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
		driver.navigate().refresh();
	}

	/** Opens a page of the app, e.g. openPage("/cart"). */
	protected void openPage(String path) {
		driver.get(ConfigReader.get("baseUrl") + path);
	}

	protected void loginAsStandardUser() {
		openPage("/login");
		loginPage.login(ConfigReader.get("standardUser.email"), ConfigReader.get("standardUser.password"));
		header.getLoggedInUserName(); // waits until the header shows the user
	}

	/** Adds a product from the product list and waits for the cart badge to update. */
	protected void addProductToCart(String productId, String expectedCartCount) {
		productsPage.addToCart(productId);
		header.waitForCartCount(expectedCartCount);
	}

	/** Adds the standard test address used by the checkout tests. */
	protected void addDefaultAddress() {
		addressPage.addAddress("Test User", "0123456789", "1 Test Street", "London", "SW1A 1AA", "United Kingdom");
	}

	/** Writes a step into the HTML report. */
	protected void step(String message) {
		if (ExtentManager.getTest() != null) {
			ExtentManager.getTest().info(message);
		}
	}

}
