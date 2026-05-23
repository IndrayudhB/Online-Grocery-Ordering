package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CustomerDAO;
import exception.DuplicateEmailException;
import exception.InvalidEmailException;
import model.Customer;

/** Handles US001 — Customer Registration. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name     = trim(req.getParameter("name"));
        String email    = trim(req.getParameter("email"));
        String contact  = trim(req.getParameter("contactNumber"));
        String address  = trim(req.getParameter("address"));
        String password = req.getParameter("password");

        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            req.setAttribute("error",
                    "Name, email and password are required.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setContactNumber(contact);
        c.setAddress(address);

        try {
            customerDAO.registerCustomer(c, password);
            req.setAttribute("success",
                    "Registration successful! Please log in.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (InvalidEmailException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (DuplicateEmailException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Database error: " + e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }
}
