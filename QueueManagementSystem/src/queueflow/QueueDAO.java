package queueflow;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * QueueDAO - Data Access Object for queue token operations.
 */
public class QueueDAO {

    private final Connection conn =
        DatabaseConnection.getInstance().getConnection();

    /**
     * Adds a new patient to the queue.
     * Returns true if successful.
     */
    public boolean addToQueue(String patientName, String serviceName) {
        try {
            int nextToken = getNextTokenNumber();
            String time   = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String date   = LocalDate.now().toString();

            String sql = "INSERT INTO queue_tokens "
                       + "(tokenNumber, patientName, serviceName, "
                       + " issueTime, status, tokenDate) "
                       + "VALUES (?, ?, ?, ?, 'Waiting', ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nextToken);
            ps.setString(2, patientName);
            ps.setString(3, serviceName);
            ps.setString(4, time);
            ps.setString(5, date);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[QueueDAO] addToQueue error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Returns today's queue as a list of QueueToken objects.
     */
    public List<QueueToken> getTodaysQueue() {
        List<QueueToken> list = new ArrayList<>();
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT * FROM queue_tokens "
                         + "WHERE tokenDate = ? "
                         + "AND status IN ('Waiting','Serving') "
                         + "ORDER BY tokenNumber ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new QueueToken(
                    rs.getInt("tokenID"),
                    rs.getInt("tokenNumber"),
                    rs.getString("patientName"),
                    rs.getString("serviceName"),
                    rs.getString("issueTime"),
                    rs.getString("status"),
                    rs.getString("tokenDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[QueueDAO] getTodaysQueue error: "
                + e.getMessage());
        }
        return list;
    }

    /**
     * Updates the status of a queue token by ID.
     */
    public boolean updateStatus(int tokenID, String status) {
        try {
            String sql = "UPDATE queue_tokens SET status = ? "
                       + "WHERE tokenID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, tokenID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[QueueDAO] updateStatus error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a token from the queue by ID.
     */
    public boolean removeToken(int tokenID) {
        try {
            String sql = "DELETE FROM queue_tokens WHERE tokenID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tokenID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[QueueDAO] removeToken error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Counts patients currently waiting today.
     */
    public int countWaiting() {
        return countByStatus("Waiting");
    }

    /**
     * Counts patients currently being served today.
     */
    public int countServing() {
        return countByStatus("Serving");
    }

    /**
     * Counts patients completed today.
     */
    public int countCompletedToday() {
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT COUNT(*) FROM queue_tokens "
                         + "WHERE tokenDate = ? AND status = 'Completed'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[QueueDAO] countCompletedToday error: "
                + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns all completed tokens today.
     */
    public List<QueueToken> getCompletedToday() {
        List<QueueToken> list = new ArrayList<>();
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT * FROM queue_tokens "
                         + "WHERE tokenDate = ? AND status = 'Completed' "
                         + "ORDER BY tokenNumber ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new QueueToken(
                    rs.getInt("tokenID"),
                    rs.getInt("tokenNumber"),
                    rs.getString("patientName"),
                    rs.getString("serviceName"),
                    rs.getString("issueTime"),
                    rs.getString("status"),
                    rs.getString("tokenDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[QueueDAO] getCompletedToday error: "
                + e.getMessage());
        }
        return list;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Counts tokens by status for today.
     */
    private int countByStatus(String status) {
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT COUNT(*) FROM queue_tokens "
                         + "WHERE tokenDate = ? AND status = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[QueueDAO] countByStatus error: "
                + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the next available token number for today.
     */
    private int getNextTokenNumber() {
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT MAX(tokenNumber) FROM queue_tokens "
                         + "WHERE tokenDate = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) + 1;
        } catch (SQLException e) {
            System.err.println("[QueueDAO] getNextTokenNumber error: "
                + e.getMessage());
        }
        return 1;
    }
}