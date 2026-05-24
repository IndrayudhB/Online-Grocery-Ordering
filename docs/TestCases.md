# Functional & Integration Test Cases — Online Grocery Ordering

Manual test plan covering all user stories from `sprint_3.pdf` (US001–US013) and
`web_proj.pdf` (US_Web_001–US_Web_005). Run each scenario against the deployed
app, fill in the **Actual Output** and **Pass/Fail** columns, and capture
screenshots as evidence.

| Field | Notes |
|---|---|
| Environment | Apache Tomcat 9.0.71, Eclipse, SQLite (`C:/Users/2873120/MySQLiteDB`) |
| Base URL | `http://localhost:8080/Online_Grocery_Ordering/` |
| Super-admin | `root@grocery.local` / `root` (seeded on first start) |
| Browser | Microsoft Edge / Chrome |
| Tester | _________________ |
| Date | _________________ |

> **How to use this document:** start with TC001, work top-to-bottom, fill the
> rightmost two columns, and reset the database between independent flows by
> stopping Tomcat, deleting the SQLite file, and restarting Tomcat.

---

## Section A — Functional Test Cases (US015)

### US001 — Customer Registration

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC001 | Register a new customer with valid data | App is running, email not in DB | 1. Open `/register.jsp` 2. Fill all fields 3. Click *Register* | Name: `Indrayudh Banerjee`, Email: `indra@test.com`, Contact: `9999999999`, Address: `Kolkata`, Password: `pass123` | Forwarded to `/login.jsp` with green banner "Registration successful! Please log in." Row added to both `login` and `customer` tables. | | |
| TC002 | Duplicate email | TC001 already executed | Repeat TC001 with same email | Email: `indra@test.com` (already used) | Page reloads on `/register.jsp` with red banner "An account already exists for email indra@test.com". No new DB row. | | |
| TC003 | Invalid email format | App running | Submit registration with malformed email | Email: `not-an-email` | Red banner "Email format is invalid: not-an-email". Form not submitted to DB. | | |
| TC004 | Empty required fields | App running | Submit form with empty name | Name: `(empty)`, others valid | Red banner "Name, email and password are required." | | |
| TC005 | Password stored hashed | TC001 executed | Open SQLite via Eclipse Data Source Explorer, run `SELECT password FROM login WHERE email = 'indra@test.com'` | — | Password is a 64-character hex SHA-256 hash, **not** the plaintext `pass123`. | | |

### US002 — Administrator Registration

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC006 | Super-admin seeded automatically | First run, fresh DB | 1. Stop Tomcat 2. Delete `MySQLiteDB` file 3. Start Tomcat 4. Inspect `login` table | — | One row exists with `email='root@grocery.local'`, `userType='admin'`, `status='Active'`. | | |
| TC007 | Add new admin via admin panel | Logged in as root admin | 1. Navigate to *Add Admin* 2. Submit form | Email: `admin2@test.com`, Password: `admin2` | Green banner "Admin 'admin2@test.com' created successfully." New row in `login` with `userType='admin'`. | | |
| TC008 | Add admin link hidden from customers | Logged in as customer | View customer navbar | — | The navbar shows Home / Catalog / Cart / My Orders / Profile only. *Add Admin* link is **not** visible. | | |
| TC009 | Add admin with duplicate email | TC007 done | Try to add same admin email again | Email: `admin2@test.com` | Red banner "An account already exists for admin2@test.com". | | |

### US003 — User Login

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC010 | Valid customer login | TC001 done | 1. Open `/login.jsp` 2. Submit valid credentials | Email: `indra@test.com`, Password: `pass123` | Redirected to `/customer/index.jsp`. Navbar shows "Hi, Indrayudh Banerjee". | | |
| TC011 | Valid admin login | App running | Submit root admin credentials | Email: `root@grocery.local`, Password: `root` | Redirected to `/admin/index.jsp`. Admin-themed navbar with "Admin: root@grocery.local". | | |
| TC012 | Wrong password | TC001 done | Submit valid email + wrong password | Email: `indra@test.com`, Password: `wrong` | Stays on `/login.jsp`. Red banner "Invalid email or password." | | |
| TC013 | Non-existent email | App running | Submit unknown email | Email: `ghost@test.com`, Password: `anything` | Red banner "Invalid email or password." | | |
| TC014 | Invalid email format | App running | Submit malformed email | Email: `notanemail` | Red banner "Email format is invalid." | | |
| TC015 | Inactive customer cannot log in | TC038 (deactivate) done | Try to log in as deactivated customer | Email: `indra@test.com`, Password: `pass123` | Red banner "Your account is deactivated. Please reactivate it first." Reactivation form appears. | | |
| TC016 | Admin always logs in even if Inactive | Manually mark admin as Inactive in DB | Login as that admin | Email: `admin2@test.com`, Password: `admin2` | Login succeeds, redirects to `/admin/index.jsp`. (Admins are never blocked by status.) | | |
| TC017 | Direct URL access without session | Logged out | Paste `/customer/catalog` in browser | — | Redirected to `/login.jsp?expired=1` with banner "Please sign in to continue." | | |

### US004 — Product Registration

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC018 | Add valid product (single) | Logged in as admin | 1. Navigate to *Add Product* 2. Fill form 3. Submit | Name: `Apples 1kg`, Description: `Fresh red apples`, Brand: `Local`, Price: `120.00`, Stock: `50` | Green banner "Product created successfully (id=N)". Form clears. New row in `product` table. | | |
| TC019 | Empty product name | Admin logged in | Submit with empty name | Name: `(empty)`, others valid | Red banner "Product name must not be empty." No DB insert. | | |
| TC020 | Empty description | Admin logged in | Submit with empty description | Description: `(empty)`, others valid | Red banner "Product description must not be empty." | | |
| TC021 | Price exceeds 100000 | Admin logged in | Submit with price > 100000 | Price: `150000.00` | Red banner "Price must be between 0 and 100000.0." | | |
| TC022 | Negative stock | Admin logged in | Submit with negative stock | Stock: `-5` | Browser blocks via HTML5 `min=0`, OR server returns "Stock availability must be >= 0." | | |
| TC023 | Bulk CSV upload — happy path | Admin logged in, `sample-products.csv` in repo | 1. Navigate to *Bulk Upload* 2. Choose `sample-products.csv` 3. Click *Upload* | File: `sample-products.csv` (10 rows) | Green banner "10 product(s) imported successfully." 10 new rows in `product` table. | | |
| TC024 | Bulk CSV with one bad row rolls back | Admin logged in | Upload a CSV where one row has price > 100000 | CSV with bad row | Red banner mentioning rollback. **Zero** rows inserted from this upload (entire batch rolled back). | | |
| TC025 | Bulk upload empty file | Admin logged in | Upload empty file | File: empty CSV | Red banner "Please choose a CSV file." or "CSV had no data rows." | | |

### US005 — View Product Catalog

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC026 | Customer sees all products | TC023 done; logged in as customer | Navigate to *Catalog* | — | Table lists all 10 imported products with id / name / description / brand / price / stock / Add button. | | |
| TC027 | Search by product name | Catalog populated | Type `rice` in search box, click *Search* | Search term: `rice` | Table is filtered, showing only rows whose `product_name` contains "rice" (case-insensitive). | | |
| TC028 | Empty search returns all | Catalog populated | Submit search with empty box | — | All products shown, same as TC026. | | |
| TC029 | Catalog when DB is empty | Fresh DB, no products | Login as customer, open Catalog | — | Page shows "No products available right now." No exceptions. | | |

### US006 — Add Products to Cart

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC030 | Add product to cart with valid quantity | Customer logged in, products exist | On Catalog row, set quantity, click *Add* | Quantity: `2` | Redirected back to Catalog. Green flash "Product added to wishlist." Row in `wishlist` table for that user/product. | | |
| TC031 | Add same product twice (increment) | TC030 done | Click *Add* again on same product | Quantity: `1` | Quantity in `wishlist` increments to `3` (2 + 1). Only one row for that product. | | |
| TC032 | Out-of-stock product cannot be added | Product with `stock_availability = 0` | Open Catalog | — | "Out of stock" label shown instead of *Add* button for that row. | | |

### US007 — Update Cart Items

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC033 | Update quantity in cart | Cart has at least one item | Open *Cart / Wishlist*, change quantity, click *Update* | New quantity: `5` | Page reloads with green flash "Quantity updated." Subtotal recalculated. `wishlist.quantity` updated in DB. | | |
| TC034 | Update quantity to 0 | Cart has at least one item | Set quantity to 0, click *Update* | Quantity: `0` | Either browser blocks (HTML5 `min=1`) or server returns "Quantity must be positive." | | |
| TC035 | Remove item from cart | Cart has at least one item | Click *Remove* on a row | — | Row deleted from `wishlist`. Page reloads with green flash "Item removed from wishlist." | | |

### US008 — Place Order

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC036 | Place order with items in cart | Cart has 2 items | 1. Open Cart 2. Click *Place Order* 3. Confirm | — | Redirected to `/customer/orderConfirmation.jsp`. Page lists each line item with subtotals + grand total. New rows in `grocery_order`. | | |
| TC037 | Stock is decremented atomically | TC036 done | Compare `product.stock_availability` before and after | — | Stock for each ordered product reduced by exactly the ordered quantity. | | |
| TC038 | Cart is cleared after order | TC036 done | Navigate to Cart | — | Page shows "Your cart is empty." `wishlist` rows for that user are deleted. | | |
| TC039 | Place order with empty cart | Cart is empty | Click *Place Order* | — | Red flash "Your cart is empty." No DB changes. | | |
| TC040 | Order more than stock | Set cart quantity > stock available | Click *Place Order* | Cart qty 100, stock 50 | Red flash "Not enough stock for X (have 50, asked 100)." Order rejected, stock unchanged, cart preserved. | | |

### US009 — View Order History

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC041 | View past orders | TC036 done | Click *My Orders* | — | Table lists every past order: ID / date / product / qty / price / line total. Sorted newest first. | | |
| TC042 | Empty history for new customer | Fresh customer, no orders | Click *My Orders* | — | Page shows "You have not placed any orders yet." | | |

### US010 — Customer Details Update

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC043 | Update profile fields | Customer logged in | Navigate to *Profile*, change fields, click *Save Changes* | Name: `Indra B.`, Address: `Mumbai` | Green banner "Profile updated successfully." `customer` row updated. Header updates to "Hi, Indra B." | | |
| TC044 | Update profile with invalid email | Customer logged in | Change email to malformed value, save | Email: `bad-email` | Red banner "Email format is invalid: bad-email." Profile unchanged. | | |
| TC045 | Update profile with email that's already in use | Two customers exist; try to take the other's email | Change email to one belonging to another user | Email: another customer's email | Red banner "Email already in use: …" Profile unchanged. | | |

### US011 — Soft Delete Customer Account

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC046 | Deactivate own account | Customer logged in | On home page, click *Deactivate Account*, confirm | — | Session invalidated, redirected to `/login.jsp?deactivated=1` with banner "Your account has been deactivated." `login.status='Inactive'` in DB. | | |
| TC047 | Customer record retained after deactivation | TC046 done | Inspect `login` and `customer` tables | — | Both rows still exist (no hard delete), `login.status='Inactive'`. | | |

### US012 — Restore Customer Account

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC048 | Reactivate from login page | TC046 done | 1. On `/login.jsp`, enter the deactivated email + correct password 2. Reactivation form appears 3. Confirm password and submit | Email: `indra@test.com`, Password: `pass123` | Green banner "Account reactivated. You can now log in." `login.status='Active'`. | | |
| TC049 | Reactivation requires correct password | TC046 done | Try to reactivate with wrong password | Email: `indra@test.com`, Password: `wrong` | Red banner "Invalid credentials — cannot reactivate." Status remains Inactive. | | |
| TC050 | Login works after reactivation | TC048 done | Submit normal login | Email: `indra@test.com`, Password: `pass123` | Login succeeds, redirected to `/customer/index.jsp`. | | |

---

## Section B — Additional Functional Tests (web_proj.pdf — US_Web_001–005)

### US_Web_005 — Logout & URL Protection

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC051 | Logout invalidates session | Customer logged in | Click *Logout* | — | Redirected to `/login.jsp?loggedOut=1` with green banner "You have been logged out." | | |
| TC052 | Cookies cleared on logout | Logged in | 1. Inspect cookies in browser DevTools (`F12 → Application → Cookies`) 2. Click Logout 3. Re-inspect | — | `JSESSIONID` cookie is removed (or set with `Max-Age=0`). | | |
| TC053 | URL access blocked after logout | Logged in then logged out | Paste `/customer/catalog` in URL bar after logout | — | Redirected to `/login.jsp?expired=1`. Protected page is **not** shown. | | |
| TC054 | Browser back button after logout | Logged in then logged out | Press browser Back to attempt to view previous protected page | — | Either fresh redirect to login, or page shows but any link/action redirects. (No-cache headers prevent serving stale page from cache.) | | |

### Cross-role access (US_Web_005)

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC055 | Customer cannot access admin pages | Logged in as customer | Paste `/admin/index.jsp` in URL bar | — | Redirected to `/customer/index.jsp`. AuthFilter blocks access. | | |
| TC056 | Admin cannot access customer pages | Logged in as admin | Paste `/customer/catalog` in URL bar | — | Redirected to `/admin/index.jsp`. AuthFilter blocks access. | | |

### Product management (US_Web_003)

| TC ID | Scenario | Pre-condition | Steps | Test Data | Expected Output | Actual Output | Pass/Fail |
|---|---|---|---|---|---|---|---|
| TC057 | Update product as admin | Products exist; admin logged in | 1. *Product Info* 2. Click *Update* on a row 3. Edit and save | Change price from 120 to 150 | Green banner "Product N updated." Catalog reflects new price. | | |
| TC058 | Delete product as admin | Products exist; admin logged in | 1. *Product Info* 2. Click *Delete* on a row, confirm | — | Green banner "Product N deleted." Row removed from `product`, related `wishlist` and `grocery_order` rows also cleaned up. | | |

---

## Section C — Integration Test Scenarios (US014)

End-to-end flows that prove multiple components (UI → Servlet → DAO → DB) cooperate correctly.

| IT ID | Integration Flow | Verification Points | Expected Outcome | Actual Outcome | Pass/Fail |
|---|---|---|---|---|---|
| IT001 | **Customer registration → login → home** Register, log out, log back in | `login` row has `userType='customer'`, `status='Active'`. `customer` row linked by `login_id`. Session attributes `login`, `customerId`, `displayName` set. | All three artefacts created and session populated. Customer lands on `/customer/index.jsp`. | | |
| IT002 | **Admin add product → customer sees in catalog** Admin adds product, customer logs in and opens Catalog | New `product` row visible to a different customer's session. JSP renders price, stock, *Add* button. | Customer sees the just-added product without restart. | | |
| IT003 | **Customer add to cart → place order → stock decrements → order appears in history** Add 3× of product P (initial stock 10), place order | Before: `wishlist` row exists; product stock = 10. After: `wishlist` cleared for that user, 1 row in `grocery_order`, product stock = 7. *My Orders* lists the order. | All three tables update atomically; if any step fails the whole transaction rolls back. | | |
| IT004 | **Bulk CSV upload → customer search → add → place order** Upload `sample-products.csv` as admin, then a customer searches "rice", adds 2 to cart, places order | 10 product rows inserted. Catalog search returns "Basmati Rice". After order: `grocery_order` row with `quantity=2`, stock for that product reduced by 2. | Cross-module data flow works through bulk import → catalog → cart → order. | | |
| IT005 | **Deactivate → cannot login → reactivate from login page → login succeeds** TC046 → TC015 → TC048 → TC050 chained | `login.status` toggles Active → Inactive → Active. Behaviour for each state is enforced at login. | Status flag drives login behaviour correctly across the round trip. | | |
| IT006 | **Profile email change updates login row too** Customer updates email in *Profile*; subsequently logs in with the new email | After update: both `customer.email` and `login.email` reflect the new value (transactional). Login with old email → "Invalid email or password". Login with new email → success. | Update propagates to both tables atomically. | | |
| IT007 | **Logout end-to-end** Login → use the app → logout → try to revisit any `/admin/*` or `/customer/*` URL | Session invalidated, all cookies cleared, all subsequent protected URLs redirect to login. Browser cache should not serve stale protected pages (no-cache headers). | Session, cookies, and cache are all cleared on logout. | | |
| IT008 | **Delete product cascades to dependent rows** Admin deletes a product that exists in some customer's cart and order history | After delete: that product gone from `product`. Any `wishlist` rows for it are gone. Any `grocery_order` rows for it are gone (so the historical view ignores deleted products gracefully). | No foreign-key violations, no orphaned references. | | |

---

## Section D — Defect Log

Use this section to record any bug found during testing.

| Defect ID | TC ID | Severity (Low / Med / High) | Description | Steps to Reproduce | Workaround | Status (Open / Fixed) |
|---|---|---|---|---|---|---|
| D001 | | | | | | |
| D002 | | | | | | |
| D003 | | | | | | |
| D004 | | | | | | |
| D005 | | | | | | |

---

## Section E — Summary

| Metric | Count |
|---|---|
| Total functional test cases (TC001–TC058) | 58 |
| Total integration test cases (IT001–IT008) | 8 |
| **Grand total** | **66** |
| Passed | _____ |
| Failed | _____ |
| Pass rate | _____ % |

**Tester signature:** ____________________   **Date:** __________

**Reviewer signature:** __________________   **Date:** __________
