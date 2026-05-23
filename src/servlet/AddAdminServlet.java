package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.LoginDAO;
import model.Login;
import util.EmailValidator;
import util.PasswordUtil;

/**
 * Admin-only: adds another admin account into the {@code login} table.
 * Customer registration is handled by RegisterServlet.
 */
@WebServlet("/admin/addAdmin")
public class AddAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LoginDAO loginDAO = new LoginDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/admin/addAdmin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = trim(req.getParameter("email"));
        String password = req.getParameter("password");

        if (email == null || email.isEmpty()
                || password == null || password.isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/admin/addAdmin.jsp").forward(req, resp);
            return;
        }
        if (!EmailValidator.isValid(email)) {
            req.setAttribute("error", "Email format is invalid.");
            req.getRequestDispatcher("/admin/addAdmin.jsp").forward(req, resp);
            return;
        }

        try {
            if (loginDAO.existsByEmail(email)) {
                req.setAttribute("error",
                        "An account already exists for " + email);
                req.getRequestDispatcher("/admin/addAdmin.jsp")
                   .forward(req, resp);
                return;
            }
            Login admin = new Login(0, email,
                    PasswordUtil.hash(password), "admin", "Active");
            loginDAO.insert(admin);
            req.setAttribute("success",
                    "Admin '" + email + "' created successfully.");
            req.getRequestDispatcher("/admin/addAdmin.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/admin/addAdmin.jsp").forward(req, resp);
        }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
