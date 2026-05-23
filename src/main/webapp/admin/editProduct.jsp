<%@ include file="/WEB-INF/jspf/header-admin.jspf" %>
<%@ page import="model.Product" %>
<%
    Product p = (Product) request.getAttribute("product");
    String error = (String) request.getAttribute("error");
%>

<section class="card">
    <h2>Edit Product</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>

    <% if (p == null) { %>
        <p class="muted">Product not found.
            <a href="<%= ctx %>/admin/products">Back to list</a></p>
    <% } else { %>
        <form method="post" action="<%= ctx %>/admin/products" autocomplete="off">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="productId" value="<%= p.getProductId() %>">
            <label>Product ID
                <input type="text" value="<%= p.getProductId() %>" disabled>
            </label>
            <label>Product name
                <input type="text" name="productName" required value="<%= p.getProductName() %>">
            </label>
            <label>Description
                <textarea name="description" rows="3" required><%= p.getDescription() == null ? "" : p.getDescription() %></textarea>
            </label>
            <label>Company / Brand
                <input type="text" name="companyName" value="<%= p.getCompanyName() == null ? "" : p.getCompanyName() %>">
            </label>
            <label>Price
                <input type="number" name="price" min="0" max="100000" step="0.01" required value="<%= p.getPrice() %>">
            </label>
            <label>Stock availability
                <input type="number" name="stockAvailability" min="0" required value="<%= p.getStockAvailability() %>">
            </label>
            <div class="cta-row">
                <button type="submit" class="btn-primary">Save</button>
                <a class="btn-secondary" href="<%= ctx %>/admin/products">Cancel</a>
            </div>
        </form>
    <% } %>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
