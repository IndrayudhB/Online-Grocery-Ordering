<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>
<%@ page import="model.Customer" %>
<%
    Customer c = (Customer) request.getAttribute("customer");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<section class="card">
    <h2>My Profile</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="flash success"><%= success %></div>
    <% } %>

    <% if (c == null) { %>
        <p class="muted">Profile not found.</p>
    <% } else { %>
        <form method="post" action="<%= ctx %>/customer/profile" autocomplete="off">
            <label>Full name
                <input type="text" name="name" required value="<%= c.getName() == null ? "" : c.getName() %>">
            </label>
            <label>Email
                <input type="email" name="email" required value="<%= c.getEmail() == null ? "" : c.getEmail() %>">
            </label>
            <label>Contact number
                <input type="text" name="contactNumber" value="<%= c.getContactNumber() == null ? "" : c.getContactNumber() %>">
            </label>
            <label>Address
                <textarea name="address" rows="3"><%= c.getAddress() == null ? "" : c.getAddress() %></textarea>
            </label>
            <button type="submit" class="btn-primary">Save Changes</button>
        </form>
    <% } %>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
