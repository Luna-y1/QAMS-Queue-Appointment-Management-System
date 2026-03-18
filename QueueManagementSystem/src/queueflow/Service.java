package queueflow;

public class Service {

    private int    serviceID;
    private String serviceName;
    private String description;
    private int    estimatedDuration;

    /**
     * Full constructor.
     */
    public Service(int serviceID, String serviceName,
                   String description, int estimatedDuration) {
        this.serviceID         = serviceID;
        this.serviceName       = serviceName;
        this.description       = description;
        this.estimatedDuration = estimatedDuration;
    }

    /**
     * Constructor without ID (used when adding new services).
     */
    public Service(String serviceName, String description,
                   int estimatedDuration) {
        this(0, serviceName, description, estimatedDuration);
    }

    // ── Business logic ────────────────────────────────────────────────────
    /**
     * Returns estimated wait time for N people ahead.
     */
    public int estimateWaitTime(int peopleAhead) {
        return peopleAhead * estimatedDuration;
    }

    /**
     * Returns a formatted summary of this service.
     */
    public String getSummary() {
        return serviceName + " (~" + estimatedDuration + " min)";
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int    getServiceID()         { return serviceID;         }
    public String getServiceName()       { return serviceName;       }
    public String getDescription()       { return description;       }
    public int    getEstimatedDuration() { return estimatedDuration; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setServiceID(int id)            { this.serviceID         = id; }
    public void setServiceName(String n)        { this.serviceName       = n;  }
    public void setDescription(String d)        { this.description       = d;  }
    public void setEstimatedDuration(int dur)   { this.estimatedDuration = dur;}

    @Override
    public String toString() {
        return "Service{id="    + serviceID
             + ", name="        + serviceName
             + ", duration="    + estimatedDuration + "min}";
    }
}
