# Test Scenarios and Test Cases: TestKart

Scenarios (TS) describe **what** to test at a high level. Each scenario is broken down into test cases (TC) that say exactly **how**, with the test design technique used to derive them.

**Legend**
- **Technique:** EP = equivalence partitioning, BVA = boundary value analysis, DT = decision table, ST = state transition, UC = use case, EG = error guessing
- **Priority:** P1 = must pass for release, P2 = important, P3 = nice to have
- **Suite:** S = smoke, R = regression. The full cycle runs everything on three browsers.
- **Data-driven rows** come from `testdata/TestKartData.xlsx`. The sheet name is shown in brackets.

---

## TS-01 Login and logout (REQ-01)

**Scenario:** A registered user can log in with valid credentials and log out. Invalid or locked accounts are refused with a clear message.

**Precondition:** the app is open and the user is logged out.

| TC ID | Title | Technique | Priority | Test data | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-LOG-01 | Valid login | EP (valid) | P1 | standard@testkart.com / Test@1234 | Lands on Products, header shows "Hi, Standard User" | S, R | `LoginTest.validLogin` |
| TC-LOG-02 | Locked account | EP (invalid) | P1 | locked@testkart.com / Test@1234 | "Your account is locked", not logged in | R | `LoginTest.lockedUserLogin` |
| TC-LOG-03a | Empty email and password | EG | P2 | blank / blank | "Email is required" | R | `LoginTest.invalidLogin` [InvalidLogin] |
| TC-LOG-03b | Empty password | EP (invalid) | P2 | standard@testkart.com / blank | "Password is required" | R | [InvalidLogin] |
| TC-LOG-03c | Wrong password | EP (invalid) | P1 | standard@testkart.com / Wrong@123 | "Invalid email or password" | R | [InvalidLogin] |
| TC-LOG-03d | Unregistered email | EP (invalid) | P2 | nobody@testkart.com / Test@1234 | "Invalid email or password" | R | [InvalidLogin] |
| TC-LOG-03e | Email without @ | EP (invalid) | P3 | standard / Test@1234 | "Enter a valid email" | R | [InvalidLogin] |
| TC-LOG-04 | Logout | ST (logged in → logged out) | P1 | standard user | Login page shown, Login link back in header | S, R | `LoginTest.logout` |

---

## TS-02 Session handling and protected pages (REQ-02)

**Scenario:** The login session behaves correctly across refreshes and tabs, and pages that need an account can't be opened when logged out.

| TC ID | Title | Technique | Priority | Steps | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-SES-01 | Session survives refresh | ST | P1 | Log in, refresh | Still "Hi, Standard User" | R | `SessionTest.sessionSurvivesRefresh` |
| TC-SES-02 | No "Remember me": new tab is logged out | ST, EG | P2 | Log in without Remember me, open a new tab | New tab shows Login link | R | `SessionTest.newTabWithoutRememberMe` |
| TC-SES-03 | "Remember me": new tab stays logged in | ST | P2 | Log in with Remember me, open a new tab | New tab shows "Hi, Standard User" | R | `SessionTest.newTabWithRememberMe` |
| TC-SES-04 | Protected page when logged out | EG | P1 | Open `/orders` directly | Redirected to `/login?redirect=%2Forders` | S, R | `SessionTest.protectedPageRedirectsToLogin` |
| TC-SES-05 | Return to the requested page after login | UC | P2 | Open `/orders`, log in | Orders page shown, "You have no orders yet" | R | `SessionTest.loginReturnsToRequestedPage` |
| TC-SES-06 | Checkout needs login | EG | P1 | Add to cart as guest, open `/checkout` | Redirected to `/login?redirect=%2Fcheckout` | R | `SessionTest.checkoutNeedsLogin` |

---

## TS-03 Sign-up (REQ-03)

**Scenario:** A new visitor can create an account. Each field is validated with a clear message.

**Rules:** all fields required, valid email format, password at least 8 characters with 1 number and 1 special character, passwords must match, email must be unique.

| TC ID | Title | Technique | Priority | Test data | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-SIG-01 | Valid sign-up, then log in | EP (valid), UC | P1 | Unique email, Test@1234 | "Account created successfully. You can now log in.", then login shows "Hi, New User" | S, R | `SignupTest.validSignupAndLogin` |
| TC-SIG-02 | Empty form | EG | P2 | All blank | Four "… is required" messages | R | `SignupTest.emptySignupForm` |
| TC-SIG-03a | Invalid email format | EP (invalid) | P2 | abc.com | "Enter a valid email" | R | `SignupTest.signupValidation` [SignupValidation] |
| TC-SIG-03b | Password 7 characters | BVA (min − 1) | P1 | Test@12 | "Password must be at least 8 characters" | R | [SignupValidation] |
| TC-SIG-03c | Password without a number | EP (invalid) | P2 | Test@abcd | "Password must contain at least 1 number" | R | [SignupValidation] |
| TC-SIG-03d | Password without a special character | EP (invalid) | P2 | Test12345 | "Password must contain at least 1 special character" | R | [SignupValidation] |
| TC-SIG-03e | Passwords don't match | EP (invalid) | P2 | Test@1234 / Test@1235 | "Passwords do not match" | R | [SignupValidation] |
| TC-SIG-03f | Email already registered | EG | P1 | standard@testkart.com | "An account with this email already exists" | R | [SignupValidation] |
| TC-SIG-03g | Name missing | EP (invalid) | P3 | blank name | "Name is required" | R | [SignupValidation] |
| TC-SIG-04 | Password exactly 8 characters | BVA (min) | P1 | Test@123 | Account created | R | `SignupTest.passwordMinimumLengthAccepted` |

---

## TS-04 Product catalogue (REQ-04)

**Scenario:** A shopper can browse, search, filter and sort products, see product details, and can't buy items that are out of stock.

| TC ID | Title | Technique | Priority | Test data | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-CAT-01 | All products listed | EP | P1 | none | "12 products", 12 cards | S, R | `ProductCatalogueTest.allProductsListed` |
| TC-CAT-02 | Search ignores case | EP, EG | P2 | "mug", "MUG" | Only Ceramic Mug, "1 products" | R | `ProductCatalogueTest.searchIsCaseInsensitive` |
| TC-CAT-03 | Search with no match | EP (invalid) | P2 | "zzzz" | "No products found", "0 products" | R | `ProductCatalogueTest.searchNoResults` |
| TC-CAT-04 | Filter by category | EP | P2 | Clothing | Cotton T-Shirt, Denim Jacket, Running Shoes, Wool Scarf | R | `ProductCatalogueTest.filterByCategory` |
| TC-CAT-05 | Sort price low to high | EP | P3 | asc | Prices ascending | R | `ProductCatalogueTest.sortPriceLowToHigh` |
| TC-CAT-06 | Sort price high to low | EP | P3 | desc | Prices descending | R | `ProductCatalogueTest.sortPriceHighToLow` |
| TC-CAT-07 | Out-of-stock can't be added | EP (invalid) | P1 | p3, p8 | Button disabled, reads "Out of stock" | S, R | `ProductCatalogueTest.outOfStockProductsDisabled` |
| TC-CAT-08 | Product details | EP | P2 | p10 | Desk Lamp, $39.99, "In stock: 3" | R | `ProductCatalogueTest.productDetails` |

---

## TS-05 Cart (REQ-05)

**Scenario:** A shopper can add, update and remove items. Quantity limits (1 to 10 per item, never more than the stock) and the shipping rule (free over $100, otherwise $5) are applied.

| TC ID | Title | Technique | Priority | Test data | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-CRT-01 | Add product to cart | EP, ST (empty → has items) | P1 | p1 ($79.99) | Count 1, subtotal $79.99, shipping $5.00, total $84.99 | S, R | `CartTest.addProductToCart` |
| TC-CRT-02 | Increase quantity | EP | P2 | p1 × 2 | Line total $159.98, count 2, shipping Free | R | `CartTest.increaseQuantity` |
| TC-CRT-03 | Quantity minimum is 1 | BVA (min) | P2 | p1 × 1 | − button disabled, quantity stays 1 | R | `CartTest.quantityMinimumIsOne` |
| TC-CRT-04 | Quantity maximum is 10 | BVA (max) | P2 | p9 × 10 | + button disabled at 10 | R | `CartTest.quantityMaximumIsTen` |
| TC-CRT-05a | Quantity 0 | BVA (min − 1) | P2 | p9, 0 | "Quantity must be at least 1" | R | `CartTest.productQuantityRules` [ProductQuantity] |
| TC-CRT-05b | Quantity 1 | BVA (min) | P2 | p9, 1 | "Added to cart" | R | [ProductQuantity] |
| TC-CRT-05c | Quantity 10 | BVA (max) | P2 | p9, 10 | "Added to cart" | R | [ProductQuantity] |
| TC-CRT-05d | Quantity 11 | BVA (max + 1) | P2 | p9, 11 | "Maximum quantity is 10" | R | [ProductQuantity] |
| TC-CRT-05e | Quantity equal to stock | BVA (stock) | P1 | p10, 3 | "Added to cart" | R | [ProductQuantity] |
| TC-CRT-05f | Quantity above stock | BVA (stock + 1) | P1 | p10, 4 | "Maximum quantity is 3" | R | [ProductQuantity] |
| TC-CRT-06 | Remove last item | ST (has items → empty) | P2 | p1 | "Your cart is empty", count 0 | R | `CartTest.removeProduct` |
| TC-CRT-07 | Free shipping boundary | BVA, DT | P1 | 8 × $12.50 = $100.00, then 9 = $112.50 | $100.00 pays $5.00 (total $105.00), $112.50 ships Free | R | `CartTest.freeShippingBoundary` |

---

## TS-06 Coupons (REQ-06)

**Scenario:** Valid coupons reduce the total correctly, and invalid, expired or ineligible coupons are rejected with a clear message. Only one coupon at a time.

**Decision table (coupon rules)**

| Rule | 1 | 2 | 3 | 4 | 5 | 6 |
| --- | --- | --- | --- | --- | --- | --- |
| Code | SAVE10 | FLAT50 | FLAT50 | EXPIRED20 | unknown | blank |
| Order ≥ $200 | any | yes | no | any | any | any |
| **Outcome** | 10% off | $50 off | Rejected | "Coupon expired" | "Invalid coupon" | "Enter a coupon code" |
| Test case | TC-CPN-01 | TC-CPN-04 | TC-CPN-05 | TC-CPN-03b | TC-CPN-03a | TC-CPN-03c |

| TC ID | Title | Technique | Priority | Test data | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-CPN-01 | SAVE10 gives 10% off | DT | P1 | p1 + SAVE10 | Discount -$8.00, total $76.99 | S, R | `CouponTest.save10Coupon` |
| TC-CPN-02 | Lower-case code | EG | P3 | save10 | Accepted, -$8.00 | R | `CouponTest.couponCodeLowerCase` |
| TC-CPN-03a | Unknown code | DT, EP (invalid) | P2 | BOGUS | "Invalid coupon" | R | `CouponTest.invalidCoupons` [InvalidCoupons] |
| TC-CPN-03b | Expired code | DT | P2 | EXPIRED20 | "Coupon expired" | R | [InvalidCoupons] |
| TC-CPN-03c | Empty code | EG | P3 | blank | "Enter a coupon code" | R | [InvalidCoupons] |
| TC-CPN-04 | FLAT50 on a qualifying order | DT | P1 | p12 ($210) + FLAT50 | -$50.00, shipping Free, total $160.00 | R | `CouponTest.flat50OnQualifyingOrder` |
| TC-CPN-05 | FLAT50 below $200 | DT, BVA | P1 | p1 ($79.99) + FLAT50 | Coupon rejected. **Fails today, see DEF-001** | R | `CouponTest.flat50BelowMinimumRejected` |
| TC-CPN-06 | Remove coupon | ST | P2 | SAVE10, then remove | Discount -$0.00, total back to $84.99 | R | `CouponTest.removeCoupon` |

---

## TS-07 Checkout address and payment (REQ-07, REQ-08)

**Scenario:** A logged-in shopper must give a valid delivery address and valid card details before they can review the order. Errors are shown next to the field.

**Precondition:** logged in as the standard user, with Wireless Headphones in the cart.

| TC ID | Title | Technique | Priority | Test data | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-CHK-01 | Continue without an address | EG | P1 | none | "Please select a delivery address" | R | `CheckoutTest.continueWithoutAddress` |
| TC-CHK-02 | Empty address form | EG | P2 | all blank | Six "… is required" messages | R | `CheckoutTest.emptyAddressForm` |
| TC-CHK-03a | Phone 9 digits | BVA (10 − 1) | P2 | 123456789 | "Phone must be exactly 10 digits" | R | `CheckoutTest.phoneValidation` [AddressPhone] |
| TC-CHK-03b | Phone 11 digits | BVA (10 + 1) | P2 | 01234567890 | "Phone must be exactly 10 digits" | R | [AddressPhone] |
| TC-CHK-03c | Letters in phone | EP (invalid) | P3 | 01234abcde | "Phone must be exactly 10 digits" | R | [AddressPhone] |
| TC-CHK-03d | Empty phone | EP (invalid) | P3 | blank | "Phone is required" | R | [AddressPhone] |
| TC-CHK-04 | Valid address saved and selected | EP (valid), BVA (10 digits) | P1 | 0123456789 | Address shown and selected | S, R | `CheckoutTest.saveValidAddress` |
| TC-CHK-05a | Empty card fields | EG | P2 | all blank | "Card number is required" | R | `CheckoutTest.paymentValidation` [PaymentValidation] |
| TC-CHK-05b | Unknown card number | EP (invalid) | P1 | 1234 5678 9012 3456 | "Invalid card" | R | [PaymentValidation] |
| TC-CHK-05c | Declined card | EP (invalid) | P1 | 4000 0000 0000 0002 | "Payment declined" | R | [PaymentValidation] |
| TC-CHK-05d | Expired card | EP (invalid) | P1 | 01/20 | "Card has expired" | R | [PaymentValidation] |
| TC-CHK-05e | Month 13 | BVA (12 + 1) | P3 | 13/30 | "Expiry must be MM/YY" | R | [PaymentValidation] |
| TC-CHK-05f | CVV 2 digits | BVA (3 − 1) | P2 | 12 | "CVV must be 3 digits" | R | [PaymentValidation] |
| TC-CHK-05g | CVV 4 digits | BVA (3 + 1) | P2 | 1234 | "CVV must be 3 digits" | R | [PaymentValidation] |
| TC-CHK-06 | Card expiring this month | BVA (on boundary) | P2 | current MM/YY | Review step opens | R | `CheckoutTest.currentMonthExpiryAccepted` |
| TC-CHK-07 | Card expired last month | BVA (boundary − 1) | P2 | last month MM/YY | "Card has expired" | R | `CheckoutTest.lastMonthExpiryRejected` |

---

## TS-08 End-to-end order journeys (REQ-09)

**Scenario:** A shopper completes a purchase from start to finish, and the order is confirmed, stored in the history and reduces the stock.

| TC ID | Title | Technique | Priority | Steps | Expected result | Suite | Automated in |
| --- | --- | --- | --- | --- | --- | --- | --- |
| TC-E2E-01 | Order with a coupon | UC, ST | P1 | Log in, add p1, apply SAVE10, add an address, pay with 4242 card, review, place order, open order history | Review shows "Card ending 4242" and $76.99. Confirmation reads "Thank you! Your order is confirmed.", cart is 0, and the order is listed at $76.99 | S, R | `EndToEndTest.placeOrderWithCoupon` |
| TC-E2E-02 | New user, free shipping | UC | P1 | Sign up, log in, add p12, apply FLAT50, check out | Order confirmed at $160.00 | R | `EndToEndTest.newUserOrderWithFreeShipping` |
| TC-E2E-03 | Order reduces stock | UC, ST | P2 | Note p1 stock (15), order 1 | Stock shows "In stock: 14" | R | `EndToEndTest.orderReducesStock` |

---

## Coverage summary

| Scenario | Test cases (incl. data rows) | Smoke | Automated |
| --- | --- | --- | --- |
| TS-01 Login | 8 | 2 | 8 |
| TS-02 Session | 6 | 1 | 6 |
| TS-03 Sign-up | 10 | 1 | 10 |
| TS-04 Catalogue | 8 | 2 | 8 |
| TS-05 Cart | 12 | 1 | 12 |
| TS-06 Coupons | 8 | 1 | 8 |
| TS-07 Checkout | 16 | 1 | 16 |
| TS-08 End to end | 3 | 1 | 3 |
| **Total** | **71** | **10** | **71** |

Each run of the full cycle executes these on Chrome, Firefox and Edge, so 213 test runs in all.
