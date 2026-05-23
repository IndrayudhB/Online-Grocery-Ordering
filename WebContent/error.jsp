<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true"
         import="java.io.PrintWriter,java.io.StringWriter" %>
<%
    String ctx = request.getContextPath();
    Integer statusObj = (Integer) request
            .getAttribute("javax.servlet.error.status_code");
    int status = statusObj == null ? 500 : statusObj.intValue();

    Throwable thrown = (Throwable) request
            .getAttribute("javax.servlet.error.exception");
    if (thrown == null) thrown = exception; // implicit JSP variable

    // Walk down to the deepest cause so the real exception is shown
    Throwable rootCause = thrown;
    while (rootCause != null && rootCause.getCause() != null
            && rootCause.getCause() != rootCause) {
        rootCause = rootCause.getCause();
    }

    String message = (String) request
            .getAttribute("javax.servlet.error.message");
    if (message == null && rootCause != null) {
        message = rootCause.getClass().getSimpleName()
                + ": " + rootCause.getMessage();
    }

    String stackTrace = null;
    if (thrown != null) {
        StringWriter sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));
        stackTrace = sw.toString();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Something went wrong</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<style>
    .auth-card.wide-debug { max-width: 880px; }
    .auth-card pre {
        background: #1c1c1c; color: #f0f0f0;
        padding: 1rem; border-radius: 6px;
        overflow: auto; max-height: 380px;
        font-size: 0.78rem; line-height: 1.35;
        white-space: pre; word-wrap: normal;
    }
    .auth-card details { margin-top: 1rem; }
    .auth-card summary { cursor: pointer; font-weight: 600; }
</style>
</head>
<body class="auth-bg">
<div class="auth-card wide-debug">
    <h1>Oops!</h1>
    <h2>Status <%= status %></h2>
    <% if (message != null) { %>
        <div class="flash error"><%= message %></div>
    <% } else { %>
        <div class="flash error">An unexpected error occurred.</div>
    <% } %>

    <% if (rootCause != null && rootCause != thrown) { %>
        <p class="muted">
            <strong>Root cause:</strong>
            <%= rootCause.getClass().getName() %>
            <% if (rootCause.getMessage() != null) { %>
                &mdash; <%= rootCause.getMessage() %>
            <% } %>
        </p>
    <% } %>

    <% if (stackTrace != null) { %>
        <details open>
            <summary>Stack trace (for debugging)</summary>
            <pre><%= stackTrace.replace("<","&lt;").replace(">","&gt;") %></pre>
        </details>
    <% } %>

    <p class="muted">
        <a href="<%= ctx %>/login.jsp">Back to login</a>
        &middot;
        <a href="javascript:history.back()">Go back</a>
    </p>
</div>
</body>
</html>
