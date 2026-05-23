package model;

/**
 * Represents a row in {@code grocery_order}.
 * (DB table is named grocery_order to avoid the SQLite reserved keyword.)
 */
public class Order {
    private int orderId;
    private int customerId;
    private int productId;
    private int quantity;
    private String orderDate;

    /* View-only fields populated when joining with product */
    private String productName;
    private double price;

    public Order() {}

    public Order(int orderId, int customerId, int productId,
                 int quantity, String orderDate) {
        this.orderId    = orderId;
        this.customerId = customerId;
        this.productId  = productId;
        this.quantity   = quantity;
        this.orderDate  = orderDate;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getLineTotal() { return price * quantity; }
}
