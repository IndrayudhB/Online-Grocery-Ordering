package model;

public class Product {
    private int productId;
    private String productName;
    private String description;
    private String companyName;
    private double price;
    private int stockAvailability;

    public Product() {}

    public Product(int productId, String productName, String description,
                   String companyName, double price, int stockAvailability) {
        this.productId         = productId;
        this.productName       = productName;
        this.description       = description;
        this.companyName       = companyName;
        this.price             = price;
        this.stockAvailability = stockAvailability;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockAvailability() { return stockAvailability; }
    public void setStockAvailability(int stockAvailability) {
        this.stockAvailability = stockAvailability;
    }
}
