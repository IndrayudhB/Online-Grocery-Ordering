<%@ include file="/WEB-INF/jspf/header-admin.jspf" %>

<section class="hero">
    <h1>Admin Dashboard</h1>
    <p>Manage your product catalog and admin team.</p>
    <div class="cta-row">
        <a class="btn-primary"   href="<%= ctx %>/admin/addProduct.jsp">Add Product</a>
        <a class="btn-secondary" href="<%= ctx %>/admin/products">Product Info</a>
        <a class="btn-secondary" href="<%= ctx %>/admin/bulkUpload.jsp">Bulk Upload (CSV)</a>
        <a class="btn-secondary" href="<%= ctx %>/admin/addAdmin.jsp">Add Admin</a>
    </div>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
