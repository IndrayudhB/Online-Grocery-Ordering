package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.ProductDAO;
import model.Product;
import util.DatabaseConnection;

/**
 * Admin-only CSV bulk upload for products (US004).
 *
 * CSV format (with header):
 *   product_name,description,company_name,price,stock_availability
 *
 * Each row is inserted in a single transaction: if any row fails
 * validation, the whole upload is rolled back.
 */
@WebServlet("/admin/bulkUpload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 10L * 1024 * 1024,
    maxRequestSize    = 11L * 1024 * 1024)
public class BulkUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/admin/bulkUpload.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Part csvPart;
        try {
            csvPart = req.getPart("csvFile");
        } catch (Exception e) {
            req.setAttribute("error",
                    "Could not read upload: " + e.getMessage());
            req.getRequestDispatcher("/admin/bulkUpload.jsp")
               .forward(req, resp);
            return;
        }

        if (csvPart == null || csvPart.getSize() == 0) {
            req.setAttribute("error", "Please choose a CSV file.");
            req.getRequestDispatcher("/admin/bulkUpload.jsp")
               .forward(req, resp);
            return;
        }

        List<Product> parsed = new ArrayList<>();
        List<String> errors  = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvPart.getInputStream(), "UTF-8"))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (lineNo == 1) {
                    // Skip a header row if it looks like one
                    if (line.toLowerCase().contains("product_name")) {
                        continue;
                    }
                }
                if (line.trim().isEmpty()) continue;
                String[] parts = splitCsv(line);
                if (parts.length < 5) {
                    errors.add("Line " + lineNo
                            + ": expected 5 columns, got " + parts.length);
                    continue;
                }
                try {
                    Product p = new Product();
                    p.setProductName(parts[0].trim());
                    p.setDescription(parts[1].trim());
                    p.setCompanyName(parts[2].trim());
                    p.setPrice(Double.parseDouble(parts[3].trim()));
                    p.setStockAvailability(
                            Integer.parseInt(parts[4].trim()));
                    parsed.add(p);
                } catch (NumberFormatException nfe) {
                    errors.add("Line " + lineNo
                            + ": invalid number — " + nfe.getMessage());
                }
            }
        } catch (IOException ioe) {
            req.setAttribute("error",
                    "Failed reading CSV: " + ioe.getMessage());
            req.getRequestDispatcher("/admin/bulkUpload.jsp")
               .forward(req, resp);
            return;
        }

        if (!errors.isEmpty()) {
            req.setAttribute("error",
                    "CSV has errors. Nothing imported. " + errors);
            req.getRequestDispatcher("/admin/bulkUpload.jsp")
               .forward(req, resp);
            return;
        }
        if (parsed.isEmpty()) {
            req.setAttribute("error", "CSV had no data rows.");
            req.getRequestDispatcher("/admin/bulkUpload.jsp")
               .forward(req, resp);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Product p : parsed) {
                    productDAO.insert(conn, p);
                }
                conn.commit();
                req.setAttribute("success",
                        parsed.size() + " product(s) imported successfully.");
            } catch (SQLException | IllegalArgumentException e) {
                conn.rollback();
                req.setAttribute("error",
                        "Import failed and rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            req.setAttribute("error",
                    "Database error: " + e.getMessage());
        }
        req.getRequestDispatcher("/admin/bulkUpload.jsp")
           .forward(req, resp);
    }

    /**
     * Minimal CSV splitter that handles a single pair of double quotes
     * around a field (so commas inside descriptions are tolerated).
     */
    private static String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
