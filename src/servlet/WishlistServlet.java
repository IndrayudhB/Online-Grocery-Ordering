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
 * Customer wishlist (acts as the shopping cart):
 *   GET                   -> list current user's wishlist
 *   POST action=add       -> add or increment a product
 *   POST action=update    -> change quantity
 *   POST action=remove    -> delete entry
 */
@WebServlet("/customer/wishlist")
public class WishlistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("loginId");
        try {
            List<Wishlist> items = wishlistDAO.findByUser(userId);
            req.setAttribute("items", items);
            double total = 0;
            for (Wishlist w : items) total += w.getLineTotal();
            req.setAttribute("cartTotal", total);
            req.getRequestDispatcher("/customer/wishlist.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Could not load wishlist: " + e.getMessage());
            req.getRequestDispatcher("/customer/wishlist.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("loginId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                int productId = Integer.parseInt(req.getParameter("productId"));
                int quantity  = parseQty(req.getParameter("quantity"), 1);
                wishlistDAO.addOrIncrement(userId, productId, quantity);
                session.setAttribute("flash",
                        "Product added to wishlist.");
                resp.sendRedirect(req.getContextPath()
                        + "/customer/catalog");
                return;
            }
            if ("update".equals(action)) {
                int wishlistId = Integer.parseInt(
                        req.getParameter("wishlistId"));
                int quantity   = parseQty(req.getParameter("quantity"), 1);
                wishlistDAO.updateQuantity(wishlistId, userId, quantity);
                session.setAttribute("flash",
                        "Quantity updated.");
                resp.sendRedirect(req.getContextPath()
                        + "/customer/wishlist");
                return;
            }
            if ("remove".equals(action)) {
                int wishlistId = Integer.parseInt(
                        req.getParameter("wishlistId"));
                wishlistDAO.remove(wishlistId, userId);
                session.setAttribute("flash",
                        "Item removed from wishlist.");
                resp.sendRedirect(req.getContextPath()
                        + "/customer/wishlist");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/customer/wishlist");
        } catch (NumberFormatException | SQLException e) {
            session.setAttribute("flashError",
                    "Action failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customer/wishlist");
        }
    }

    private static int parseQty(String s, int defaultVal) {
        if (s == null || s.trim().isEmpty()) return defaultVal;
        try {
            int q = Integer.parseInt(s.trim());
            return q > 0 ? q : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
