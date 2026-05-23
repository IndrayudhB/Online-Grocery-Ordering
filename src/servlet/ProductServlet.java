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

/**
 * Admin-only product CRUD. Action discriminated by {@code action} parameter:
 *   list   -> show product table (default)
 *   add    -> insert
 *   edit   -> show edit form
 *   update -> save edits
 *   delete -> remove
 */
@WebServlet("/admin/products")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Product p = productDAO.findById(id);
                if (p == null) {
                    req.setAttribute("error",
                            "Product " + id + " was not found.");
                    listAndForward(req, resp);
                    return;
                }
                req.setAttribute("product", p);
                req.getRequestDispatcher("/admin/editProduct.jsp")
                   .forward(req, resp);
                return;
            }
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                productDAO.delete(id);
                req.setAttribute("success",
                        "Product " + id + " deleted.");
                listAndForward(req, resp);
                return;
            }
            // default = list
            listAndForward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid product id.");
            listAndForward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
            listAndForward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("update".equals(action)) {
                Product p = readProduct(req);
                p.setProductId(Integer.parseInt(req.getParameter("productId")));
                productDAO.update(p);
                req.setAttribute("success",
                        "Product " + p.getProductId() + " updated.");
                listAndForward(req, resp);
                return;
            }
            // default = add
            Product p = readProduct(req);
            int newId = productDAO.insert(p);
            req.setAttribute("success",
                    "Product created successfully (id=" + newId + ").");
            req.getRequestDispatcher("/admin/addProduct.jsp")
               .forward(req, resp);
        } catch (IllegalArgumentException e) {
            // NumberFormatException extends IllegalArgumentException, so
            // bad number inputs from the form land here too.
            req.setAttribute("error", e.getMessage());
            if ("update".equals(action)) {
                try { req.setAttribute("product", readProduct(req)); }
                catch (Exception ignored) {}
                req.getRequestDispatcher("/admin/editProduct.jsp")
                   .forward(req, resp);
            } else {
                req.getRequestDispatcher("/admin/addProduct.jsp")
                   .forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/admin/addProduct.jsp")
               .forward(req, resp);
        }
    }

    private Product readProduct(HttpServletRequest req) {
        Product p = new Product();
        p.setProductName(trim(req.getParameter("productName")));
        p.setDescription(trim(req.getParameter("description")));
        p.setCompanyName(trim(req.getParameter("companyName")));
        p.setPrice(parseDouble(req.getParameter("price")));
        p.setStockAvailability(parseInt(req.getParameter("stockAvailability")));
        return p;
    }

    /**
     * Loads the product list and forwards to productInfo.jsp.
     *
     * Catches SQLException internally so callers (especially other
     * catch blocks) don't need to deal with it. If the load fails,
     * the JSP renders with a null products list and the existing
     * "error" request attribute is preserved.
     */
    private void listAndForward(HttpServletRequest req,
                                HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Product> products = productDAO.findAll();
            req.setAttribute("products", products);
        } catch (SQLException e) {
            if (req.getAttribute("error") == null) {
                req.setAttribute("error",
                        "Database error: " + e.getMessage());
            }
        }
        req.getRequestDispatcher("/admin/productInfo.jsp")
           .forward(req, resp);
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid price.");
        }
    }
    private static int parseInt(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid stock value.");
        }
    }
}
