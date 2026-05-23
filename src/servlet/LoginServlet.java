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
import dao.LoginDAO;
import model.Customer;
import model.Login;
import util.EmailValidator;
import util.PasswordUtil;

/** Handles US003 — User Login (single page for customer + admin). */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LoginDAO loginDAO = new LoginDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = trim(req.getParameter("email"));
        String password = req.getParameter("password");

        if (email == null || password == null
                || email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        if (!EmailValidator.isValid(email)) {
            req.setAttribute("error", "Email format is invalid.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            Login login = loginDAO.findByEmail(email);
            if (login == null
                    || !PasswordUtil.matches(password, login.getPassword())) {
                req.setAttribute("error", "Invalid email or password.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // Customers must be active. Admins can always log in.
            if (login.isCustomer() && !login.isActive()) {
                req.setAttribute("error",
                        "Your account is deactivated. "
                        + "Please reactivate it first.");
                req.setAttribute("inactiveEmail", email);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("login", login);
            session.setAttribute("loginId", login.getLoginId());
            session.setAttribute("userType", login.getUserType());
            session.setAttribute("email", login.getEmail());

            if (login.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/index.jsp");
            } else {
                Customer cust = customerDAO.findByLoginId(login.getLoginId());
                if (cust != null) {
                    session.setAttribute("customer", cust);
                    session.setAttribute("customerId", cust.getCustomerId());
                    session.setAttribute("displayName", cust.getName());
                }
                resp.sendRedirect(req.getContextPath() + "/customer/index.jsp");
            }
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Database error: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
