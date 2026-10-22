# TestKart Selenium Framework

A UI test automation framework for **TestKart**, a small e-commerce web app, built with **Java, Selenium 4, TestNG and Maven** using the **Page Object Model**.

It covers sign-up, login, session handling, catalogue, cart, coupons, checkout, mock payments and order history. Everything can run locally or cross-browser on **Selenium Grid in Docker**, with smoke, regression and full-cycle suites running in parallel.

## Tech stack

| Area | Tools |
| --- | --- |
| Language | Java 11+ |
| UI automation | Selenium WebDriver 4.35 (Selenium Manager handles drivers) |
| Test runner | TestNG 7 (groups, parameters, data providers, parallel runs) |
| Build | Maven |
| Test data | Excel via Apache POI |
| Reporting | Extent Reports 5 (HTML report, screenshots on failure) |
| Cross-browser | Chrome, Firefox, Edge on Selenium Grid (Docker Compose) |
| CI | Jenkins pipeline |

## Project structure

```
├── src/main/java
│   ├── base/
│   │   ├── BasePage.java          common actions with explicit waits (click, type, getText…)
│   │   ├── DriverFactory.java     creates Chrome / Firefox / Edge, locally or on the Grid
│   │   └── DriverManager.java     one WebDriver per thread for parallel runs
│   ├── pages/                     one class per screen (PageFactory @FindBy)
│   │   ├── HeaderSection, LoginPage, SignupPage, ProductsPage, ProductDetailsPage
│   │   ├── CartPage, CheckoutAddressPage, CheckoutPaymentPage, CheckoutReviewPage
│   │   └── OrderConfirmationPage, OrdersPage
│   └── utility/
│       ├── ConfigReader.java      reads config.properties, -D values win
│       ├── ExcelUtility.java      reads Excel sheets for data providers
│       ├── ExtentManager.java     builds the HTML report
│       ├── ScreenshotUtility.java
│       └── TestDataUtility.java   unique emails, card expiry dates
├── src/main/resources/config.properties
├── src/test/java
│   ├── base/BaseTest.java         opens the browser, resets app data, creates page objects
│   ├── dataproviders/TestDataProvider.java
│   ├── listeners/TestListener.java   writes results and screenshots to the report
│   └── tests/                     LoginTest, SessionTest, SignupTest, ProductCatalogueTest,
│                                  CartTest, CouponTest, CheckoutTest, EndToEndTest
├── suites/                        testng-smoke.xml, testng-regression.xml, testng-full.xml
├── testdata/TestKartData.xlsx     data-driven test rows, one sheet per feature
├── docker-compose.yml             Selenium Grid: hub + Chrome + Firefox + Edge
├── Jenkinsfile
├── TEST_PLAN.md
└── TEST_SCENARIOS.md
```

## How it works

1. **`BaseTest`** runs before every test. It opens a browser through `DriverFactory`, opens the app, clears localStorage and sessionStorage so the seed data comes back, and creates the page objects.
2. **Page classes** hold only locators and page actions. All locators use `data-testid`, and `BasePage` waits for every element, so there's no `Thread.sleep()`.
3. **Tests** read like steps: `loginPage.login(...)`, `cartPage.applyCoupon("SAVE10")`, then assertions.
4. **`TestListener`** writes every result into the Extent report and attaches a screenshot when a test fails.

## Prerequisites

- JDK 11 or newer, and Maven 3.9+
- Chrome, Firefox and/or Edge installed (for local runs)
- Docker Desktop (for Grid runs)
- The TestKart app running locally:
  ```
  npm install
  npm run dev        # serves on http://localhost:8080
  ```

## Running the tests

All settings live in `src/main/resources/config.properties`. You can override any of them with `-D`.

| What | Command |
| --- | --- |
| Smoke, Chrome, local | `mvn clean test` |
| Smoke on Firefox | `mvn clean test -Dbrowser=firefox` |
| Regression | `mvn clean test -Dsuite=testng-regression.xml` |
| Regression, headless Edge | `mvn clean test -Dsuite=testng-regression.xml -Dbrowser=edge -Dheadless=true` |
| Different app URL | `mvn clean test -DbaseUrl=http://192.168.56.1:8080` |

### Cross-browser on Selenium Grid (Docker)

```
docker compose up -d                       # start hub + Chrome, Firefox, Edge nodes
# Grid console: http://localhost:4444

mvn clean test -Dsuite=testng-full.xml -DrunMode=grid -DbaseUrl=http://host.docker.internal:8080

docker compose down
```

The browsers run inside Docker, so they reach the app on your machine through `host.docker.internal`. If the app is only reachable on `localhost`, start it with its host set to `0.0.0.0`.

### Suites

| Suite | What runs | Parallel |
| --- | --- | --- |
| `testng-smoke.xml` | 10 critical tests (`smoke` group) | 3 classes at a time |
| `testng-regression.xml` | All 71 test cases (`regression` group) | 4 classes at a time |
| `testng-full.xml` | All 71 test cases on Chrome, Firefox and Edge (213 runs) | 3 browsers at a time |

## Reports

- **Extent HTML report:** `reports/TestKart_Report_<date_time>.html`, with steps, test data, browser, and screenshots of failures
- **TestNG / Surefire:** `target/surefire-reports/`

## Test data

- Seeded users, coupons and test cards are listed in `TEST_PLAN.md`. They are demo data, not real accounts.
- Negative and boundary cases are in `testdata/TestKartData.xlsx`. Add a row to add a test case, with no code changes.

## Known defect

`CouponTest.flat50BelowMinimumRejected` fails on purpose. It documents **DEF-001**: FLAT50 is accepted on orders under $200. See `TEST_PLAN.md`, section 13.

## Adding a new test

1. If it's a new screen, add a page class in `pages/` that extends `BasePage`.
2. Add a test method in the right class in `tests/`, extending `BaseTest`.
3. Tag it with `groups = { "regression" }`, adding `"smoke"` if it's critical.
4. Add the test case to `TEST_SCENARIOS.md`.
