<%@ include file="/WEB-INF/jspf/header-admin.jspf" %>
<%
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<section class="card">
    <h2>Add Admin</h2>
    <p class="muted">Create another admin account. Only admins can see this page.</p>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="flash success"><%= success %></div>
    <% } %>

    <form method="post" action="<%= ctx %>/admin/addAdmin" autocomplete="off">
        <label>Admin email
            <input type="email" name="email" required>
        </label>
        <label>Password
            <input type="password" name="password" required minlength="4">
        </label>
        <button type="submit" class="btn-primary">Create Admin</button>
    </form>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
