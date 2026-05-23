package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.LoginDAO;
import model.Login;
import util.EmailValidator;
import util.PasswordUtil;

/**
 * US011 — Soft delete (deactivate) and US012 — Restore (reactivate)
 * a customer account by toggling the {@code login.status} column.
 *
 * Two modes:
 *   1) Logged-in customer hitting POST /accountStatus?action=deactivate
 *   2) Logged-out user hitting POST /accountStatus?action=reactivate
 *      with email + password (used from the login page when their
 *      account is disabled).
 */
@WebServlet("/accountStatus")
public class AccountStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LoginDAO loginDAO = new LoginDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("deactivate".equals(action)) {
                handleDeactivate(req, resp);
            } else if ("reactivate".equals(action)) {
                handleReactivate(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleDeactivate(HttpServletRequest req,
                                  HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null
                || session.getAttribute("loginId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        int loginId = (Integer) session.getAttribute("loginId");
        loginDAO.updateStatus(loginId, "Inactive");
        session.invalidate();
        resp.sendRedirect(req.getContextPath()
                + "/login.jsp?deactivated=1");
    }

    private void handleReactivate(HttpServletRequest req,
                                  HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        String email    = trim(req.getParameter("email"));
        String password = req.getParameter("password");
        if (email == null || password == null
                || !EmailValidator.isValid(email)) {
            req.setAttribute("error",
                    "Provide a valid email and password to reactivate.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        Login login = loginDAO.findByEmail(email);
        if (login == null
                || !PasswordUtil.matches(password, login.getPassword())) {
            req.setAttribute("error",
                    "Invalid credentials — cannot reactivate.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        if (!login.isCustomer()) {
            req.setAttribute("error",
                    "Only customer accounts can be reactivated here.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        loginDAO.updateStatus(login.getLoginId(), "Active");
        req.setAttribute("success",
                "Account reactivated. You can now log in.");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
