<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%
    String ctx = request.getContextPath();
    String error   = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Register — Grocery App</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body class="auth-bg">
<div class="auth-card wide">
    <h1>Create your account</h1>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <form method="post" action="<%= ctx %>/register" autocomplete="off" novalidate>
        <label>Full name
            <input type="text" name="name" required>
        </label>
        <label>Email
            <input type="email" name="email" required>
        </label>
        <label>Contact number
            <input type="text" name="contactNumber">
        </label>
        <label>Address
            <textarea name="address" rows="2"></textarea>
        </label>
        <label>Password
            <input type="password" name="password" required minlength="4">
        </label>
        <button type="submit" class="btn-primary">Register</button>
    </form>
    <p class="muted">
        Already registered?
        <a href="<%= ctx %>/login.jsp">Sign in</a>
    </p>
</div>
</body>
</html>
