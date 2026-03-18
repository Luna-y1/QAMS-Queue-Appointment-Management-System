package queueflow;

public class Appointment {

    private int    appointmentID;
    private String patientName;
    private String serviceName;
    private String staffName;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    /**
     * Full constructor.
     */
    public Appointment(int appointmentID, String patientName,
                       String serviceName, String staffName,
                       String appointmentDate, String appointmentTime,
                       String status) {
        this.appointmentID   = appointmentID;
        this.patientName     = patientName;
        this.serviceName     = serviceName;
        this.staffName       = staffName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status          = status;
    }

    /**
     * Constructor without ID (used when creating new appointments).
     */
    public Appointment(String patientName, String serviceName,
                       String staffName, String appointmentDate,
                       String appointmentTime, String status) {
        this(0, patientName, serviceName, staffName,
             appointmentDate, appointmentTime, status);
    }

    // ── Business logic ────────────────────────────────────────────────────
    /**
     * Reschedules the appointment to a new date and time.
     */
    public void reschedule(String newDate, String newTime) {
        this.appointmentDate = newDate;
        this.appointmentTime = newTime;
        this.status          = "Scheduled";
    }

    /**
     * Cancels the appointment.
     */
    public void cancel() {
        this.status = "Cancelled";
    }

    /**
     * Confirms the appointment.
     */
    public void confirm() {
        this.status = "Confirmed";
    }

    /**
     * Returns true if appointment is still active.
     */
    public boolean isActive() {
        return !status.equals("Cancelled")
            && !status.equals("Completed");
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int    getAppointmentID()   { return appointmentID;   }
    public String getPatientName()     { return patientName;     }
    public String getServiceName()     { return serviceName;     }
    public String getStaffName()       { return staffName;       }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus()          { return status;          }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setAppointmentID(int id)       { this.appointmentID   = id;   }
    public void setPatientName(String n)       { this.patientName     = n;    }
    public void setServiceName(String s)       { this.serviceName     = s;    }
    public void setStaffName(String s)         { this.staffName       = s;    }
    public void setAppointmentDate(String d)   { this.appointmentDate = d;    }
    public void setAppointmentTime(String t)   { this.appointmentTime = t;    }
    public void setStatus(String status)       { this.status          = status; }

    @Override
    public String toString() {
        return "Appointment{id="   + appointmentID
             + ", patient="        + patientName
             + ", service="        + serviceName
             + ", date="           + appointmentDate
             + ", time="           + appointmentTime
             + ", status="         + status + "}";
    }
}