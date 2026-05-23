package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Login;
import util.DatabaseConnection;

/** JDBC access to the {@code login} table. */
public class LoginDAO {

    public Login findByEmail(String email) throws SQLException {
        String sql = "SELECT login_id, email, password, userType, status "
                   + "FROM login WHERE email = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Login findById(int loginId) throws SQLException {
        String sql = "SELECT login_id, email, password, userType, status "
                   + "FROM login WHERE login_id = ?";
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

    public boolean existsByEmail(String email) throws SQLException {
        return findByEmail(email) != null;
    }

    /** Inserts a new login row and returns the generated login_id. */
    public int insert(Connection conn, Login login) throws SQLException {
        String sql = "INSERT INTO login (email, password, userType, status) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps =
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, login.getEmail());
            ps.setString(2, login.getPassword());
            ps.setString(3, login.getUserType());
            ps.setString(4, login.getStatus() == null ? "Active"
                                                       : login.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to obtain generated login_id.");
    }

    public int insert(Login login) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return insert(c, login);
        }
    }

    public void updateStatus(int loginId, String status) throws SQLException {
        String sql = "UPDATE login SET status = ? WHERE login_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, loginId);
            ps.executeUpdate();
        }
    }

    public void updatePassword(int loginId, String hashed) throws SQLException {
        String sql = "UPDATE login SET password = ? WHERE login_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setInt(2, loginId);
            ps.executeUpdate();
        }
    }

    public void updateEmail(Connection conn, int loginId, String email)
            throws SQLException {
        String sql = "UPDATE login SET email = ? WHERE login_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, loginId);
            ps.executeUpdate();
        }
    }

    private Login mapRow(ResultSet rs) throws SQLException {
        return new Login(
            rs.getInt("login_id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("userType"),
            rs.getString("status")
        );
    }
}
