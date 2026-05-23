package model;

public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String contactNumber;
    private String address;
    private int loginId;

    public Customer() {}

    public Customer(int customerId, String name, String email,
                    String contactNumber, String address, int loginId) {
        this.customerId    = customerId;
        this.name          = name;
        this.email         = email;
        this.contactNumber = contactNumber;
        this.address       = address;
        this.loginId       = loginId;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }
}
