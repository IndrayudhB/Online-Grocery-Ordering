<%@ include file="/WEB-INF/jspf/header-admin.jspf" %>
<%
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<section class="card">
    <h2>Add Product</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="flash success"><%= success %></div>
    <% } %>

    <form method="post" action="<%= ctx %>/admin/products" autocomplete="off">
        <input type="hidden" name="action" value="add">
        <label>Product name
            <input type="text" name="productName" required>
        </label>
        <label>Description
            <textarea name="description" rows="3" required></textarea>
        </label>
        <label>Company / Brand
            <input type="text" name="companyName">
        </label>
        <label>Price
            <input type="number" name="price" min="0" max="100000" step="0.01" required>
        </label>
        <label>Stock availability
            <input type="number" name="stockAvailability" min="0" required>
        </label>
        <button type="submit" class="btn-primary">Create Product</button>
    </form>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
