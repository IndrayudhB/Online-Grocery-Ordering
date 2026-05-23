<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>
<%@ page import="java.util.List, model.Wishlist" %>
<%
    List<Wishlist> items = (List<Wishlist>) request.getAttribute("items");
    Double total = (Double) request.getAttribute("cartTotal");
    String error = (String) request.getAttribute("error");
%>

<section class="card">
    <h2>Cart / Wishlist</h2>
    <% if (error != null) { %>
        <div class="flash error"><%= error %></div>
    <% } %>

    <% if (items == null || items.isEmpty()) { %>
        <p class="muted">Your cart is empty.
            <a href="<%= ctx %>/customer/catalog">Browse the catalog</a>.</p>
    <% } else { %>
        <table class="data">
            <thead>
                <tr>
                    <th>Product</th>
                    <th class="num">Price</th>
                    <th>Qty</th>
                    <th class="num">Subtotal</th>
                    <th>Update</th>
                    <th>Remove</th>
                </tr>
            </thead>
            <tbody>
            <% for (Wishlist w : items) { %>
                <tr>
                    <td>
                        <%= w.getProductName() %>
                        <% if (w.getQuantity() > w.getStockAvailability()) { %>
                            <small class="warn">(only <%= w.getStockAvailability() %> in stock)</small>
                        <% } %>
                    </td>
                    <td class="num">&#8377; <%= String.format("%.2f", w.getPrice()) %></td>
                    <td>
                        <form method="post" action="<%= ctx %>/customer/wishlist" class="inline-form">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="wishlistId" value="<%= w.getWishlistId() %>">
                            <input type="number" name="quantity" min="1" max="<%= Math.max(w.getStockAvailability(), w.getQuantity()) %>" value="<%= w.getQuantity() %>" class="qty">
                            <button type="submit" class="btn-secondary small">Update</button>
                        </form>
                    </td>
                    <td class="num">&#8377; <%= String.format("%.2f", w.getLineTotal()) %></td>
                    <td></td>
                    <td>
                        <form method="post" action="<%= ctx %>/customer/wishlist" class="inline-form">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="wishlistId" value="<%= w.getWishlistId() %>">
                            <button type="submit" class="btn-danger small">Remove</button>
                        </form>
                    </td>
                </tr>
            <% } %>
            </tbody>
            <tfoot>
                <tr>
                    <th colspan="3" class="right">Total</th>
                    <th class="num">&#8377; <%= String.format("%.2f", total == null ? 0.0 : total) %></th>
                    <th colspan="2"></th>
                </tr>
            </tfoot>
        </table>

        <form method="post" action="<%= ctx %>/customer/placeOrder"
              onsubmit="return confirm('Place this order?');">
            <button type="submit" class="btn-primary">Place Order</button>
        </form>
    <% } %>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
