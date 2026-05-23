<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>
<%@ page import="java.util.List, model.Order" %>
<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String error = (String) request.getAttribute("error");
%>

<section class="card">
    <h2>Order History</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>
    <% if (orders == null || orders.isEmpty()) { %>
        <p class="muted">You have not placed any orders yet.</p>
    <% } else { %>
        <table class="data">
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Date</th>
                    <th>Product</th>
                    <th class="num">Qty</th>
                    <th class="num">Price</th>
                    <th class="num">Total</th>
                </tr>
            </thead>
            <tbody>
            <% for (Order o : orders) { %>
                <tr>
                    <td>#<%= o.getOrderId() %></td>
                    <td><%= o.getOrderDate() == null ? "" : o.getOrderDate() %></td>
                    <td><%= o.getProductName() == null ? ("Product " + o.getProductId()) : o.getProductName() %></td>
                    <td class="num"><%= o.getQuantity() %></td>
                    <td class="num">&#8377; <%= String.format("%.2f", o.getPrice()) %></td>
                    <td class="num">&#8377; <%= String.format("%.2f", o.getLineTotal()) %></td>
                </tr>
            <% } %>
            </tbody>
        </table>
    <% } %>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
