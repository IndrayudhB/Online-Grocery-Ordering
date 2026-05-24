<%@ include file="/WEB-INF/jspf/header-customer.jspf" %>
<%@ page import="java.util.List, model.Wishlist" %>
<%
    List<Wishlist> items = (List<Wishlist>) request.getAttribute("items");
    Double total = (Double) request.getAttribute("cartTotal");
    if (total == null) total = 0.0;
%>

<style>
    .payment-option {
        display: block;
        padding: 1rem 1.1rem;
        border: 2px solid #e0e0e0;
        border-radius: 12px;
        margin-bottom: 0.75rem;
        cursor: pointer;
        transition: 0.2s ease;
        background: #fff;
    }
    .payment-option:hover {
        border-color: #81c784;
        background: rgba(76,175,80,0.05);
    }
    .payment-option input[type="radio"] {
        width: auto;
        margin: 0 0.55rem 0 0;
        accent-color: #43a047;
    }
    .payment-option input[type="radio"]:checked + .po-title {
        color: #1b5e20;
    }
    .payment-option .po-title {
        font-weight: 700;
        color: #2d3436;
        font-size: 1.05rem;
    }
    .payment-option .po-desc {
        display: block;
        font-size: 0.88rem;
        color: #636e72;
        margin: 0.25rem 0 0 1.6rem;
    }
    .payment-option:has(input[type="radio"]:checked) {
        border-color: #43a047;
        background: rgba(76,175,80,0.08);
        box-shadow: 0 4px 14px rgba(67,160,71,0.15);
    }
    .payment-fields {
        margin: 1rem 0 0;
        padding: 1.1rem;
        background: #fff8e7;
        border-radius: 12px;
        border-left: 4px solid #fdd835;
    }
    .payment-fields p { margin: 0; }
    .grid-2 {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
    }
    @media (max-width: 480px) {
        .grid-2 { grid-template-columns: 1fr; }
    }
    .pay-actions {
        display: flex;
        gap: 0.75rem;
        flex-wrap: wrap;
        margin-top: 1.25rem;
    }
    .demo-note {
        margin-top: 1rem;
        padding: 0.75rem 1rem;
        background: #e3f2fd;
        border-radius: 8px;
        color: #1565c0;
        font-size: 0.88rem;
    }
</style>

<section class="card">
    <h2>Checkout &amp; Payment</h2>
    <p class="muted">
        Review your order, then pick how you'd like to pay.
    </p>

    <h3 style="margin-top:1.5rem;">Order Summary</h3>
    <table class="data">
        <thead>
            <tr>
                <th>Product</th>
                <th class="num">Price</th>
                <th class="num">Qty</th>
                <th class="num">Subtotal</th>
            </tr>
        </thead>
        <tbody>
        <% for (Wishlist w : items) { %>
            <tr>
                <td><%= w.getProductName() %></td>
                <td class="num">&#8377; <%= String.format("%.2f", w.getPrice()) %></td>
                <td class="num"><%= w.getQuantity() %></td>
                <td class="num">&#8377; <%= String.format("%.2f", w.getLineTotal()) %></td>
            </tr>
        <% } %>
        </tbody>
        <tfoot>
            <tr>
                <th colspan="3" class="right">Total Payable</th>
                <th class="num">&#8377; <%= String.format("%.2f", total) %></th>
            </tr>
        </tfoot>
    </table>
</section>

<section class="card">
    <h2>Choose a Payment Method</h2>

    <form method="post" action="<%= ctx %>/customer/placeOrder"
          autocomplete="off" id="paymentForm">

        <label class="payment-option">
            <input type="radio" name="paymentMethod" value="cod" required checked>
            <span class="po-title">Cash on Delivery</span>
            <span class="po-desc">Pay the delivery agent in cash when your order arrives.</span>
        </label>

        <label class="payment-option">
            <input type="radio" name="paymentMethod" value="upi">
            <span class="po-title">UPI</span>
            <span class="po-desc">Use any UPI app &mdash; Google Pay, PhonePe, Paytm, BHIM&hellip;</span>
        </label>

        <label class="payment-option">
            <input type="radio" name="paymentMethod" value="card">
            <span class="po-title">Credit / Debit Card</span>
            <span class="po-desc">Visa, Mastercard, RuPay accepted.</span>
        </label>

        <!-- COD details (no extra inputs) -->
        <div id="fields-cod" class="payment-fields">
            <p>
                Please keep
                <strong>&#8377; <%= String.format("%.2f", total) %></strong>
                ready when the delivery agent arrives.
            </p>
        </div>

        <!-- UPI details -->
        <div id="fields-upi" class="payment-fields" style="display:none;">
            <label>UPI ID
                <input type="text" name="upiId"
                       placeholder="yourname@bank"
                       pattern="^[A-Za-z0-9._\-]+@[A-Za-z]+$"
                       title="Format: name@bank (letters, digits, dot, underscore, hyphen before @)">
            </label>
        </div>

        <!-- Card details -->
        <div id="fields-card" class="payment-fields" style="display:none;">
            <label>Card Number
                <input type="text" name="cardNumber"
                       placeholder="1234 5678 9012 3456"
                       inputmode="numeric"
                       maxlength="19">
            </label>
            <div class="grid-2">
                <label>Expiry (MM/YY)
                    <input type="text" name="cardExpiry"
                           placeholder="12/29"
                           maxlength="5"
                           pattern="^(0[1-9]|1[0-2])/\d{2}$">
                </label>
                <label>CVV
                    <input type="text" name="cardCvv"
                           placeholder="123"
                           inputmode="numeric"
                           maxlength="4"
                           pattern="^\d{3,4}$">
                </label>
            </div>
            <label>Cardholder Name
                <input type="text" name="cardName"
                       placeholder="As printed on the card">
            </label>
        </div>

        <div class="pay-actions">
            <button type="submit" class="btn-primary">
                Pay &#8377; <%= String.format("%.2f", total) %>
            </button>
            <a href="<%= ctx %>/customer/wishlist" class="btn-secondary">
                Back to Cart
            </a>
        </div>

        <div class="demo-note">
            &#128274; <strong>Demo mode</strong> &mdash; this is a simulated payment screen.
            No real charges are made and no card details are sent anywhere.
            You can use any plausible-looking values, e.g.:
            <code>4111 1111 1111 1111</code>, expiry <code>12/29</code>, CVV <code>123</code>,
            or UPI ID <code>demo@upi</code>.
        </div>
    </form>
</section>

<script>
(function () {
    var radios = document.querySelectorAll('input[name="paymentMethod"]');
    var fieldsByMethod = {
        cod:  document.getElementById('fields-cod'),
        upi:  document.getElementById('fields-upi'),
        card: document.getElementById('fields-card')
    };
    var inputUpi   = document.querySelector('input[name="upiId"]');
    var inputCard  = document.querySelector('input[name="cardNumber"]');
    var inputExp   = document.querySelector('input[name="cardExpiry"]');
    var inputCvv   = document.querySelector('input[name="cardCvv"]');

    function applyMethod(method) {
        for (var key in fieldsByMethod) {
            fieldsByMethod[key].style.display =
                (key === method) ? 'block' : 'none';
        }
        // Toggle required so the browser only validates fields the user
        // actually needs to fill in based on the chosen method.
        inputUpi.required  = (method === 'upi');
        inputCard.required = (method === 'card');
        inputExp.required  = (method === 'card');
        inputCvv.required  = (method === 'card');
    }

    radios.forEach(function (r) {
        r.addEventListener('change', function () { applyMethod(this.value); });
    });
    var checked = document.querySelector('input[name="paymentMethod"]:checked');
    if (checked) applyMethod(checked.value);

    // Auto-format card number into groups of 4 digits as the user types.
    if (inputCard) {
        inputCard.addEventListener('input', function (e) {
            var digits = e.target.value.replace(/\D/g, '').slice(0, 16);
            var groups = [];
            for (var i = 0; i < digits.length; i += 4) {
                groups.push(digits.substring(i, i + 4));
            }
            e.target.value = groups.join(' ');
        });
    }

    // Auto-insert "/" between MM and YY in the expiry field.
    if (inputExp) {
        inputExp.addEventListener('input', function (e) {
            var v = e.target.value.replace(/\D/g, '').slice(0, 4);
            if (v.length >= 3) v = v.substring(0, 2) + '/' + v.substring(2);
            e.target.value = v;
        });
    }
})();
</script>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
