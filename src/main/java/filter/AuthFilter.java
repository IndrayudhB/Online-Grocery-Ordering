package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Login;

/**
 * Role-based access control:
 *  - Anything under /admin/*    requires session with userType=admin
 *  - Anything under /customer/* requires session with userType=customer
 *  - Cross-role access is bounced back to that user's home page
 *  - Unauthenticated access redirects to /login.jsp
 *
 * Also writes no-cache headers on every protected response so the
 * browser back-button cannot resurrect protected pages after logout.
 */
@WebFilter(urlPatterns = {"/admin/*", "/customer/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String ctx  = req.getContextPath();
        String uri  = req.getRequestURI();
        String path = uri.substring(ctx.length()); // e.g. /admin/index.jsp

        // Disable caching on all protected pages.
        resp.setHeader("Cache-Control",
                "no-cache, no-store, must-revalidate, private");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        Login login = (session == null) ? null
                : (Login) session.getAttribute("login");

        if (login == null) {
            resp.sendRedirect(ctx + "/login.jsp?expired=1");
            return;
        }

        boolean isAdminArea    = path.startsWith("/admin/");
        boolean isCustomerArea = path.startsWith("/customer/");

        if (isAdminArea && !login.isAdmin()) {
            resp.sendRedirect(ctx + "/customer/index.jsp");
            return;
        }
        if (isCustomerArea && !login.isCustomer()) {
            resp.sendRedirect(ctx + "/admin/index.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
