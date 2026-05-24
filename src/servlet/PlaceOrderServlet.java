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
 * US008 - Place Order. Validates the dummy payment details submitted
 * by the payment page, then converts the current user's wishlist into
 * one or more rows in {@code grocery_order}, decrements stock atomically,
 * and clears the wishlist.
 *
 * Payment processing here is intentionally fake - no real gateway call
 * is made. The point of the validation is only to keep the demo flow
 * realistic (UPI ID format, 16-digit card number, etc.).
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

        // 1. Validate the (dummy) payment details. If anything is wrong,
        //    bounce the user back to the payment page with a flash error.
        String methodLabel = validatePayment(req, session);
        if (methodLabel == null) {
            resp.sendRedirect(req.getContextPath() + "/customer/checkout");
            return;
        }

        // 2. Now do the actual order placement.
        try {
            List<Wishlist> items = wishlistDAO.findByUser(loginId);
            if (items.isEmpty()) {
                session.setAttribute("flashError",
                        "Your cart is empty.");
                resp.sendRedirect(req.getContextPath()
                        + "/customer/wishlist");
                return;
            }

            // Stock re-check (cart may have changed since the payment
            // page loaded).
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
            session.setAttribute("paymentMethod", methodLabel);
            resp.sendRedirect(req.getContextPath()
                    + "/customer/orderConfirmation.jsp");
        } catch (SQLException e) {
            session.setAttribute("flashError",
                    "Order failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customer/wishlist");
        }
    }

    /**
     * Performs lightweight validation on the chosen payment method.
     * Returns a human-readable label (e.g. "UPI (alice@okhdfc)") on
     * success, or {@code null} after setting a flash error on failure.
     */
    private String validatePayment(HttpServletRequest req,
                                   HttpSession session) {
        String method = trim(req.getParameter("paymentMethod"));
        if (method == null || method.isEmpty()) {
            session.setAttribute("flashError",
                    "Please choose a payment method.");
            return null;
        }
        switch (method.toLowerCase()) {
            case "cod":
                return "Cash on Delivery";
            case "upi": {
                String upi = trim(req.getParameter("upiId"));
                if (upi == null
                        || !upi.matches("^[A-Za-z0-9._-]+@[A-Za-z]+$")) {
                    session.setAttribute("flashError",
                            "Please enter a valid UPI ID like name@bank.");
                    return null;
                }
                return "UPI (" + upi + ")";
            }
            case "card": {
                String num = trim(req.getParameter("cardNumber"));
                String cvv = trim(req.getParameter("cardCvv"));
                String exp = trim(req.getParameter("cardExpiry"));
                if (num == null) {
                    session.setAttribute("flashError",
                            "Card number is required.");
                    return null;
                }
                String numClean = num.replaceAll("\\s", "");
                if (!numClean.matches("^\\d{16}$")) {
                    session.setAttribute("flashError",
                            "Card number must be 16 digits.");
                    return null;
                }
                if (cvv == null || !cvv.matches("^\\d{3,4}$")) {
                    session.setAttribute("flashError",
                            "CVV must be 3 or 4 digits.");
                    return null;
                }
                if (exp == null
                        || !exp.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                    session.setAttribute("flashError",
                            "Expiry must be in MM/YY format.");
                    return null;
                }
                String last4 = numClean.substring(12);
                return "Card ending **** " + last4;
            }
            default:
                session.setAttribute("flashError",
                        "Unknown payment method.");
                return null;
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
