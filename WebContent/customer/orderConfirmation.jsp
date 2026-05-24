<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>
<%@ page import="java.util.List, model.Order" %>
<%
    List<Order> placed = (List<Order>) session.getAttribute("lastOrder");
    String paymentMethod = (String) session.getAttribute("paymentMethod");
    if (placed != null)        session.removeAttribute("lastOrder");
    if (paymentMethod != null) session.removeAttribute("paymentMethod");
%>

<section class="card">
    <h2>&#10003; Order placed successfully</h2>
    <% if (placed == null || placed.isEmpty()) { %>
        <p>No order summary available.
            <a href="<%= ctx %>/customer/catalog">Continue shopping</a>.</p>
    <% } else { %>
        <p class="muted">Your order has been recorded. Below are the details:</p>
        <% if (paymentMethod != null) { %>
            <p>
                <strong>Payment method:</strong> <%= paymentMethod %>
                <% if ("Cash on Delivery".equals(paymentMethod)) { %>
                    &mdash; pay on delivery.
                <% } else { %>
                    &mdash; payment confirmed (demo).
                <% } %>
            </p>
        <% } %>
        <table class="data">
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Product</th>
                    <th class="num">Qty</th>
                    <th class="num">Price</th>
                    <th class="num">Subtotal</th>
                </tr>
            </thead>
            <tbody>
            <%
                double grand = 0;
                for (Order o : placed) {
                    double sub = o.getQuantity() * o.getPrice();
                    grand += sub;
            %>
                <tr>
                    <td>#<%= o.getOrderId() %></td>
                    <td><%= o.getProductName() %></td>
                    <td class="num"><%= o.getQuantity() %></td>
                    <td class="num">&#8377; <%= String.format("%.2f", o.getPrice()) %></td>
                    <td class="num">&#8377; <%= String.format("%.2f", sub) %></td>
                </tr>
            <% } %>
            </tbody>
            <tfoot>
                <tr>
                    <th colspan="4" class="right">Total</th>
                    <th class="num">&#8377; <%= String.format("%.2f", grand) %></th>
                </tr>
            </tfoot>
        </table>
    <% } %>
    <div class="cta-row">
        <a class="btn-primary"   href="<%= ctx %>/customer/catalog">Continue Shopping</a>
        <a class="btn-secondary" href="<%= ctx %>/customer/orderHistory">View All Orders</a>
    </div>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
