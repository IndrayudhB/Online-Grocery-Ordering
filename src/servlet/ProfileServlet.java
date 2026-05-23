package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CustomerDAO;
import exception.DuplicateEmailException;
import exception.InvalidEmailException;
import model.Customer;

/** US010 — Customer profile view + update. */
@WebServlet("/customer/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try {
            Customer c = customerDAO.findByLoginId(loginId);
            req.setAttribute("customer", c);
            req.getRequestDispatcher("/customer/profile.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Could not load profile: " + e.getMessage());
            req.getRequestDispatcher("/customer/profile.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer loginId = (Integer) session.getAttribute("loginId");
        if (loginId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try {
            Customer c = customerDAO.findByLoginId(loginId);
            if (c == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            c.setName(trim(req.getParameter("name")));
            c.setEmail(trim(req.getParameter("email")));
            c.setContactNumber(trim(req.getParameter("contactNumber")));
            c.setAddress(trim(req.getParameter("address")));
            customerDAO.update(c);

            session.setAttribute("customer", c);
            session.setAttribute("displayName", c.getName());
            session.setAttribute("email", c.getEmail());

            req.setAttribute("customer", c);
            req.setAttribute("success", "Profile updated successfully.");
            req.getRequestDispatcher("/customer/profile.jsp")
               .forward(req, resp);
        } catch (InvalidEmailException | DuplicateEmailException e) {
            req.setAttribute("error", e.getMessage());
            try {
                req.setAttribute("customer",
                        customerDAO.findByLoginId(loginId));
            } catch (SQLException ignored) {}
            req.getRequestDispatcher("/customer/profile.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Database error: " + e.getMessage());
            req.getRequestDispatcher("/customer/profile.jsp")
               .forward(req, resp);
        }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
