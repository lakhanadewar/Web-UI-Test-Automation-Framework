# TestKart

A simple e-commerce web app built as a practice target for UI test automation.
Runs fully offline: all data is stored in the browser's localStorage. No backend, captcha, third-party login or real payments.

## Run locally

```bash
npm install
npm run dev       # start dev server
npm run build     # production build
npm run preview   # preview the production build
```

Use the "Reset demo data" button in the footer (`data-testid="reset-demo-data"`) to restore the seed data and log out.

## Test users

| Email                   | Password    | Behaviour                                   |
| ----------------------- | ----------- | ------------------------------------------- |
| standard@testkart.com   | Test@1234   | Normal user                                 |
| locked@testkart.com     | Test@1234   | Login fails with "Your account is locked"   |

Wrong credentials show "Invalid email or password".
Without "Remember me" the session lasts for the browser tab (max 24h); with "Remember me" it lasts 7 days.

## Sign-up rules

- Name, email, password, confirm password are required
- Email must be a valid format
- Password: min 8 characters, at least 1 number and 1 special character
- Passwords must match
- Duplicate email shows "An account with this email already exists"

## Products (seed data)

| ID  | Name                | Category    | Price   | Stock |
| --- | ------------------- | ----------- | ------- | ----- |
| p1  | Wireless Headphones | Electronics | 79.99   | 15    |
| p2  | Smart Watch         | Electronics | 149.00  | 8     |
| p3  | Bluetooth Speaker   | Electronics | 45.50   | 0 (out of stock) |
| p4  | USB-C Charger       | Electronics | 19.99   | 30    |
| p5  | Cotton T-Shirt      | Clothing    | 15.00   | 50    |
| p6  | Denim Jacket        | Clothing    | 89.00   | 5     |
| p7  | Running Shoes       | Clothing    | 120.00  | 12    |
| p8  | Wool Scarf          | Clothing    | 25.00   | 0 (out of stock) |
| p9  | Ceramic Mug         | Home        | 12.50   | 40    |
| p10 | Desk Lamp           | Home        | 39.99   | 3     |
| p11 | Throw Pillow        | Home        | 22.00   | 20    |
| p12 | Coffee Maker        | Home        | 210.00  | 6     |

## Cart rules

- Quantity per item: min 1, max 10, and never more than stock
- Out-of-stock products cannot be added
- Empty cart shows "Your cart is empty"

## Coupons (one at a time, removable)

| Code       | Result                                   |
| ---------- | ---------------------------------------- |
| SAVE10     | 10% off subtotal                         |
| FLAT50     | 50 off when subtotal is 200 or more      |
| EXPIRED20  | Error: "Coupon expired"                  |
| any other  | Error: "Invalid coupon"                  |

## Shipping

Free when subtotal is over 100, otherwise 5.
Total = subtotal − discount + shipping.

## Address validation

Full name, phone, address line, city, postcode and country are required.
Phone must be exactly 10 digits. Postcode: 3–10 letters, digits, spaces or hyphens.

## Mock payment cards

| Card number           | Result                        |
| --------------------- | ----------------------------- |
| 4242 4242 4242 4242   | Payment succeeds              |
| 4000 0000 0000 0002   | Error: "Payment declined"     |
| any other number      | Error: "Invalid card"         |

- Expiry: format MM/YY, must not be in the past (e.g. `12/30`)
- CVV: exactly 3 digits (e.g. `123`)

## Pages

| Path           | Login required |
| -------------- | -------------- |
| `/`            | No — product list, search, category filter, price sort |
| `/product/:id` | No — product detail |
| `/cart`        | No |
| `/login`, `/signup` | No |
| `/checkout`    | Yes — 3 steps: Address, Payment, Review |
| `/orders`      | Yes — order history |
| `/orders/:id`  | Yes — order confirmation |
| `/profile`     | Yes — profile and addresses |

Protected pages redirect to `/login?redirect=...` when logged out.

## Common test IDs

- Login: `login-email`, `login-password`, `login-remember`, `login-submit`, `login-error`
- Sign up: `signup-name`, `signup-email`, `signup-password`, `signup-confirm`, `signup-submit`, `signup-error`, `signup-success`, field errors `signup-<field>-error`
- Header: `nav-home`, `nav-products`, `nav-cart`, `cart-count`, `nav-orders`, `nav-profile`, `nav-login`, `nav-signup`, `logout-button`
- Catalogue: `search-input`, `category-filter`, `sort-select`, `result-count`, `no-results`, `product-card-<id>`, `product-name-<id>`, `product-price-<id>`, `product-stock-<id>`, `add-to-cart-<id>`, `add-to-cart-message-<id>`
- Product detail: `product-detail-name`, `product-detail-price`, `product-detail-stock`, `product-detail-qty`, `product-detail-add-to-cart`, `product-detail-message`
- Cart: `cart-empty`, `cart-item-<id>`, `cart-qty-<id>`, `cart-increase-<id>`, `cart-decrease-<id>`, `cart-remove-<id>`, `cart-line-total-<id>`, `checkout-button`
- Coupon: `coupon-input`, `coupon-apply`, `coupon-message`, `coupon-applied`, `coupon-remove`
- Totals: `summary-subtotal`, `summary-discount`, `summary-shipping`, `summary-total`
- Address: `address-fullName`, `address-phone`, `address-line`, `address-city`, `address-postcode`, `address-country`, `address-save`, `address-add`, `address-select-<id>`, `address-edit-<id>`, errors `address-<field>-error`
- Checkout: `checkout-step-address`, `checkout-step-payment`, `checkout-step-review`, `checkout-error`, `checkout-address-continue`, `payment-card`, `payment-expiry`, `payment-cvv`, `payment-continue`, `payment-error`, `place-order`
- Orders: `order-id`, `order-total`, `orders-list`, `orders-empty`, `order-row-<id>`
- Footer: `reset-demo-data`
