package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.WishlistDAO;
import model.Wishlist;

/**
 * Step 1 of checkout: validates the cart (not empty, stock sufficient)
 * and forwards to the payment page so the customer can pick a method
 * before the order is actually written to the database.
 *
 * Triggered when the customer clicks "Place Order" on the wishlist page.
 * The actual order placement still happens in {@link PlaceOrderServlet}
 * once the payment form is submitted.
 */
@WebServlet("/customer/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showPaymentPage(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Reached via redirect after a payment-form validation error.
        showPaymentPage(req, resp);
    }

    private void showPaymentPage(HttpServletRequest req,
                                 HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<Wishlist> items = wishlistDAO.findByUser(loginId);
            if (items.isEmpty()) {
                session.setAttribute("flashError", "Your cart is empty.");
                resp.sendRedirect(req.getContextPath()
                        + "/customer/wishlist");
                return;
            }

            // Stock check up-front so we don't show the payment page if
            // the customer can't actually complete the order.
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

            double total = 0;
            for (Wishlist w : items) {
                total += w.getLineTotal();
            }

            req.setAttribute("items", items);
            req.setAttribute("cartTotal", total);
            req.getRequestDispatcher("/customer/payment.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            session.setAttribute("flashError",
                    "Checkout failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customer/wishlist");
        }
    }
}
