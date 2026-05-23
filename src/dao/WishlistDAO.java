package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Wishlist;
import util.DatabaseConnection;

/** JDBC access to the {@code wishlist} table. */
public class WishlistDAO {

    /**
     * Adds the product to the user's wishlist, or increments quantity
     * if it is already present.
     */
    public void addOrIncrement(int userId, int productId, int quantity)
            throws SQLException {
        if (quantity <= 0) {
            quantity = 1;
        }
        try (Connection c = DatabaseConnection.getConnection()) {
            // Make sure the product actually exists
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT product_id FROM product WHERE product_id = ?")) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Product not found: " + productId);
                    }
                }
            }

            // Already in wishlist? increment.
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT wishlist_id, quantity FROM wishlist "
                    + "WHERE user_id = ? AND product_id = ?")) {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int wishlistId = rs.getInt("wishlist_id");
                        int existing   = rs.getInt("quantity");
                        try (PreparedStatement up = c.prepareStatement(
                                "UPDATE wishlist SET quantity = ? "
                                + "WHERE wishlist_id = ?")) {
                            up.setInt(1, existing + quantity);
                            up.setInt(2, wishlistId);
                            up.executeUpdate();
                        }
                        return;
                    }
                }
            }

            // Otherwise insert
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO wishlist (user_id, product_id, quantity) "
                    + "VALUES (?, ?, ?)")) {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }
        }
    }

    public List<Wishlist> findByUser(int userId) throws SQLException {
        List<Wishlist> list = new ArrayList<>();
        String sql = "SELECT w.wishlist_id, w.user_id, w.product_id, "
                   + "w.quantity, p.product_name, p.price, "
                   + "p.stock_availability "
                   + "FROM wishlist w "
                   + "JOIN product p ON p.product_id = w.product_id "
                   + "WHERE w.user_id = ? "
                   + "ORDER BY w.wishlist_id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Wishlist w = new Wishlist(
                        rs.getInt("wishlist_id"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"));
                    w.setProductName(rs.getString("product_name"));
                    w.setPrice(rs.getDouble("price"));
                    w.setStockAvailability(rs.getInt("stock_availability"));
                    list.add(w);
                }
            }
        }
        return list;
    }

    public void updateQuantity(int wishlistId, int userId, int quantity)
            throws SQLException {
        if (quantity <= 0) {
            throw new SQLException("Quantity must be positive.");
        }
        String sql = "UPDATE wishlist SET quantity = ? "
                   + "WHERE wishlist_id = ? AND user_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, wishlistId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void remove(int wishlistId, int userId) throws SQLException {
        String sql = "DELETE FROM wishlist "
                   + "WHERE wishlist_id = ? AND user_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, wishlistId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void clearForUser(Connection conn, int userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM wishlist WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
