package model;

public class Login {
    private int loginId;
    private String email;
    private String password; // hashed in DB
    private String userType;  // 'customer' or 'admin'
    private String status;    // 'Active' or 'Inactive'

    public Login() {}

    public Login(int loginId, String email, String password,
                 String userType, String status) {
        this.loginId  = loginId;
        this.email    = email;
        this.password = password;
        this.userType = userType;
        this.status   = status;
    }

    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(userType);
    }
    public boolean isCustomer() {
        return "customer".equalsIgnoreCase(userType);
    }
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
}
