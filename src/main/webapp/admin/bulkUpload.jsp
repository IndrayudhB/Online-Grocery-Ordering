<%@ include file="/WEB-INF/jspf/header-admin.jspf" %>
<%
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<section class="card">
    <h2>Bulk Upload Products (CSV)</h2>
    <p class="muted">
        Expected columns:
        <code>product_name, description, company_name, price, stock_availability</code>
    </p>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="flash success"><%= success %></div>
    <% } %>

    <form method="post" action="<%= ctx %>/admin/bulkUpload"
          enctype="multipart/form-data">
        <label>CSV file
            <input type="file" name="csvFile" accept=".csv,text/csv" required>
        </label>
        <button type="submit" class="btn-primary">Upload</button>
        <a class="btn-secondary" href="<%= ctx %>/sample-products.csv">Download sample CSV</a>
    </form>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
