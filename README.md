# Online Grocery Ordering — JSP + Servlets + SQLite

A full-stack online grocery ordering web application built with classic
Java Web technologies (JSP + Servlets), packaged as an Eclipse Dynamic
Web Project (no Maven), targeting Apache Tomcat 9.0.71 with SQLite as the
persistence layer. The codebase follows the **MVC** architecture:

```
View         JSPs in src/main/webapp/
Controller   Servlets in src/main/java/servlet/, Filter in src/main/java/filter/
Model        POJOs in src/main/java/model/
Data         DAOs in src/main/java/dao/, JDBC helper in src/main/java/util/
```

---

## Tech stack

| Layer            | Choice                                    |
|------------------|-------------------------------------------|
| Frontend         | JSP, HTML, CSS, JavaScript                |
| Backend          | Java Servlets (Servlet 4.0, JSP 2.3)      |
| Server           | Apache Tomcat 9.0.71                      |
| Database         | SQLite via `sqlite-jdbc-3.7.2.jar` (JDBC) |
| Java target      | 1.8                                       |

No frameworks, no Maven, no Hibernate, no Spring — pure JDBC + JSP + Servlets.

---

## Project layout

```
Online-Grocery-Ordering/
├── .classpath, .project, .settings/   # Eclipse Dynamic Web Project metadata
└── src/
    ├── main/
    │   ├── java/                      # Java source root (Maven-style layout, no Maven)
    │   │   ├── dao/         CustomerDAO, LoginDAO, ProductDAO, OrderDAO, WishlistDAO
    │   │   ├── exception/   InvalidEmailException, DuplicateEmailException, AppException
    │   │   ├── filter/      AuthFilter   (role-based access control)
    │   │   ├── model/       Customer, Login, Product, Order, Wishlist
    │   │   ├── servlet/     RegisterServlet, LoginServlet, LogoutServlet,
    │   │   │                AddAdminServlet, ProductServlet, BulkUploadServlet,
    │   │   │                CatalogServlet, WishlistServlet, PlaceOrderServlet,
    │   │   │                OrderHistoryServlet, ProfileServlet, AccountStatusServlet
    │   │   └── util/        DatabaseConnection, EmailValidator, PasswordUtil,
    │   │                    AppContextListener  (creates schema, seeds super-admin)
    │   └── webapp/                    # Web content root (was WebContent/)
    │       ├── META-INF/
    │       │   └── MANIFEST.MF
    │       ├── WEB-INF/
    │       │   ├── web.xml            # session, error pages, http-only cookies, db.path
    │       │   ├── jspf/              # shared header/footer fragments
    │       │   └── lib/               # drop sqlite-jdbc-3.7.2.jar HERE
    │       ├── admin/                 # admin pages (index, addProduct, productInfo, ...)
    │       ├── customer/              # customer pages (catalog, wishlist/cart, ...)
    │       ├── css/style.css
    │       ├── js/script.js
    │       ├── login.jsp, register.jsp
    │       ├── error.jsp
    │       ├── index.jsp              # redirects to /login.jsp
    │       └── sample-products.csv    # for bulk-upload demo
```

---

## Database schema

Created automatically on first startup by `util.AppContextListener`. The
SQLite file lives at `src/main/webapp/WEB-INF/grocery.db` once Tomcat exposes
the exploded webapp (this is the *fallback* path used when no `db.path`
context-param or `-Dgrocery.db.path=` system property is set).

| Table          | Notes                                                    |
|----------------|----------------------------------------------------------|
| `login`        | Auth source for both customers and admins. Status flag   |
|                | toggles between `Active` / `Inactive` for soft-delete.   |
| `customer`     | Profile details, FK -> `login(login_id)`                 |
| `product`      | `price` is constrained to `<= 100000` at the DB level    |
| `grocery_order`| Renamed from `order` to dodge the SQLite reserved word   |
| `wishlist`     | Doubles as the cart-staging table                        |

A super-admin is seeded on first startup:

```
email:    root@grocery.local
password: root
```

---

## Build & deploy

### One-time setup

1. **Install Apache Tomcat 9.0.71.**
2. **Drop the SQLite driver in place:**
   - Download `sqlite-jdbc-3.7.2.jar` (e.g. from Maven Central:
     `https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.7.2/sqlite-jdbc-3.7.2.jar`)
   - Copy it into `src/main/webapp/WEB-INF/lib/`.

### From Eclipse (recommended)

1. *File → Import → Existing Projects into Workspace* and pick this folder.
2. *Window → Preferences → Server → Runtime Environments → Add* a new
   *Apache Tomcat v9.0* runtime pointing to your local Tomcat 9.0.71.
3. Right-click the project → *Run As → Run on Server*. Eclipse will deploy
   the project at context path `/GroceryApp` and start Tomcat.
4. Open `http://localhost:8080/GroceryApp/` — you'll be redirected to
   `/login.jsp`.

### From a plain Tomcat install (without Eclipse)

You can also deploy the exploded webapp manually:

```
$TOMCAT_HOME/webapps/GroceryApp/
├── WEB-INF/
│   ├── classes/        # compiled .class files preserving the package layout
│   ├── lib/            # sqlite-jdbc-3.7.2.jar
│   └── web.xml
├── admin/
├── customer/
├── css/, js/
├── login.jsp, register.jsp, error.jsp, index.jsp, sample-products.csv
└── ... (everything else from src/main/webapp/)
```

Compile sources with the Tomcat `lib/servlet-api.jar` on the classpath:

```bash
mkdir -p build/classes
javac -d build/classes -cp "$TOMCAT_HOME/lib/*" $(find src/main/java -name "*.java")
cp -r build/classes/* $TOMCAT_HOME/webapps/GroceryApp/WEB-INF/classes/
cp -r src/main/webapp/* $TOMCAT_HOME/webapps/GroceryApp/
cp src/main/webapp/WEB-INF/lib/sqlite-jdbc-3.7.2.jar \
   $TOMCAT_HOME/webapps/GroceryApp/WEB-INF/lib/
$TOMCAT_HOME/bin/startup.sh
```

Then visit `http://localhost:8080/GroceryApp/`.

---

## Default URLs

| URL                                  | Who        | Purpose                       |
|--------------------------------------|------------|-------------------------------|
| `/login.jsp`                         | public     | Sign in (customer or admin)   |
| `/register.jsp`                      | public     | Create a customer account     |
| `/customer/index.jsp`                | customer   | Customer home                 |
| `/customer/catalog`                  | customer   | Browse + search products      |
| `/customer/wishlist`                 | customer   | Cart / wishlist (US006-US008) |
| `/customer/orderHistory`             | customer   | Past orders (US009)           |
| `/customer/profile`                  | customer   | Update personal info (US010)  |
| `/admin/index.jsp`                   | admin      | Admin dashboard               |
| `/admin/addProduct.jsp`              | admin      | Add a single product (US004)  |
| `/admin/products`                    | admin      | List + Update + Delete        |
| `/admin/bulkUpload.jsp`              | admin      | Bulk CSV upload (US004)       |
| `/admin/addAdmin.jsp`                | admin      | Create another admin          |
| `/logout`                            | any        | Clear session + cookies       |
| `/accountStatus` (POST)              | varies     | Deactivate / reactivate       |

---

## User stories coverage

| Story                                          | Status |
|------------------------------------------------|--------|
| US001 Customer Registration                    | done   |
| US002 Admin Registration (programmatic seed)   | done   |
| US003 User Login (single page, role redirect)  | done   |
| US004 Product Add (single + bulk CSV)          | done   |
| US005 View Product Catalog                     | done   |
| US006 Add to Cart (wishlist-as-cart)           | done   |
| US007 Update Cart Items                        | done   |
| US008 Place Order (atomic + stock decrement)   | done   |
| US009 View Order History                       | done   |
| US010 Customer Details Update                  | done   |
| US011 Soft Delete Customer (Inactive)          | done   |
| US012 Restore Customer Account                 | done   |
| US013 Code Integration                         | done   |
| US014/US015 Tests                              | n/a    |

Plus the additional backlog items:

- **US_Web_002**: super-admin seed (`root@grocery.local` / `root`) and an admin-only "Add Admin" page.
- **US_Web_003**: admin product table with inline Update/Delete + popups via flash messages; price validation enforced at app level *and* DB level (`CHECK(price <= 100000)`).
- **US_Web_004**: catalog "Add" button with quantity + name search; wishlist page with Update / Remove; product existence is verified before insert.
- **US_Web_005 / Story 5**: `AuthFilter` blocks customers from `/admin/*` and admins from `/customer/*`. Logout invalidates the session, clears cookies, and writes `Cache-Control: no-store` so back-button cannot resurrect protected pages.

---

## Validations & error handling

- Email format is checked with a regex (`util.EmailValidator`) before any
  insert; mismatches throw `exception.InvalidEmailException`.
- Duplicate emails throw `exception.DuplicateEmailException`.
- All database failures bubble up as `SQLException` and are caught by the
  servlet, which forwards back to the originating JSP with a friendly
  message.
- Product `price > 100000`, empty `product_name`, or empty `description`
  raise `IllegalArgumentException` from `ProductDAO.validate()` and are
  surfaced inline in the form.
- Bulk CSV imports run inside a single transaction — if any row fails
  validation the whole import is rolled back.
- The 500 / uncaught-exception response is mapped to `error.jsp` via
  `web.xml`.

---

## Notes

- Passwords are hashed with SHA-256 + a static salt
  (`util.PasswordUtil`). For production you should swap this for bcrypt
  or PBKDF2.
- The SQLite database file lives next to `WEB-INF` and persists across
  redeployments of the exploded webapp.
- All servlets, filters and the context-listener register themselves via
  Servlet 3.0+ annotations; `web.xml` only carries error-page mappings,
  session timeout, and the welcome file list.
