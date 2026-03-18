package queueflow;

import java.sql.*;

public class UserDAO {

    private final Connection conn =
        DatabaseConnection.getInstance().getConnection();

    /**
     * Validates login credentials.
     * Returns the matched user row or null if not found.
     */
    public ResultSet loginUser(String email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email = ? "
                       + "AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.err.println("[UserDAO] loginUser error: "
                + e.getMessage());
            return null;
        }
    }

    /**
     * Registers a new user.
     * Returns true if successful.
     */
    public boolean registerUser(String fullName, String email,
                                String password, String role) {
        try {
            String sql = "INSERT INTO users "
                       + "(fullName, email, password, role) "
                       + "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerUser error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Returns all users as a ResultSet.
     */
    public ResultSet getAllUsers() {
        try {
            String sql = "SELECT * FROM users ORDER BY createdAt DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: "
                + e.getMessage());
            return null;
        }
    }

    /**
     * Updates a user's role.
     */
    public boolean updateUserRole(int userID, String role) {
        try {
            String sql = "UPDATE users SET role = ? WHERE userID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, role);
            ps.setInt(2, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateUserRole error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a user by ID.
     */
    public boolean deleteUser(int userID) {
        try {
            String sql = "DELETE FROM users WHERE userID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteUser error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Counts total registered users.
     */
    public int countUsers() {
        try {
            String sql = "SELECT COUNT(*) FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UserDAO] countUsers error: "
                + e.getMessage());
        }
        return 0;
    }

    /**
     * Counts total admin users.
     */
    public int countAdmins() {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE role = 'Admin'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UserDAO] countAdmins error: "
                + e.getMessage());
        }
        return 0;
    }
}
