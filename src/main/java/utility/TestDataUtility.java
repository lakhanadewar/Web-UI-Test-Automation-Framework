package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Small helpers that create fresh test data at run time.
 */
public class TestDataUtility {

	/** A unique email every time, so sign-up never hits "already exists". */
	public static String uniqueEmail() {
		return "user" + System.currentTimeMillis() + Thread.currentThread().getId() + "@testkart.com";
	}

	/** A card expiry one year from today in MM/YY format, so the test never goes out of date. */
	public static String futureExpiry() {
		return LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("MM/yy"));
	}

	/** The current month in MM/YY format (boundary: should still be accepted). */
	public static String currentMonthExpiry() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yy"));
	}

	/** Last month in MM/YY format (boundary: should be rejected as expired). */
	public static String lastMonthExpiry() {
		return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
	}

}
