<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>
<%@ page import="java.util.List, model.Product" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    String error = (String) request.getAttribute("error");
    String q     = (String) request.getAttribute("q");
    if (q == null) q = "";
%>

<section class="card">
    <h2>Product Catalog</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>

    <form method="get" action="<%= ctx %>/customer/catalog" class="inline-form">
        <input type="text" name="q" placeholder="Search by product name..."
               value="<%= q %>">
        <button type="submit" class="btn-secondary">Search</button>
        <% if (!q.isEmpty()) { %>
            <a href="<%= ctx %>/customer/catalog">Clear</a>
        <% } %>
    </form>

    <% if (products == null || products.isEmpty()) { %>
        <p class="muted">No products available right now.</p>
    <% } else { %>
        <table class="data">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Brand</th>
                    <th class="num">Price</th>
                    <th class="num">Stock</th>
                    <th>Add</th>
                </tr>
            </thead>
            <tbody>
            <% for (Product p : products) { %>
                <tr>
                    <td><%= p.getProductId() %></td>
                    <td><%= p.getProductName() %></td>
                    <td><%= p.getDescription() == null ? "" : p.getDescription() %></td>
                    <td><%= p.getCompanyName() == null ? "" : p.getCompanyName() %></td>
                    <td class="num">&#8377; <%= String.format("%.2f", p.getPrice()) %></td>
                    <td class="num"><%= p.getStockAvailability() %></td>
                    <td>
                        <% if (p.getStockAvailability() > 0) { %>
                            <form method="post" action="<%= ctx %>/customer/wishlist" class="inline-form">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" value="<%= p.getProductId() %>">
                                <input type="number" name="quantity" min="1" max="<%= p.getStockAvailability() %>" value="1" class="qty">
                                <button type="submit" class="btn-primary small">Add</button>
                            </form>
                        <% } else { %>
                            <span class="muted">Out of stock</span>
                        <% } %>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    <% } %>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
