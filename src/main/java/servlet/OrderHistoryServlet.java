package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.OrderDAO;
import model.Order;

/** US009 — list a customer's previous orders. */
@WebServlet("/customer/orderHistory")
public class OrderHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer customerId =
                (Integer) req.getSession().getAttribute("customerId");
        if (customerId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try {
            List<Order> orders = orderDAO.findByCustomer(customerId);
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/customer/orderHistory.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Could not load orders: " + e.getMessage());
            req.getRequestDispatcher("/customer/orderHistory.jsp")
               .forward(req, resp);
        }
    }
}
