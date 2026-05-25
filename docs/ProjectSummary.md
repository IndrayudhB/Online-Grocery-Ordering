# Project Summary — Online Grocery Ordering Platform

> Snapshot of the project as of branch `feat/online-grocery-ordering-app`,
> for quick reference by team members, reviewers, and the client.

---

## 1. One-line description

A self-hosted online grocery e-commerce platform built end-to-end on
classic Java Web technologies — JSP + Servlets + SQLite running on
Apache Tomcat 9 — following the MVC pattern, with no framework
lock-in, no per-transaction fees, and full data ownership.

---

## 2. Tech stack

| Layer | Choice | Why |
|---|---|---|
| Front-end | JSP, HTML, CSS, JavaScript | Mature, no build step, easy to maintain |
| Back-end | Java Servlets (Servlet 4.0 / JSP 2.3) | Standard Jakarta-EE, runs on any compliant server |
| Server | Apache Tomcat 9.0.71 | Lightweight, well-documented, Pi-class friendly |
| Database | SQLite via `sqlite-jdbc-3.7.2.jar` (JDBC) | Single file, zero-config, atomic transactions |
| Java target | 1.8 | Broad compatibility |
| Build | None — Eclipse Dynamic Web Project | No Maven, no Gradle, plain `.classpath` / `.project` |

No external frameworks (no Spring, no Hibernate). Pure JDBC + JSP +
Servlets. Zero monthly licensing.

---

## 3. What's been delivered

### User stories (sprint requirements)

| ID | Story | Status |
|---|---|---|
| US001 | Customer registration with validation | done |
| US002 | Admin registration (programmatic seed) | done |
| US003 | Single-page login (customer + admin) | done |
| US004 | Product registration + bulk CSV upload | done |
| US005 | View product catalogue | done |
| US006 | Add products to cart | done |
| US007 | Update cart items | done |
| US008 | Place order with payment selection | done |
| US009 | View order history | done |
| US010 | Customer profile update | done |
| US011 | Soft-delete customer account | done |
| US012 | Restore deactivated account | done |
| US013 | End-to-end module integration | done |
| US014 | Integration testing (8 IT scenarios) | docs only |
| US015 | Functional testing (58 TC cases) | docs only |

### Web-specific stories (web project doc)

| ID | Story | Status |
|---|---|---|
| US_Web_001 | Customer registration + login routing | done |
| US_Web_002 | Add-admin via admin panel | done |
| US_Web_003 | Product management with validations | done |
| US_Web_004 | Wishlist / cart management with search | done |
| US_Web_005 | Logout, role isolation, URL protection | done |

### Extras beyond requirements

- **Dummy payment page** with three options: Cash on Delivery, UPI,
  Credit / Debit Card — full validation but no real gateway calls
- **40-product sample CSV** for bulk-upload demos
- **Self-diagnosing error page** that displays root cause + full
  stack trace inline (helps debug 500s without digging into Tomcat
  console)
- **Configurable DB path** via `web.xml` `<context-param db.path>`
  with system-property override (`-Dgrocery.db.path=...`)
- **Modern grocery-themed CSS** with gradients, animated login
  background, and responsive tables
- **66-test test plan** in markdown + CSV form
- **8-slide client presentation** in PowerPoint format

---

## 4. Architecture overview

### Three-tier

```
   Browser              Application Server             Database
  +--------+  HTTP   +----------------------+  JDBC  +--------+
  |  JSP   | <-----> | Servlets + Filters   | <----> | SQLite |
  |rendered|         |  (business logic)    |        |   DB   |
  +--------+         +----------------------+        +--------+
```

### MVC layout

| Layer | Code location | Examples |
|---|---|---|
| **View** | `WebContent/` (JSP files) | `customer/catalog.jsp`, `admin/productInfo.jsp`, `payment.jsp` |
| **Controller** | `src/servlet/` + `src/filter/` | `LoginServlet`, `PlaceOrderServlet`, `AuthFilter` |
| **Model** | `src/model/` (POJOs) + `src/dao/` (JDBC) | `Customer`, `Product`, `CustomerDAO`, `ProductDAO` |
| **Helpers** | `src/util/`, `src/exception/` | `DatabaseConnection`, `EmailValidator`, `PasswordUtil` |

### Database schema (5 tables)

```
login (login_id PK, email UNIQUE, password, userType, status)
customer (customer_id PK, name, email UNIQUE, contact_number,
          address, login_id FK -> login)
product (product_id PK, product_name, description, company_name,
         price CHECK <= 100000, stock_availability)
grocery_order (order_id PK, customer_id FK, product_id FK,
               quantity, order_date)
wishlist (wishlist_id PK, user_id FK -> login,
          product_id FK, quantity)
```

`grocery_order` was renamed from `order` because `order` is a SQLite
reserved keyword.

### Three engineering principles

1. **Separation of concerns** — UI never talks to the database directly.
2. **Transactional integrity** — placing an order, decrementing
   stock, and clearing the cart happen as one atomic step. Bulk CSV
   upload is also a single transaction (a bad row rolls back the
   whole batch).
3. **Defence in depth** — the same rule (e.g. `price <= 100000`) is
   enforced at the form level, the application level, *and* the
   database level (`CHECK` constraint).

---

## 5. Project layout

```
Online-Grocery-Ordering/
|-- .classpath, .project, .settings/      Eclipse Dynamic Web Project
|-- README.md                              Project overview + setup
|-- WebContent/
|   |-- WEB-INF/
|   |   |-- web.xml                        Sessions, error pages, db.path
|   |   |-- jspf/                          Header/footer fragments
|   |   |   |-- header-customer.jspf
|   |   |   |-- header-admin.jspf
|   |   |   +-- footer.jspf
|   |   +-- lib/                           Drop sqlite-jdbc-3.7.2.jar HERE
|   |-- admin/                             Admin pages
|   |   |-- index.jsp, addProduct.jsp,
|   |   |-- productInfo.jsp, editProduct.jsp,
|   |   +-- bulkUpload.jsp, addAdmin.jsp
|   |-- customer/                          Customer pages
|   |   |-- index.jsp, catalog.jsp,
|   |   |-- wishlist.jsp, payment.jsp,
|   |   |-- orderConfirmation.jsp,
|   |   +-- orderHistory.jsp, profile.jsp
|   |-- css/style.css                      Grocery theme stylesheet
|   |-- js/script.js                       Lightweight UI helpers
|   |-- login.jsp, register.jsp
|   |-- error.jsp                          Self-diagnosing error page
|   |-- index.jsp                          Redirects to /login.jsp
|   |-- sample-products.csv                 10 grocery items
|   +-- bulk-upload-products.csv            40 grocery items
|-- src/
|   |-- dao/                               JDBC data access objects
|   |   |-- CustomerDAO.java, LoginDAO.java,
|   |   |-- ProductDAO.java, OrderDAO.java,
|   |   +-- WishlistDAO.java
|   |-- exception/
|   |   |-- AppException.java,
|   |   |-- DuplicateEmailException.java,
|   |   +-- InvalidEmailException.java
|   |-- filter/
|   |   +-- AuthFilter.java                 Role-based access control
|   |-- model/                             Data classes
|   |   +-- Customer, Login, Product, Order, Wishlist
|   |-- servlet/
|   |   |-- RegisterServlet, LoginServlet, LogoutServlet,
|   |   |-- AddAdminServlet, ProductServlet, BulkUploadServlet,
|   |   |-- CatalogServlet, WishlistServlet,
|   |   |-- CheckoutServlet, PlaceOrderServlet,
|   |   |-- OrderHistoryServlet, ProfileServlet,
|   |   +-- AccountStatusServlet
|   +-- util/
|       |-- DatabaseConnection.java        Singleton JDBC helper
|       |-- AppContextListener.java        Schema bootstrap + admin seed
|       |-- EmailValidator.java
|       +-- PasswordUtil.java              SHA-256 hashing
+-- docs/
    |-- TestCases.md                       66 manual test cases
    |-- TestCases.csv                      Same data, spreadsheet form
    |-- PresentationPlan.md                6-speaker run-of-show
    |-- Presentation.pptx                  8-slide client deck
    |-- AISummaryPrompt.md                 Prompt for ChatGPT / Claude
    +-- ProjectSummary.md                  THIS FILE
```

---

## 6. Notable engineering decisions

### Authentication & sessions
- Single login page handles both roles, redirects by `userType`
- Passwords stored as **SHA-256 hashes** with a static application salt
- Session cookies marked `HttpOnly` (no JavaScript access)
- `AuthFilter` registered on `/admin/*` and `/customer/*` enforces
  role isolation: customers cannot reach admin URLs and vice versa
- Logout invalidates session, clears cookies, sets
  `Cache-Control: no-store` so the browser back-button cannot
  resurrect protected pages

### Soft-delete pattern
- Account "deletion" only flips `login.status` to `Inactive`
- Customer can reactivate by entering the same credentials and
  confirming the password
- All historical orders + customer record are preserved for audit

### Schema bootstrap
- `AppContextListener` runs on app startup, creates schema if missing
  (`CREATE TABLE IF NOT EXISTS`), and seeds a super-admin row
  (`root@grocery.local` / `root`)
- Resolution order for the SQLite path:
  1. JVM system property `-Dgrocery.db.path=...`
  2. `<context-param db.path>` in `web.xml`
  3. Default: `<webapp>/WEB-INF/grocery.db`

### Order placement
- Atomic transaction: insert order rows + decrement stock + clear
  wishlist all succeed together or none of them do
- Stock re-check at the moment of insertion catches concurrent edits
- Payment validation runs before any DB write — bad payment fields
  bounce back to checkout page with cart intact

### Validation strategy (defence in depth)

| Rule | Form (HTML5) | Servlet (Java) | Database |
|---|---|---|---|
| Email format | `type="email"` | `EmailValidator` regex | `UNIQUE` constraint |
| Price <= 100000 | `max="100000"` | `ProductDAO.validate()` | `CHECK(price <= 100000)` |
| Required fields | `required` | null/empty checks | `NOT NULL` columns |
| Quantity > 0 | `min="1"` | server validation | — |

---

## 7. Testing & quality artefacts

| Artefact | Location | Notes |
|---|---|---|
| Manual test plan (markdown) | `docs/TestCases.md` | 66 cases |
| Manual test plan (CSV) | `docs/TestCases.csv` | Excel-friendly |
| Functional cases | TC001 - TC058 | 58 cases, all 13 sprint stories + 5 web stories |
| Integration scenarios | IT001 - IT008 | end-to-end flows proving cross-module behaviour |
| Defect log template | TestCases.md - Section D | Severity / steps / status |
| Sign-off section | TestCases.md - Section E | Tester / reviewer signatures |

No automated tests yet. JUnit + DAO-level tests are the next step
(Phase 2 backlog).

---

## 8. Branches & commits

### Active branches

| Branch | Purpose |
|---|---|
| `main` | Default branch — initial state only |
| `feat/online-grocery-ordering-app` | All feature work, primary working branch |
| `feat/dwp-maven-layout` | Alternative Maven-style layout (closed PR) |
| `style/css-grocery-theme` | Standalone CSS download branch |
| `prep/presentation-pptx` | Standalone PowerPoint download branch |

### Key commits on `feat/online-grocery-ordering-app`

| SHA | Description |
|---|---|
| Initial | Initial commit |
| `3956071` | Build full-stack app (JSP + Servlets + SQLite) |
| `269b7ac` | Make SQLite path configurable via web.xml |
| `47783fa` | Resolve unreported SQLException in `ProductServlet.doGet` |
| `67f00b6` | Drop redundant `NumberFormatException` from multi-catch |
| `ed2698a` | Add manual test plan (US014 + US015) |
| `a302738` | Add 40-product bulk-upload sample CSV |
| `7aa57c6` | Delete bulk-upload-products.csv (later restored) |
| `bfb225c` | Refactor and enhance styles for modern grocery app |
| `4cfeb5e` | Update stylesheet link to use pageContext |
| `edf06ef` | Add dummy payment page (COD / UPI / card) |

### Pull requests

| PR | Title | State |
|---|---|---|
| #1 | feat: build full-stack online grocery ordering app | open |
| #2 | refactor: restructure to Maven-style DWP layout | open (alt) |

---

## 9. Run / deploy

### Eclipse + Tomcat (recommended)
1. Install Apache Tomcat 9.0.71
2. Drop `sqlite-jdbc-3.7.2.jar` into `WebContent/WEB-INF/lib/`
3. *File - Import - Existing Projects into Workspace*
4. *Window - Preferences - Server - Runtime Environments - Add Tomcat 9*
5. Right-click project - *Run As - Run on Server*
6. Open `http://localhost:8080/Online_Grocery_Ordering/`

### First-login credentials
- Super-admin: `root@grocery.local` / `root` (auto-seeded)

### Quick demo path
1. Login as super-admin
2. *Bulk Upload* - choose `bulk-upload-products.csv` - 40 products imported
3. Logout, register new customer, login
4. *Catalog* - search "rice" - add a couple products
5. *Cart - Proceed to Payment* - pick UPI - enter `demo@upi` - Pay
6. See order confirmation with payment method
7. *My Orders* - past orders show

---

## 10. Known constraints & roadmap

### Current constraints
- SQLite scales to a few hundred concurrent users; beyond that, a
  MySQL / PostgreSQL migration is needed (one-line JDBC URL change)
- Passwords use SHA-256 + static salt; production should use bcrypt
  / PBKDF2 / Argon2
- No HTTPS by default (Tomcat handles SSL termination if configured)
- No automated tests yet; only manual test cases documented

### Phase 2 roadmap
**Soon** — payment gateway, email/SMS notifications, bcrypt passwords,
mobile-PWA polish.

**Mid-term** — multi-store, REST API layer, MySQL migration,
analytics dashboard, loyalty engine.

**Long-term** — native iOS/Android apps, recommendation engine,
multi-tenant SaaS deployment.

---

## 11. Why this stack vs market alternatives

| Aspect | Our Platform | SaaS (Shopify) | WordPress + Plugin |
|---|---|---|---|
| Per-sale fee | 0 | 1.5-2.9% per sale | Plugin licensing |
| Time to deploy | hours | days | days |
| Hosting footprint | Pi-class machine | vendor cloud only | 1GB+ PHP/MySQL |
| Data ownership | 100% (one DB file) | vendor cloud | hosting provider |
| Vendor lock-in | none | total | heavy plugin chain |
| Customisation | full source | themes + apps only | plugin-permitted |
| Grocery features | built-in | generic | generic |

Cost predictability + total ownership + grocery-fit features = lower
TCO than every market alternative.
