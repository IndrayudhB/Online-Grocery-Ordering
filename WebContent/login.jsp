<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%
    String ctx = request.getContextPath();
    String error      = (String) request.getAttribute("error");
    String success    = (String) request.getAttribute("success");
    String inactive   = (String) request.getAttribute("inactiveEmail");
    String loggedOut  = request.getParameter("loggedOut");
    String expired    = request.getParameter("expired");
    String deactNotif = request.getParameter("deactivated");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Sign In — Grocery App</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body class="auth-bg">
<div class="auth-card">
    <h1>Grocery App</h1>
    <h2>Sign in</h2>

    <% if (loggedOut != null) { %>
        <div class="flash success">You have been logged out.</div>
    <% } %>
    <% if (expired != null) { %>
        <div class="flash error">Please sign in to continue.</div>
    <% } %>
    <% if (deactNotif != null) { %>
        <div class="flash success">Your account has been deactivated.</div>
    <% } %>
    <% if (success != null) { %>
        <div class="flash success"><%= success %></div>
    <% } %>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>

    <form method="post" action="<%= ctx %>/login" autocomplete="off" novalidate>
        <label>Email
            <input type="email" name="email" required
                   value="<%= inactive == null ? "" : inactive %>">
        </label>
        <label>Password
            <input type="password" name="password" required>
        </label>
        <button type="submit" class="btn-primary">Sign in</button>
    </form>

    <% if (inactive != null) { %>
        <hr>
        <p>Your account is currently <strong>Inactive</strong>. Reactivate it:</p>
        <form method="post" action="<%= ctx %>/accountStatus" autocomplete="off">
            <input type="hidden" name="action" value="reactivate">
            <input type="hidden" name="email" value="<%= inactive %>">
            <label>Confirm password
                <input type="password" name="password" required>
            </label>
            <button type="submit" class="btn-secondary">Reactivate Account</button>
        </form>
    <% } %>

    <p class="muted">
        Don't have an account?
        <a href="<%= ctx %>/register.jsp">Create one</a>
    </p>
</div>
</body>
</html>
