package dataproviders;

import org.testng.annotations.DataProvider;

import utility.ExcelUtility;

/**
 * All data-driven tests read their rows from testdata/TestKartData.xlsx.
 * One sheet per feature. The first column is always the test case id.
 */
public class TestDataProvider {

	@DataProvider(name = "invalidLogin")
	public static Object[][] invalidLogin() {
		return ExcelUtility.getSheetData("InvalidLogin");
	}

	@DataProvider(name = "signupValidation")
	public static Object[][] signupValidation() {
		return ExcelUtility.getSheetData("SignupValidation");
	}

	@DataProvider(name = "productQuantity")
	public static Object[][] productQuantity() {
		return ExcelUtility.getSheetData("ProductQuantity");
	}

	@DataProvider(name = "invalidCoupons")
	public static Object[][] invalidCoupons() {
		return ExcelUtility.getSheetData("InvalidCoupons");
	}

	@DataProvider(name = "addressPhone")
	public static Object[][] addressPhone() {
		return ExcelUtility.getSheetData("AddressPhone");
	}

	@DataProvider(name = "paymentValidation")
	public static Object[][] paymentValidation() {
		return ExcelUtility.getSheetData("PaymentValidation");
	}

}
