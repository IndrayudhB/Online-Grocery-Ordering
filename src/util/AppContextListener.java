package util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Bootstraps the SQLite database when the web application starts:
 *  - Resolves the DB file path under WEB-INF/grocery.db
 *  - Creates all required tables if they do not yet exist
 *  - Seeds a super-admin account (root@grocery.local / root)
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String ROOT_ADMIN_EMAIL = "root@grocery.local";
    public static final String ROOT_ADMIN_PASSWORD = "root";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        // Resolution order for the SQLite database file path:
        //   1. JVM system property   -Dgrocery.db.path=...
        //   2. <context-param> "db.path" in web.xml
        //   3. Default: <webapp>/WEB-INF/grocery.db
        String dbPath = System.getProperty("grocery.db.path");
        if (dbPath == null || dbPath.trim().isEmpty()) {
            dbPath = ctx.getInitParameter("db.path");
        }
        if (dbPath == null || dbPath.trim().isEmpty()) {
            String webInfPath = ctx.getRealPath("/WEB-INF");
            if (webInfPath == null) {
                ctx.log("[GroceryApp] Could not resolve /WEB-INF real path. "
                        + "Falling back to java.io.tmpdir.");
                webInfPath = System.getProperty("java.io.tmpdir");
            }
            dbPath = new File(webInfPath, "grocery.db").getAbsolutePath();
        } else {
            // Make sure the parent directory exists so SQLite can create
            // the file (otherwise it fails with "unable to open db file").
            File f = new File(dbPath.trim());
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    ctx.log("[GroceryApp] Could not create parent dir: "
                            + parent.getAbsolutePath());
                }
            }
            dbPath = f.getAbsolutePath();
        }

        DatabaseConnection.setDbPath(dbPath);
        ctx.log("[GroceryApp] SQLite DB path: " + dbPath);

        try {
            createSchema();
            seedRootAdmin();
            ctx.log("[GroceryApp] Database initialized successfully.");
        } catch (SQLException e) {
            ctx.log("[GroceryApp] Database initialization failed: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No persistent connection pool to close.
    }

    private void createSchema() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS login (" +
                "  login_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  email    TEXT UNIQUE NOT NULL," +
                "  password TEXT NOT NULL," +
                "  userType TEXT NOT NULL," +
                "  status   TEXT NOT NULL DEFAULT 'Active'" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS customer (" +
                "  customer_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name           TEXT NOT NULL," +
                "  email          TEXT UNIQUE NOT NULL," +
                "  contact_number TEXT," +
                "  address        TEXT," +
                "  login_id       INTEGER," +
                "  FOREIGN KEY (login_id) REFERENCES login(login_id)" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS product (" +
                "  product_id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  product_name        TEXT NOT NULL," +
                "  description         TEXT NOT NULL," +
                "  company_name        TEXT," +
                "  price               REAL NOT NULL CHECK(price <= 100000)," +
                "  stock_availability  INTEGER" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS grocery_order (" +
                "  order_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  customer_id INTEGER," +
                "  product_id  INTEGER," +
                "  quantity    INTEGER," +
                "  order_date  TEXT," +
                "  FOREIGN KEY (customer_id) REFERENCES customer(customer_id)," +
                "  FOREIGN KEY (product_id)  REFERENCES product(product_id)" +
                ")");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS wishlist (" +
                "  wishlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  user_id     INTEGER," +
                "  product_id  INTEGER," +
                "  quantity    INTEGER," +
                "  FOREIGN KEY (user_id)    REFERENCES login(login_id)," +
                "  FOREIGN KEY (product_id) REFERENCES product(product_id)" +
                ")");
        }
    }

    private void seedRootAdmin() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Skip if already exists
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT login_id FROM login WHERE email = ?")) {
                ps.setString(1, ROOT_ADMIN_EMAIL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO login (email, password, userType, status) " +
                    "VALUES (?, ?, 'admin', 'Active')")) {
                ps.setString(1, ROOT_ADMIN_EMAIL);
                ps.setString(2, PasswordUtil.hash(ROOT_ADMIN_PASSWORD));
                ps.executeUpdate();
            }
        }
    }
}
