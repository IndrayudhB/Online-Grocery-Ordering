package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ProductDAO;
import dao.WishlistDAO;
import model.Order;
import model.Wishlist;
import util.DatabaseConnection;

/**
 * US008 — Place Order. Converts the current user's wishlist into one or
 * more rows in {@code grocery_order}, decrements stock atomically, and
 * clears the wishlist.
 */
@WebServlet("/customer/placeOrder")
public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final ProductDAO  productDAO  = new ProductDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");
        Integer loginId    = (Integer) session.getAttribute("loginId");
        if (customerId == null || loginId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<Wishlist> items = wishlistDAO.findByUser(loginId);
            if (items.isEmpty()) {
                session.setAttribute("flashError",
                        "Your cart is empty.");
                resp.sendRedirect(req.getContextPath()
                        + "/customer/wishlist");
                return;
            }

            // Stock check first, before opening a transaction.
            for (Wishlist w : items) {
                if (w.getQuantity() > w.getStockAvailability()) {
                    session.setAttribute("flashError",
                            "Not enough stock for " + w.getProductName()
                            + " (have " + w.getStockAvailability()
                            + ", asked " + w.getQuantity() + ").");
                    resp.sendRedirect(req.getContextPath()
                            + "/customer/wishlist");
                    return;
                }
            }

            List<Integer> orderIds = new ArrayList<>();
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    String now = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss").format(new Date());

                    for (Wishlist w : items) {
                        // Insert order row
                        try (java.sql.PreparedStatement ps =
                                conn.prepareStatement(
                                  "INSERT INTO grocery_order "
                                + "(customer_id, product_id, quantity, "
                                + "order_date) VALUES (?, ?, ?, ?)",
                                java.sql.Statement.RETURN_GENERATED_KEYS)) {
                            ps.setInt(1, customerId);
                            ps.setInt(2, w.getProductId());
                            ps.setInt(3, w.getQuantity());
                            ps.setString(4, now);
                            ps.executeUpdate();
                            try (java.sql.ResultSet keys =
                                    ps.getGeneratedKeys()) {
                                if (keys.next()) {
                                    orderIds.add(keys.getInt(1));
                                }
                            }
                        }
                        productDAO.decrementStock(conn,
                                w.getProductId(), w.getQuantity());
                    }
                    wishlistDAO.clearForUser(conn, loginId);
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

            // Build a tiny summary for the confirmation page
            List<Order> placed = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                Wishlist w = items.get(i);
                Order o = new Order();
                o.setOrderId(i < orderIds.size() ? orderIds.get(i) : 0);
                o.setProductId(w.getProductId());
                o.setQuantity(w.getQuantity());
                o.setProductName(w.getProductName());
                o.setPrice(w.getPrice());
                placed.add(o);
            }
            session.setAttribute("lastOrder", placed);
            resp.sendRedirect(req.getContextPath()
                    + "/customer/orderConfirmation.jsp");
        } catch (SQLException e) {
            session.setAttribute("flashError",
                    "Order failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customer/wishlist");
        }
    }
}
