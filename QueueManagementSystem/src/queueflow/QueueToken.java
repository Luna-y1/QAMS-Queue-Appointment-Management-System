package queueflow;

public class QueueToken {

    private int    tokenID;
    private int    tokenNumber;
    private String patientName;
    private String serviceName;
    private String issueTime;
    private String status;
    private String tokenDate;

    /**
     * Full constructor.
     */
    public QueueToken(int tokenID, int tokenNumber, String patientName,
                      String serviceName, String issueTime,
                      String status, String tokenDate) {
        this.tokenID     = tokenID;
        this.tokenNumber = tokenNumber;
        this.patientName = patientName;
        this.serviceName = serviceName;
        this.issueTime   = issueTime;
        this.status      = status;
        this.tokenDate   = tokenDate;
    }

    /**
     * Constructor without ID (used when issuing new tokens).
     */
    public QueueToken(int tokenNumber, String patientName,
                      String serviceName, String issueTime,
                      String status, String tokenDate) {
        this(0, tokenNumber, patientName, serviceName,
             issueTime, status, tokenDate);
    }

    // ── Business logic ────────────────────────────────────────────────────
    /**
     * Marks this token as now being served.
     */
    public void serve() {
        this.status = "Serving";
    }

    /**
     * Marks this token as completed.
     */
    public void complete() {
        this.status = "Completed";
    }

    /**
     * Marks this token as skipped.
     */
    public void skip() {
        this.status = "Skipped";
    }

    /**
     * Returns true if this token is still in the queue.
     */
    public boolean isActive() {
        return status.equals("Waiting") || status.equals("Serving");
    }

    /**
     * Returns formatted token label e.g. Q001.
     */
    public String getTokenLabel() {
        return "Q" + String.format("%03d", tokenNumber);
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int    getTokenID()     { return tokenID;     }
    public int    getTokenNumber() { return tokenNumber; }
    public String getPatientName() { return patientName; }
    public String getServiceName() { return serviceName; }
    public String getIssueTime()   { return issueTime;   }
    public String getStatus()      { return status;      }
    public String getTokenDate()   { return tokenDate;   }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setTokenID(int id)         { this.tokenID     = id;     }
    public void setTokenNumber(int n)      { this.tokenNumber = n;      }
    public void setPatientName(String n)   { this.patientName = n;      }
    public void setServiceName(String s)   { this.serviceName = s;      }
    public void setIssueTime(String t)     { this.issueTime   = t;      }
    public void setStatus(String status)   { this.status      = status; }
    public void setTokenDate(String d)     { this.tokenDate   = d;      }

    @Override
    public String toString() {
        return "QueueToken{token="  + getTokenLabel()
             + ", patient="         + patientName
             + ", service="         + serviceName
             + ", status="          + status + "}";
    }
}
