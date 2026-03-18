package queueflow;

public abstract class User {

    // Encapsulated fields
    private int    userID;
    private String fullName;
    private String email;
    private String password;
    private String role;

    /**
     * Constructor for User.
     */
    public User(int userID, String fullName, String email,
                String password, String role) {
        this.userID   = userID;
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── Abstract methods (Polymorphism) ──────────────────────────────────
    /**
     * Each user type accesses a different dashboard.
     * Overridden in Customer, Staff, Admin.
     */
    public abstract String accessDashboard();

    // ── Getters ──────────────────────────────────────────────────────────
    public int    getUserID()   { return userID;   }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email;    }
    public String getPassword() { return password; }
    public String getRole()     { return role;     }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email)       { this.email    = email;    }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role)         { this.role     = role;     }

    @Override
    public String toString() {
        return "User{id=" + userID + ", name=" + fullName
             + ", role=" + role + "}";
    }
}
