# User Manual
# Online Grocery Ordering Platform

---

| | |
|---|---|
| **Product Name** | Online Grocery Ordering Platform |
| **Document Type** | User Manual |
| **Version** | 1.0 |
| **Release Date** | ______________________ |
| **Prepared by** | Team of 6 |
| **Audience** | End Customers & Store Administrators |
| **Platform** | Web application (JSP + Servlets + SQLite on Apache Tomcat 9) |

---

## Document Revision History

| Version | Date | Author | Description of Change |
|---------|------|--------|-----------------------|
| 1.0 | __________ | ____________ | Initial release |
| | | | |
| | | | |

---

## Table of Contents

1. Introduction
2. Purpose & Scope
3. Intended Audience
4. System Requirements
5. Getting Started (Accessing the Application)
6. User Roles & Access Levels
7. Customer Guide
   - 7.1 Registering a New Account
   - 7.2 Logging In
   - 7.3 Browsing the Product Catalogue
   - 7.4 Searching for a Product
   - 7.5 Adding Products to the Cart
   - 7.6 Viewing & Updating the Cart
   - 7.7 Placing an Order & Choosing Payment
   - 7.8 Viewing Order History
   - 7.9 Updating Your Profile
   - 7.10 Deactivating Your Account
   - 7.11 Reactivating Your Account
   - 7.12 Logging Out
8. Administrator Guide
   - 8.1 Logging In as Admin
   - 8.2 Adding a Single Product
   - 8.3 Bulk Uploading Products (CSV)
   - 8.4 Viewing, Editing & Deleting Products
   - 8.5 Adding Another Administrator
   - 8.6 Logging Out
9. Payment Methods (Demo)
10. Error Messages & Their Meaning
11. Troubleshooting & FAQ
12. Data Privacy & Security Notes
13. Glossary
14. Support & Contact

---

## 1. Introduction

The Online Grocery Ordering Platform is a web-based application that lets
customers browse a grocery catalogue, build a shopping cart, and place
orders online. Store administrators use the same application to manage
the product catalogue, upload products in bulk, and manage admin
accounts.

This manual explains, in step-by-step form, how to use every feature of
the application. No technical knowledge is required to follow it.

---

## 2. Purpose & Scope

**Purpose** — to give customers and administrators a clear, illustrated
guide to performing every task the application supports.

**In scope:**
- All customer-facing features (registration, shopping, ordering,
  profile management).
- All administrator features (product management, bulk upload, admin
  management).
- Common error messages and how to resolve them.

**Out of scope:**
- Server installation and deployment (covered separately in the
  project README / deployment guide).
- Source-code or developer documentation.
- Real payment processing (the payment step is a demo simulation).

---

## 3. Intended Audience

| Audience | What they will find useful |
|----------|----------------------------|
| **Customers** | Sections 5, 6, 7, 9, 10, 11 |
| **Administrators** | Sections 5, 6, 8, 9, 10, 11 |
| **Support staff** | Sections 10, 11, 12, 13, 14 |

---

## 4. System Requirements

To use the application you only need a web browser. No software needs to
be installed on your computer.

| Requirement | Minimum |
|-------------|---------|
| Web browser | Google Chrome, Microsoft Edge, Mozilla Firefox, or Safari (current version) |
| Internet / network | Access to the server URL (LAN or internet, depending on deployment) |
| Screen resolution | 1024 x 768 or higher recommended |
| JavaScript | Must be enabled (it is by default) |
| Cookies | Must be allowed for the application site (used for login sessions) |

---

## 5. Getting Started (Accessing the Application)

1. Open your web browser.
2. In the address bar, type the application URL provided by your
   administrator. For a local installation this is typically:

   ```
   http://localhost:8080/Online_Grocery_Ordering/
   ```

3. Press **Enter**. You will be taken to the **Login** page.

> _[Screenshot placeholder: Login page]_

From the Login page you can either sign in (if you already have an
account) or click the link to register a new customer account.

---

## 6. User Roles & Access Levels

The application has two types of users:

| Role | Can do | Cannot do |
|------|--------|-----------|
| **Customer** | Register, browse catalogue, search, manage cart, place orders, view order history, update profile, deactivate/reactivate own account | Access any administrator page |
| **Administrator** | Add / edit / delete products, bulk-upload products, add other admins | Access customer shopping pages (cart, checkout) |

The system automatically directs you to the correct home page based on
your role when you log in. Attempting to open a page meant for the other
role will redirect you away.

---

## 7. Customer Guide

### 7.1 Registering a New Account

1. On the Login page, click **Create one** (the registration link).
2. The Registration form appears. Fill in:
   - **Full name** (required)
   - **Email** (required, must be a valid email format and not already
     registered)
   - **Contact number** (optional)
   - **Address** (optional)
   - **Password** (required)
3. Click **Register**.
4. On success you are returned to the Login page with the message
   *"Registration successful! Please log in."*

> _[Screenshot placeholder: Registration form]_

**Validation notes:**
- If the email format is invalid, you will see *"Email format is
  invalid"*.
- If the email is already registered, you will see *"An account already
  exists for email ..."*.

---

### 7.2 Logging In

1. On the Login page, enter your **Email** and **Password**.
2. Click **Sign in**.
3. You are taken to the **Customer Home** page, which greets you by name.

> _[Screenshot placeholder: Customer home page]_

If your credentials are incorrect you will see *"Invalid email or
password."*

---

### 7.3 Browsing the Product Catalogue

1. From the top navigation bar, click **Catalog**.
2. The catalogue displays all available products in a table showing:
   ID, name, description, brand, price, and available stock.
3. Each in-stock product has a quantity box and an **Add** button.

> _[Screenshot placeholder: Product catalogue]_

Products that are out of stock show an *"Out of stock"* label instead of
an Add button.

---

### 7.4 Searching for a Product

1. On the Catalog page, type part of a product name in the search box
   (for example, `rice`).
2. Click **Search**.
3. The table updates to show only matching products.
4. Click **Clear** (or search with an empty box) to see all products
   again.

---

### 7.5 Adding Products to the Cart

1. On the Catalog page, find the product you want.
2. Enter the **quantity** in the quantity box.
3. Click **Add**.
4. A confirmation message *"Product added to wishlist"* appears.

If you add the same product again, the quantity is increased rather than
creating a duplicate entry.

---

### 7.6 Viewing & Updating the Cart

1. Click **Cart / Wishlist** in the navigation bar.
2. The cart lists each product, its price, quantity, and subtotal, plus
   the overall total.

> _[Screenshot placeholder: Cart / Wishlist page]_

**To change a quantity:**
- Update the number in the quantity box for that row and click
  **Update**. A *"Quantity updated"* message confirms the change.

**To remove an item:**
- Click **Remove** on that row. An *"Item removed from wishlist"*
  message confirms removal.

---

### 7.7 Placing an Order & Choosing Payment

1. In the cart, click **Proceed to Payment**.
2. The **Checkout & Payment** page appears, showing an order summary and
   the total payable amount.
3. Choose one of the payment methods:
   - **Cash on Delivery** — no extra details needed.
   - **UPI** — enter a UPI ID in the form `name@bank`.
   - **Credit / Debit Card** — enter the card number, expiry (MM/YY),
     CVV, and cardholder name.
4. Click **Pay**.
5. On success you are taken to the **Order Confirmation** page, which
   lists each item, the total, and the chosen payment method.

> _[Screenshot placeholder: Payment page]_
> _[Screenshot placeholder: Order confirmation page]_

> **Note:** Payment is a **demo simulation only**. No real money is
> charged and no card details are sent anywhere. You may use sample
> values such as card `4111 1111 1111 1111`, expiry `12/29`, CVV `123`,
> or UPI ID `demo@upi`.

**What happens when you order:**
- Each item becomes an order record.
- Product stock is reduced by the quantity you ordered.
- Your cart is emptied.

If an item's quantity exceeds available stock, the order is rejected and
a message tells you which product is short.

---

### 7.8 Viewing Order History

1. Click **My Orders** in the navigation bar.
2. A table lists all your past orders, newest first, showing order ID,
   date, product, quantity, price, and line total.

> _[Screenshot placeholder: Order history page]_

If you have not placed any orders yet, the page shows *"You have not
placed any orders yet."*

---

### 7.9 Updating Your Profile

1. Click **Profile** in the navigation bar.
2. Edit any of: full name, email, contact number, address.
3. Click **Save Changes**.
4. A *"Profile updated successfully"* message confirms the update, and
   your displayed name updates immediately.

> _[Screenshot placeholder: Profile page]_

**Validation notes:**
- The email must be a valid format.
- The email cannot already belong to another account.

---

### 7.10 Deactivating Your Account

1. On the Customer Home page, find the **Account** section.
2. Click **Deactivate Account**.
3. Confirm when prompted.
4. Your account is deactivated and you are logged out. Your data is
   **not deleted** — it is preserved so you can reactivate later.

---

### 7.11 Reactivating Your Account

1. On the Login page, enter the email and password of your deactivated
   account and click **Sign in**.
2. The system detects the account is inactive and shows a reactivation
   form.
3. Confirm your password and click **Reactivate Account**.
4. A message confirms reactivation. You can now log in normally.

---

### 7.12 Logging Out

1. Click **Logout** at the top-right of any page.
2. You are returned to the Login page with a *"You have been logged
   out"* message.
3. For your security, after logout the browser cannot reopen your
   previous pages using the Back button, and pasting an internal URL
   redirects you to the Login page.

---

## 8. Administrator Guide

### 8.1 Logging In as Admin

1. Go to the Login page.
2. Enter the administrator email and password. The default super-admin
   account created with the system is:
   - **Email:** `root@grocery.local`
   - **Password:** `root`
3. Click **Sign in**. You are taken to the **Admin Dashboard**.

> _[Screenshot placeholder: Admin dashboard]_

> **Security tip:** change or replace the default super-admin
> credentials before going live.

---

### 8.2 Adding a Single Product

1. From the admin navigation, click **Add Product**.
2. Fill in:
   - **Product name** (required, cannot be empty)
   - **Description** (required, cannot be empty)
   - **Company / Brand** (optional)
   - **Price** (required, must be 0 to 100000)
   - **Stock availability** (required, 0 or more)
3. Click **Create Product**.
4. A *"Product created successfully"* message appears and the form
   clears, ready for the next product.

> _[Screenshot placeholder: Add Product form]_

---

### 8.3 Bulk Uploading Products (CSV)

1. From the admin navigation, click **Bulk Upload**.
2. Prepare a CSV file with this header row and one product per line:

   ```
   product_name,description,company_name,price,stock_availability
   ```

   A ready-made sample (`bulk-upload-products.csv`, 40 products) is
   provided with the application.
3. Click **Choose File** and select your CSV.
4. Click **Upload**.
5. On success a message reports how many products were imported, e.g.
   *"40 product(s) imported successfully."*

> _[Screenshot placeholder: Bulk Upload page]_

**Important:** the upload is all-or-nothing. If any row fails validation
(for example a price above 100000), the entire file is rejected and no
products are imported. Fix the offending row and upload again.

---

### 8.4 Viewing, Editing & Deleting Products

1. From the admin navigation, click **Product Info**.
2. A table lists every product with **Update** and **Delete** buttons.

> _[Screenshot placeholder: Product Info table]_

**To edit a product:**
1. Click **Update** on its row.
2. A pre-filled form opens. Change any field.
3. Click **Save**. A confirmation message appears and the catalogue
   reflects your change.

**To delete a product:**
1. Click **Delete** on its row.
2. Confirm when prompted.
3. The product is removed. Any references to it in customer carts and
   order history are cleaned up automatically.

---

### 8.5 Adding Another Administrator

1. From the admin navigation, click **Add Admin**.
2. Enter the new administrator's **email** and **password**.
3. Click **Create Admin**.
4. A *"Admin created successfully"* message confirms the new account.

> _[Screenshot placeholder: Add Admin form]_

The **Add Admin** option is visible only to administrators; customers
never see it.

---

### 8.6 Logging Out

Click **Logout** at the top-right. You are returned to the Login page and
your session is fully cleared.

---

## 9. Payment Methods (Demo)

The application offers three payment methods at checkout. All are
**simulated** for demonstration; no real transaction occurs.

| Method | What you enter | Notes |
|--------|----------------|-------|
| **Cash on Delivery** | Nothing | Pay the delivery agent on arrival |
| **UPI** | UPI ID like `name@bank` | Format is validated, not charged |
| **Credit / Debit Card** | 16-digit number, MM/YY expiry, 3-4 digit CVV, name | Format is validated, not charged |

Sample test values: card `4111 1111 1111 1111`, expiry `12/29`,
CVV `123`, UPI `demo@upi`.

---

## 10. Error Messages & Their Meaning

| Message | Meaning | What to do |
|---------|---------|------------|
| Email format is invalid | The email you typed is not a valid address | Re-enter a correct email (e.g. name@example.com) |
| An account already exists for email ... | That email is already registered | Log in instead, or use a different email |
| Invalid email or password | Login credentials don't match | Re-check email and password |
| Your account is deactivated | You are trying to log in to a deactivated account | Use the reactivation form shown on the login page |
| Name, email and password are required | A required registration field is empty | Fill in all required fields |
| Product name must not be empty | Admin left the product name blank | Enter a product name |
| Price must be between 0 and 100000.0 | Admin entered a price out of range | Enter a price up to 100000 |
| Your cart is empty | You tried to checkout with nothing in the cart | Add products before checking out |
| Not enough stock for ... | Ordered quantity exceeds available stock | Reduce the quantity or wait for restock |
| Please enter a valid UPI ID like name@bank | UPI ID format is wrong | Enter a UPI ID in the correct format |
| Card number must be 16 digits | Card number is the wrong length | Enter a 16-digit card number |
| Please sign in to continue | You tried to open a protected page while logged out | Log in first |

---

## 11. Troubleshooting & FAQ

**Q: I clicked a link/button and nothing seems to have changed.**
Refresh the page (press F5). If the problem persists, log out and log
back in.

**Q: The catalogue is empty.**
No products have been added yet. An administrator must add products
(individually or via bulk upload) before they appear.

**Q: I can't log in even though my password is correct.**
Your account may be deactivated. Try logging in — if it is, the
reactivation form will appear. Otherwise contact support.

**Q: After logging out, the Back button shows an old page.**
This is prevented by design; clicking any link or refreshing returns you
to the Login page. Simply log in again to continue.

**Q: My session expired / I was logged out unexpectedly.**
Sessions time out after a period of inactivity. Log in again.

**Q: The bulk CSV upload failed.**
Check that the first line is the header row and that every product row
has all five columns, with a price no greater than 100000. Fix the file
and upload again.

**Q: Are my card details stored?**
No. Payment is a demo and no card or UPI details are stored or
transmitted to any payment processor.

---

## 12. Data Privacy & Security Notes

- Passwords are never stored in plain text; they are stored as secure
  one-way hashes.
- Login sessions use secure cookies that web pages' scripts cannot read.
- Customers and administrators are strictly separated — neither can
  access the other's pages.
- Logging out clears your session and cookies completely.
- Deactivating an account hides it but preserves your data so you can
  return; it is never silently deleted.

---

## 13. Glossary

| Term | Meaning |
|------|---------|
| **Catalogue** | The list of all products available to buy |
| **Cart / Wishlist** | Your selected products before you place an order |
| **Checkout** | The step where you confirm your order and choose payment |
| **COD** | Cash on Delivery |
| **UPI** | Unified Payments Interface (Indian instant-payment system) |
| **Administrator (Admin)** | A user who manages products and admin accounts |
| **Super-admin** | The first administrator account created with the system |
| **Soft delete** | Marking an account inactive without erasing its data |
| **Session** | The period during which you stay logged in |
| **Bulk upload** | Adding many products at once via a CSV file |

---

## 14. Support & Contact

| Item | Detail |
|------|--------|
| Support email | ______________________ |
| Phone | ______________________ |
| Hours | ______________________ |
| Escalation contact | ______________________ |

For installation or server-side issues, refer to the project README and
deployment guide, or contact your system administrator.

---

*End of User Manual — Online Grocery Ordering Platform, Version 1.0*
