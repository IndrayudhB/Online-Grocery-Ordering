package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection helper for SQLite.
 *
 * The actual database file path is configured at application startup
 * by {@link AppContextListener} via {@link #setDbPath(String)}.
 *
 * Each call to {@link #getConnection()} returns a fresh JDBC connection
 * with foreign-key enforcement enabled. Callers MUST close the
 * connection (and any statements/result sets) when done.
 */
public final class DatabaseConnection {

    private static String dbPath;
    private static boolean driverLoaded = false;

    private DatabaseConnection() {
        // utility class
    }

    /** Called once during application startup. */
    public static synchronized void setDbPath(String path) {
        dbPath = path;
    }

    public static synchronized String getDbPath() {
        return dbPath;
    }

    /** Returns a new SQLite JDBC connection with foreign keys enabled. */
    public static Connection getConnection() throws SQLException {
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new SQLException(
                    "Database path is not configured. Did AppContextListener run?");
        }
        if (!driverLoaded) {
            try {
                Class.forName("org.sqlite.JDBC");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                        "SQLite JDBC driver (sqlite-jdbc-3.7.2.jar) is missing "
                                + "from WEB-INF/lib.", e);
            }
        }
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (java.sql.Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
}
