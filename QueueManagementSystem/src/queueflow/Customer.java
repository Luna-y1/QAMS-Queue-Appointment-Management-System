package queueflow;

public class Customer extends User {

    private int customerID;

    public Customer(int userID, String fullName, String email,
                    String password) {
        super(userID, fullName, email, password, "Customer");
        this.customerID = userID;
    }

    @Override
    public String accessDashboard() {
        return "Customer Dashboard: Book appointments, view queue status.";
    }

    // ── Customer-specific methods ─────────────────────────────────────────
    public String bookAppointment(String service, String date, String time) {
        return "Appointment booked for " + getFullName()
             + " | Service: " + service
             + " | " + date + " at " + time;
    }

    public String viewQueueStatus(int position) {
        return "Queue position for " + getFullName() + ": #" + position;
    }

    public int getCustomerID() { return customerID; }

    @Override
    public String toString() {
        return "Customer{id=" + customerID
             + ", name=" + getFullName() + "}";
    }
}