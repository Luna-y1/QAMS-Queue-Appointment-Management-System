package queueflow;

public class Admin extends User {

    private int adminID;

    public Admin(int userID, String fullName, String email,
                 String password) {
        super(userID, fullName, email, password, "Admin");
        this.adminID = userID;
    }

    @Override
    public String accessDashboard() {
        return "Admin Dashboard: Full access to users, settings, reports.";
    }

    // ── Admin-specific methods ────────────────────────────────────────────
    public String manageUsers(String action, String targetUser) {
        return "Admin " + getFullName()
             + " performed [" + action + "] on user: " + targetUser;
    }

    public String configureSettings(String key, String value) {
        return "Setting updated: " + key + " = " + value;
    }

    public String generateReport(String reportType) {
        return "Report generated: " + reportType
             + " by Admin " + getFullName();
    }

    public int getAdminID() { return adminID; }

    @Override
    public String toString() {
        return "Admin{id=" + adminID
             + ", name=" + getFullName() + "}";
    }
}