package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.DuplicateEmailException;
import exception.InvalidEmailException;
import model.Customer;
import model.Login;
import util.DatabaseConnection;
import util.EmailValidator;
import util.PasswordUtil;

/** JDBC access to the {@code customer} table. */
public class CustomerDAO {

    private final LoginDAO loginDAO = new LoginDAO();

    /**
     * Atomically registers a customer (login + customer rows).
     * Validates email format and uniqueness before insert.
     */
    public int registerCustomer(Customer customer, String plainPassword)
            throws InvalidEmailException, DuplicateEmailException, SQLException {

        if (!EmailValidator.isValid(customer.getEmail())) {
            throw new InvalidEmailException(
                    "Email format is invalid: " + customer.getEmail());
        }
        if (loginDAO.existsByEmail(customer.getEmail())) {
            throw new DuplicateEmailException(
                    "An account already exists for email "
                            + customer.getEmail());
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Login login = new Login(0, customer.getEmail(),
                        PasswordUtil.hash(plainPassword), "customer", "Active");
                int loginId = loginDAO.insert(conn, login);

                String sql = "INSERT INTO customer "
                           + "(name, email, contact_number, address, login_id) "
                           + "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, customer.getName());
                    ps.setString(2, customer.getEmail());
                    ps.setString(3, customer.getContactNumber());
                    ps.setString(4, customer.getAddress());
                    ps.setInt(5, loginId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int customerId = keys.getInt(1);
                            conn.commit();
                            return customerId;
                        }
                    }
                }
                conn.rollback();
                throw new SQLException(
                        "Failed to obtain generated customer_id.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public Customer findByLoginId(int loginId) throws SQLException {
        String sql = "SELECT customer_id, name, email, contact_number, "
                   + "address, login_id FROM customer WHERE login_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Customer findById(int customerId) throws SQLException {
        String sql = "SELECT customer_id, name, email, contact_number, "
                   + "address, login_id FROM customer WHERE customer_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates the customer profile. If the email changes, the linked
     * login row's email is updated atomically as well.
     */
    public void update(Customer customer) throws SQLException,
            InvalidEmailException, DuplicateEmailException {

        if (!EmailValidator.isValid(customer.getEmail())) {
            throw new InvalidEmailException(
                    "Email format is invalid: " + customer.getEmail());
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Detect duplicate email collisions
                String dupSql = "SELECT login_id FROM login "
                              + "WHERE email = ? AND login_id <> ?";
                try (PreparedStatement ps = conn.prepareStatement(dupSql)) {
                    ps.setString(1, customer.getEmail());
                    ps.setInt(2, customer.getLoginId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new DuplicateEmailException(
                                    "Email already in use: "
                                            + customer.getEmail());
                        }
                    }
                }

                String sql = "UPDATE customer SET name = ?, email = ?, "
                           + "contact_number = ?, address = ? "
                           + "WHERE customer_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, customer.getName());
                    ps.setString(2, customer.getEmail());
                    ps.setString(3, customer.getContactNumber());
                    ps.setString(4, customer.getAddress());
                    ps.setInt(5, customer.getCustomerId());
                    ps.executeUpdate();
                }

                loginDAO.updateEmail(conn, customer.getLoginId(),
                        customer.getEmail());

                conn.commit();
            } catch (SQLException | DuplicateEmailException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("contact_number"),
            rs.getString("address"),
            rs.getInt("login_id")
        );
    }
}
