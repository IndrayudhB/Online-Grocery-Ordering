package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ProductDAO;
import model.Product;

/** Customer product catalog with optional name search. */
@WebServlet("/customer/catalog")
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = req.getParameter("q");
        try {
            List<Product> products = (q == null || q.trim().isEmpty())
                    ? productDAO.findAll()
                    : productDAO.searchByName(q);
            req.setAttribute("products", products);
            req.setAttribute("q", q == null ? "" : q);
            req.getRequestDispatcher("/customer/catalog.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Could not load catalog: " + e.getMessage());
            req.getRequestDispatcher("/customer/catalog.jsp")
               .forward(req, resp);
        }
    }
}
