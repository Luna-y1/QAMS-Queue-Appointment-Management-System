package queueflow;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO - Data Access Object for appointment operations.
 */
public class AppointmentDAO {

    private final Connection conn =
        DatabaseConnection.getInstance().getConnection();

    /**
     * Books a new appointment.
     * Returns true if successful.
     */
    public boolean bookAppointment(String patientName, String serviceName,
                                   String staffName, String date,
                                   String time, String status) {
        try {
            String sql = "INSERT INTO appointments "
                       + "(patientName, serviceName, staffName, "
                       + " appointmentDate, appointmentTime, status) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, patientName);
            ps.setString(2, serviceName);
            ps.setString(3, staffName);
            ps.setString(4, date);
            ps.setString(5, time);
            ps.setString(6, status);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] bookAppointment error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Returns today's appointments as a list.
     */
    public List<Appointment> getTodaysAppointments() {
        List<Appointment> list = new ArrayList<>();
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT * FROM appointments "
                         + "WHERE appointmentDate = ? "
                         + "ORDER BY appointmentTime ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] getTodaysAppointments "
                + "error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns upcoming appointments (after today) as a list.
     */
    public List<Appointment> getUpcomingAppointments() {
        List<Appointment> list = new ArrayList<>();
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT * FROM appointments "
                         + "WHERE appointmentDate > ? "
                         + "AND status != 'Cancelled' "
                         + "ORDER BY appointmentDate ASC, "
                         + "appointmentTime ASC LIMIT 10";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] getUpcomingAppointments "
                + "error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns all appointments as a list.
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM appointments "
                       + "ORDER BY appointmentDate DESC, "
                       + "appointmentTime DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] getAllAppointments "
                + "error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Updates the status of an appointment.
     */
    public boolean updateStatus(int appointmentID, String status) {
        try {
            String sql = "UPDATE appointments SET status = ? "
                       + "WHERE appointmentID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, appointmentID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] updateStatus error: "
                + e.getMessage());
            return false;
        }
    }

    /**
     * Reschedules an appointment to a new date and time.
     */
    public boolean rescheduleAppointment(int appointmentID,
                                         String newDate, String newTime) {
        try {
            String sql = "UPDATE appointments "
                       + "SET appointmentDate = ?, appointmentTime = ?, "
                       + "status = 'Scheduled' "
                       + "WHERE appointmentID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newDate);
            ps.setString(2, newTime);
            ps.setInt(3, appointmentID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] rescheduleAppointment "
                + "error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes an appointment by ID.
     */
    public boolean deleteAppointment(int appointmentID) {
        try {
            String sql = "DELETE FROM appointments "
                       + "WHERE appointmentID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, appointmentID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] deleteAppointment "
                + "error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Counts today's appointments.
     */
    public int countToday() {
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT COUNT(*) FROM appointments "
                         + "WHERE appointmentDate = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] countToday error: "
                + e.getMessage());
        }
        return 0;
    }

    /**
     * Counts upcoming appointments after today.
     */
    public int countUpcoming() {
        try {
            String today = LocalDate.now().toString();
            String sql   = "SELECT COUNT(*) FROM appointments "
                         + "WHERE appointmentDate > ? "
                         + "AND status != 'Cancelled'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] countUpcoming error: "
                + e.getMessage());
        }
        return 0;
    }

    /**
     * Counts all appointments ever booked.
     */
    public int countTotal() {
        try {
            String sql = "SELECT COUNT(*) FROM appointments";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] countTotal error: "
                + e.getMessage());
        }
        return 0;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Maps a ResultSet row to an Appointment object.
     */
    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("appointmentID"),
            rs.getString("patientName"),
            rs.getString("serviceName"),
            rs.getString("staffName"),
            rs.getString("appointmentDate"),
            rs.getString("appointmentTime"),
            rs.getString("status")
        );
    }
}