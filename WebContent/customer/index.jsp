<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>

<section class="hero">
    <h1>Welcome back, <%= displayName %>!</h1>
    <p>Browse fresh groceries, build your cart, and place an order in seconds.</p>
    <div class="cta-row">
        <a class="btn-primary" href="<%= ctx %>/customer/catalog">Browse Catalog</a>
        <a class="btn-secondary" href="<%= ctx %>/customer/wishlist">View Cart</a>
        <a class="btn-secondary" href="<%= ctx %>/customer/orderHistory">My Orders</a>
    </div>
</section>

<section class="card">
    <h2>Account</h2>
    <p>Need to take a break? You can deactivate your account; you can
       reactivate it later by signing in with the same credentials.</p>
    <form method="post" action="<%= ctx %>/accountStatus"
          onsubmit="return confirm('Deactivate your account?');">
        <input type="hidden" name="action" value="deactivate">
        <button type="submit" class="btn-danger">Deactivate Account</button>
    </form>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
