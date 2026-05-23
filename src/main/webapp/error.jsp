<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%
    String ctx = request.getContextPath();
    Integer statusObj = (Integer) request
            .getAttribute("javax.servlet.error.status_code");
    int status = statusObj == null ? 500 : statusObj.intValue();
    Throwable thrown = (Throwable) request
            .getAttribute("javax.servlet.error.exception");
    String message = (String) request
            .getAttribute("javax.servlet.error.message");
    if (message == null && thrown != null) message = thrown.getMessage();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Something went wrong</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body class="auth-bg">
<div class="auth-card">
    <h1>Oops!</h1>
    <h2>Status <%= status %></h2>
    <% if (message != null) { %>
        <div class="flash error"><%= message %></div>
    <% } else { %>
        <div class="flash error">An unexpected error occurred.</div>
    <% } %>
    <p class="muted">
        <a href="<%= ctx %>/login.jsp">Back to login</a>
    </p>
</div>
</body>
</html>
