package model;

public class Wishlist {
    private int wishlistId;
    private int userId;     // login_id
    private int productId;
    private int quantity;

    /* View-only fields populated when joining with product */
    private String productName;
    private double price;
    private int stockAvailability;

    public Wishlist() {}

    public Wishlist(int wishlistId, int userId, int productId, int quantity) {
        this.wishlistId = wishlistId;
        this.userId     = userId;
        this.productId  = productId;
        this.quantity   = quantity;
    }

    public int getWishlistId() { return wishlistId; }
    public void setWishlistId(int wishlistId) { this.wishlistId = wishlistId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockAvailability() { return stockAvailability; }
    public void setStockAvailability(int stockAvailability) {
        this.stockAvailability = stockAvailability;
    }

    public double getLineTotal() { return price * quantity; }
}
