package queueflow;

public class StaffMember extends User {

    private int    staffID;
    private String specialization;
    private String contactNumber;

    public StaffMember(int userID, String fullName, String email,
                       String password, String specialization,
                       String contactNumber) {
        super(userID, fullName, email, password, "Staff");
        this.staffID        = userID;
        this.specialization = specialization;
        this.contactNumber  = contactNumber;
    }

    @Override
    public String accessDashboard() {
        return "Staff Dashboard: Manage queue, update appointment status.";
    }

    // ── Staff-specific methods ────────────────────────────────────────────
    public String manageQueue(int tokenID) {
        return "Staff " + getFullName()
             + " is managing queue token #" + tokenID;
    }

    public String updateAppointmentStatus(int appointmentID, String status) {
        return "Appointment #" + appointmentID
             + " updated to: " + status + " by " + getFullName();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────
    public int    getStaffID()        { return staffID;        }
    public String getSpecialization() { return specialization; }
    public String getContactNumber()  { return contactNumber;  }

    public void setSpecialization(String s) { this.specialization = s; }
    public void setContactNumber(String c)  { this.contactNumber  = c; }

    @Override
    public String toString() {
        return "StaffMember{id=" + staffID
             + ", name=" + getFullName()
             + ", spec=" + specialization + "}";
    }
}
