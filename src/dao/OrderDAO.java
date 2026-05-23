package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Order;
import util.DatabaseConnection;

/** JDBC access to the {@code grocery_order} table. */
public class OrderDAO {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Places one or more order rows in a single transaction and decrements
     * the corresponding product stock. Returns the list of new order IDs.
     */
    public List<Integer> placeOrder(int customerId, List<Order> items)
            throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("Order is empty.");
        }
        List<Integer> orderIds = new ArrayList<>();
        ProductDAO productDAO = new ProductDAO();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String now;
                synchronized (DATE_FMT) {
                    now = DATE_FMT.format(new Date());
                }

                String sql = "INSERT INTO grocery_order "
                           + "(customer_id, product_id, quantity, order_date) "
                           + "VALUES (?, ?, ?, ?)";
                for (Order o : items) {
                    try (PreparedStatement ps = conn.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, customerId);
                        ps.setInt(2, o.getProductId());
                        ps.setInt(3, o.getQuantity());
                        ps.setString(4, now);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) {
                                orderIds.add(keys.getInt(1));
                            }
                        }
                    }
                    productDAO.decrementStock(conn, o.getProductId(),
                            o.getQuantity());
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return orderIds;
    }

    public List<Order> findByCustomer(int customerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, o.product_id, "
                   + "o.quantity, o.order_date, "
                   + "p.product_name, p.price "
                   + "FROM grocery_order o "
                   + "LEFT JOIN product p ON p.product_id = o.product_id "
                   + "WHERE o.customer_id = ? "
                   + "ORDER BY o.order_id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getString("order_date"));
                    o.setProductName(rs.getString("product_name"));
                    o.setPrice(rs.getDouble("price"));
                    list.add(o);
                }
            }
        }
        return list;
    }

    public void updateQuantity(int orderId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new SQLException("Quantity must be positive.");
        }
        String sql = "UPDATE grocery_order SET quantity = ? WHERE order_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }
}
