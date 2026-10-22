# Test Plan: TestKart Web Application

| Item | Detail |
| --- | --- |
| Document ID | TP-TESTKART-001 |
| Version | 1.0 (draft) |
| Author | Lakhan Adewar |
| Status | Draft for review |
| Structure | Based on ISO/IEC/IEEE 29119-3 and ISTQB Foundation guidance |

## 1. Introduction

This plan describes how the TestKart e-commerce web application will be tested. TestKart is a practice app with sign-up, login, a product catalogue, a cart, coupons, a three-step checkout, mock payments and order history.

The aim is to check that a shopper can complete the full journey from sign-up to order confirmation, and that the app handles invalid input, limits and errors correctly.

## 2. Test items

| Item | Version / location |
| --- | --- |
| TestKart web app (React + Vite) | Local build, `http://localhost:8080` |
| Test automation framework | This repository (Java, Selenium 4, TestNG) |

## 3. Features to be tested

| Req ID | Feature | Scenario |
| --- | --- | --- |
| REQ-01 | Login and logout | TS-01 |
| REQ-02 | Session handling and protected pages | TS-02 |
| REQ-03 | Sign-up and field validation | TS-03 |
| REQ-04 | Catalogue: list, search, filter, sort, product details, stock | TS-04 |
| REQ-05 | Cart: add, update, remove, quantity limits, shipping rule | TS-05 |
| REQ-06 | Coupons: SAVE10, FLAT50, EXPIRED20, invalid codes | TS-06 |
| REQ-07 | Checkout address: add, select, validate | TS-07 |
| REQ-08 | Mock payment: valid, declined, invalid card, expiry, CVV | TS-07 |
| REQ-09 | Order confirmation and order history | TS-08 |

Detailed scenarios and test cases are in [TEST_SCENARIOS.md](TEST_SCENARIOS.md).

## 4. Features not to be tested

- **Real payment providers.** The app only has a mock payment, so no card data leaves the browser.
- **Performance and load testing.** It's a single-user local app, so this is out of scope for this cycle.
- **Security testing**, such as penetration testing or XSS and SQL injection. A separate plan would cover these.
- **Visual design and pixel layout**, apart from making sure key elements are visible.
- **Mobile apps.** The app is web only, and mobile browser widths can be added later.
- **The "Reset demo data" button.** It's a test helper, and the framework resets data itself.

## 5. Approach

### 5.1 Test levels and types

| Level / type | What | How |
| --- | --- | --- |
| System testing (functional) | Every feature in section 3 | Automated UI tests |
| End-to-end testing | Full shopper journeys | Automated UI tests (TS-08) |
| Regression testing | Re-run after every change | Regression suite |
| Smoke testing | Critical happy paths on every build | Smoke suite |
| Cross-browser testing | Chrome, Firefox, Edge | Full-cycle suite on Selenium Grid |
| Exploratory testing | Time-boxed sessions per release | Manual, notes logged as defects |

### 5.2 Test design techniques (ISTQB)

| Technique | Where it's used |
| --- | --- |
| Equivalence partitioning | Valid and invalid emails, coupon codes, card numbers, categories |
| Boundary value analysis | Password length (7 / 8), quantity (0, 1, 10, 11), stock limit (3 / 4), phone (9 / 10 / 11 digits), CVV (2 / 3 / 4), shipping threshold ($100.00 / $112.50), card expiry (last month / this month) |
| Decision table | Coupon rules (code × order value × expiry) and shipping (subtotal over or under $100) |
| State transition | Logged out → logged in → logged out, cart empty → has items → empty, checkout steps Address → Payment → Review → Confirmed |
| Use case testing | End-to-end order journeys |
| Error guessing | Upper or lower case coupon codes, letters in phone, empty forms, opening protected URLs directly |

### 5.3 Automation approach

- **Framework.** Java 11, Selenium 4.35, TestNG 7, Maven, Page Object Model with PageFactory.
- **Locators.** Only `data-testid` attributes are used, so tests don't break when styling changes.
- **Waits.** Explicit waits only, with no `Thread.sleep()`.
- **Data.** Each test starts with a fresh browser and clears storage, which restores the seed data. Tests are independent and can run in any order or in parallel.
- **Data-driven tests.** Negative and boundary cases read their rows from `testdata/TestKartData.xlsx`.
- **Parallel runs.** Smoke and regression run test classes in parallel. The full cycle runs Chrome, Firefox and Edge at the same time.
- **Reporting.** Each run produces a timestamped Extent HTML report with screenshots of failures, plus TestNG and Surefire results for CI.

## 6. Pass and fail criteria

| Criteria | Rule |
| --- | --- |
| Test case passes | Every assertion in the test passes |
| Test case fails | Any assertion fails or an unexpected error happens |
| Smoke suite passes | 100% of smoke tests pass |
| Release ready | All smoke and P1 tests pass, there are no open Critical or High defects, and at least 95% of regression tests pass |

## 7. Suspension and resumption criteria

- **Suspend** if the app doesn't start, login is broken, or more than 30% of smoke tests fail because of the environment.
- **Resume** once a new build fixes the blocker and the smoke suite passes again.

## 8. Test deliverables

- This test plan
- Test scenarios and test cases (`TEST_SCENARIOS.md`)
- Automated test scripts (`src/test/java/tests`)
- Test data (`testdata/TestKartData.xlsx`)
- Execution reports (`reports/TestKart_Report_<timestamp>.html`)
- Defect log (section 13)
- Test summary report at the end of the cycle

## 9. Environment

| Item | Value |
| --- | --- |
| App URL (local run) | `http://localhost:8080` |
| App URL (Docker Grid run) | `http://host.docker.internal:8080` |
| Browsers | Chrome, Firefox, Edge (latest stable) |
| Grid | Selenium Grid 4.35 in Docker (`docker-compose.yml`) |
| OS | Windows 11 (local), Linux containers (Grid) |
| Tools | JDK 11+, Maven 3.9, Docker Desktop, Jenkins (optional) |

### Test data

| Data | Value |
| --- | --- |
| Standard user | `standard@testkart.com` / `Test@1234` |
| Locked user | `locked@testkart.com` / `Test@1234` |
| Coupons | `SAVE10` (10% off), `FLAT50` ($50 off orders of $200 or more), `EXPIRED20` (expired) |
| Cards | `4242 4242 4242 4242` succeeds, `4000 0000 0000 0002` is declined, any other number is invalid |
| Out-of-stock products | Bluetooth Speaker (p3), Wool Scarf (p8) |
| Low-stock product | Desk Lamp (p10), 3 in stock |

These are seeded demo accounts, not real ones.

## 10. Entry and exit criteria

**Entry**
- The app builds and runs locally with `npm run dev`.
- Test data is seeded, which happens automatically.
- The smoke suite has been run once to confirm the environment works.

**Exit**
- All planned test cases have been run.
- The pass criteria in section 6 are met.
- Open defects are logged with a severity and agreed with the product owner.

## 11. Roles

| Role | Responsibility |
| --- | --- |
| QA engineer | Plan, design, automate, run and report |
| Developer | Fix defects and support the environment |
| Product owner | Agree priorities and sign off the release |

## 12. Risks and mitigation

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Data in the browser changes between tests (stock, users) | Flaky tests | Clear localStorage and sessionStorage before every test |
| App on a local IP is not reachable from Docker | Grid runs fail | Use `host.docker.internal` and keep the URL in config |
| Browser or driver version mismatch | Tests can't start | Selenium Manager matches drivers automatically, and Grid images are pinned |
| Timing issues in a React app | Flaky tests | Explicit waits on every action, no fixed sleeps |
| Edge Docker image is amd64 only | No Edge runs on Apple Silicon | Run Edge locally or on an amd64 CI agent |
| Requirements only come from the build prompt | Gaps in expected results | Confirm unclear rules with the product owner and log them as questions |

## 13. Defect log

| ID | Title | Severity | Priority | Status | Found by |
| --- | --- | --- | --- | --- | --- |
| DEF-001 | FLAT50 is accepted on orders under $200. The app shows "Coupon applied" with a $0.00 discount instead of rejecting the code. | Medium | High | Open | TC-CPN-05 |

**DEF-001 steps to reproduce**
1. Add Wireless Headphones ($79.99) to the cart.
2. Apply coupon `FLAT50`.
3. **Expected:** the coupon is rejected with a message about the $200 minimum.
4. **Actual:** "Coupon applied" and "Coupon FLAT50 applied" appear, and the discount is -$0.00.

## 14. Schedule (example)

| Activity | Duration |
| --- | --- |
| Test planning and analysis | 1 day |
| Test design (scenarios and cases) | 1 day |
| Framework and script development | 3 days |
| Execution and defect reporting | 1 day per cycle |
| Test summary report | 0.5 day |

## 15. Approvals

| Name | Role | Date | Signature |
| --- | --- | --- | --- |
| | QA lead | | |
| | Product owner | | |
