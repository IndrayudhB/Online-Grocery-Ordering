<%@ include file="/WEB-INF/jspf/header-admin.jspf" %>
<%@ page import="java.util.List, model.Product" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<section class="card">
    <h2>Product Info</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="flash success"><%= success %></div>
    <% } %>

    <% if (products == null || products.isEmpty()) { %>
        <p class="muted">No products yet.
            <a href="<%= ctx %>/admin/addProduct.jsp">Add your first product</a>.</p>
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
                    <th>Update</th>
                    <th>Delete</th>
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
                        <a class="btn-secondary small"
                           href="<%= ctx %>/admin/products?action=edit&id=<%= p.getProductId() %>">
                           Update</a>
                    </td>
                    <td>
                        <form method="get" action="<%= ctx %>/admin/products"
                              onsubmit="return confirm('Delete product <%= p.getProductId() %>?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= p.getProductId() %>">
                            <button type="submit" class="btn-danger small">Delete</button>
                        </form>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    <% } %>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
