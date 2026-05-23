package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Product;
import util.DatabaseConnection;

/** JDBC access to the {@code product} table. */
public class ProductDAO {

    public static final double MAX_PRICE = 100000.0;

    /** Validates business rules and inserts a product. */
    public int insert(Product p) throws SQLException, IllegalArgumentException {
        validate(p);
        try (Connection c = DatabaseConnection.getConnection()) {
            return insert(c, p);
        }
    }

    /** Same as {@link #insert(Product)} but reuses an existing connection. */
    public int insert(Connection conn, Product p)
            throws SQLException, IllegalArgumentException {
        validate(p);
        String sql = "INSERT INTO product "
                   + "(product_name, description, company_name, price, "
                   + "stock_availability) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getCompanyName());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStockAvailability());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to obtain generated product_id.");
    }

    public void update(Product p) throws SQLException, IllegalArgumentException {
        validate(p);
        String sql = "UPDATE product SET product_name = ?, description = ?, "
                   + "company_name = ?, price = ?, stock_availability = ? "
                   + "WHERE product_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getCompanyName());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStockAvailability());
            ps.setInt(6, p.getProductId());
            ps.executeUpdate();
        }
    }

    public void delete(int productId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Cleanup dependent rows so FK constraints don't trip
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM wishlist WHERE product_id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM grocery_order WHERE product_id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM product WHERE product_id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public Product findById(int productId) throws SQLException {
        String sql = "SELECT product_id, product_name, description, "
                   + "company_name, price, stock_availability "
                   + "FROM product WHERE product_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, product_name, description, "
                   + "company_name, price, stock_availability "
                   + "FROM product ORDER BY product_id";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Product> searchByName(String term) throws SQLException {
        if (term == null || term.trim().isEmpty()) {
            return findAll();
        }
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, product_name, description, "
                   + "company_name, price, stock_availability "
                   + "FROM product WHERE product_name LIKE ? "
                   + "ORDER BY product_id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + term.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public void decrementStock(Connection conn, int productId, int qty)
            throws SQLException {
        String sql = "UPDATE product SET stock_availability "
                   + "= stock_availability - ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    private void validate(Product p) {
        if (p == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (p.getProductName() == null
                || p.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name must not be empty.");
        }
        if (p.getDescription() == null
                || p.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product description must not be empty.");
        }
        if (p.getPrice() < 0 || p.getPrice() > MAX_PRICE) {
            throw new IllegalArgumentException(
                    "Price must be between 0 and " + MAX_PRICE + ".");
        }
        if (p.getStockAvailability() < 0) {
            throw new IllegalArgumentException(
                    "Stock availability must be >= 0.");
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("product_name"),
            rs.getString("description"),
            rs.getString("company_name"),
            rs.getDouble("price"),
            rs.getInt("stock_availability")
        );
    }
}
