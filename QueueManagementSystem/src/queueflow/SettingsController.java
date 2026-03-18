package queueflow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.*;

public class SettingsController {

    private final Connection conn =
        DatabaseConnection.getInstance().getConnection();
    private final UserDAO userDAO = new UserDAO();

    // ── Org fields ────────────────────────────────────────────────────────
    private TextField tfOrgName;
    private TextField tfOrgEmail;
    private TextField tfOrgPhone;
    private TextField tfOrgAddress;

    // ── Queue config fields ───────────────────────────────────────────────
    private TextField    tfAvgTime;
    private TextField    tfMaxQueue;
    private ToggleButton tbAutoAdvance;
    private ToggleButton tbPriority;

    // ── Notification toggles ──────────────────────────────────────────────
    private ToggleButton tbSMS;
    private ToggleButton tbEmail;
    private ToggleButton tbDisplay;

    // ── Status labels ─────────────────────────────────────────────────────
    private Label lblActiveUsers;
    private Label lblAdmins;

    /**
     * Builds and returns the full settings view.
     */
    public ScrollPane getView() {

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );

        VBox header = MainApp.pageHeader(
            "Settings",
            "Manage your system preferences and configurations."
        );

        // Two column layout
        HBox twoCol = new HBox(16);

        // Left column
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.getChildren().addAll(
            buildOrgCard(),
            buildQueueConfigCard(),
            buildNotificationCard()
        );

        // Right column
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(260);
        rightCol.setMaxWidth(260);
        rightCol.getChildren().addAll(
            buildStatusCard(),
            buildUserManagementCard(),
            buildHelpCard()
        );

        twoCol.getChildren().addAll(leftCol, rightCol);

        root.getChildren().addAll(header, twoCol);

        loadSettings();

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );
        return sp;
    }

    // ── Organisation info card ────────────────────────────────────────────
    private VBox buildOrgCard() {
        VBox card = MainApp.card();

        Label title = sectionTitle("🏠  Organization Information");

        tfOrgName    = MainApp.styledField("Organization name");
        tfOrgEmail   = MainApp.styledField("Email address");
        tfOrgPhone   = MainApp.styledField("Phone number");
        tfOrgAddress = MainApp.styledField("Address");

        for (TextField tf : new TextField[]{
                tfOrgName, tfOrgEmail, tfOrgPhone, tfOrgAddress}) {
            tf.setMaxWidth(Double.MAX_VALUE);
        }

        Button btnSave = MainApp.primaryBtn("Save Changes");
        btnSave.setOnAction(e -> saveOrgSettings());

        card.getChildren().addAll(
            title,
            fieldGroup("Organization Name", tfOrgName),
            fieldGroup("Email",             tfOrgEmail),
            fieldGroup("Phone Number",      tfOrgPhone),
            fieldGroup("Address",           tfOrgAddress),
            btnSave
        );
        return card;
    }

    // ── Queue configuration card ──────────────────────────────────────────
    private VBox buildQueueConfigCard() {
        VBox card = MainApp.card();

        Label title = sectionTitle("🕐  Queue Configuration");

        tfAvgTime  = MainApp.styledField("Minutes");
        tfMaxQueue = MainApp.styledField("Max size");
        tfAvgTime .setMaxWidth(Double.MAX_VALUE);
        tfMaxQueue.setMaxWidth(Double.MAX_VALUE);

        tbAutoAdvance = toggleBtn("Auto-advance Queue",
            "Automatically move to next customer");
        tbPriority    = toggleBtn("Priority Queue",
            "Enable priority customer handling");

        Button btnSave = MainApp.primaryBtn("Save Changes");
        btnSave.setOnAction(e -> saveQueueSettings());

        card.getChildren().addAll(
            title,
            fieldGroup("Average Service Time (minutes)", tfAvgTime),
            fieldGroup("Maximum Queue Size",             tfMaxQueue),
            tbAutoAdvance,
            tbPriority,
            btnSave
        );
        return card;
    }

    // ── Notification settings card ────────────────────────────────────────
    private VBox buildNotificationCard() {
        VBox card = MainApp.card();

        Label title = sectionTitle("🔔  Notification Settings");

        tbSMS     = toggleBtn("SMS Notifications",
            "Send SMS when turn is near");
        tbEmail   = toggleBtn("Email Reminders",
            "Send appointment reminder emails");
        tbDisplay = toggleBtn("Display Notifications",
            "Show queue updates on display screen");

        Button btnSave = MainApp.primaryBtn("Save Changes");
        btnSave.setOnAction(e -> saveNotifSettings());

        card.getChildren().addAll(
            title, tbSMS, tbEmail, tbDisplay, btnSave
        );
        return card;
    }

    // ── System status card ────────────────────────────────────────────────
    private VBox buildStatusCard() {
        VBox card = MainApp.card();

        Label title = sectionTitle("System Status");

        HBox queueRow  = statusRow("Queue Status",  "● Active",  MainApp.GREEN);
        HBox healthRow = statusRow("System Health", "● Normal",  MainApp.GREEN);
        HBox backupRow = statusRow("Last Backup",   "2 hrs ago", MainApp.TEXT_GREY);

        card.getChildren().addAll(
            title, queueRow, healthRow, backupRow
        );
        return card;
    }

    // ── User management card ──────────────────────────────────────────────
    private VBox buildUserManagementCard() {
        VBox card = MainApp.card();

        Label title = sectionTitle("👤  User Management");

        lblActiveUsers = new Label("—");
        lblActiveUsers.setStyle(
            "-fx-font-size:24px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        lblAdmins = new Label("—");
        lblAdmins.setStyle(
            "-fx-font-size:24px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        Label usersLbl = smallLabel("Active Users");
        Label admLbl   = smallLabel("Administrators");

        Button btnManage = MainApp.outlineBtn("Manage Users");
        btnManage.setMaxWidth(Double.MAX_VALUE);
        btnManage.setOnAction(e -> showManageUsersDialog());

        card.getChildren().addAll(
            title,
            usersLbl, lblActiveUsers,
            admLbl,   lblAdmins,
            btnManage
        );
        return card;
    }

    // ── Help card ─────────────────────────────────────────────────────────
    private VBox buildHelpCard() {
        VBox card = MainApp.card();

        Label title = new Label("Need Help?");
        title.setStyle(
            "-fx-font-size:14px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        Label body = new Label(
            "Contact our support team for assistance "
          + "with your queue management system."
        );
        body.setWrapText(true);
        body.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );

        Button btnContact = MainApp.outlineBtn("Contact Support");
        btnContact.setMaxWidth(Double.MAX_VALUE);
        btnContact.setOnAction(e ->
            MainApp.showInfo("Support",
                "Email: support@queueflow.com\n"
              + "Phone: +1 (555) 000-0000")
        );

        card.getChildren().addAll(title, body, btnContact);
        return card;
    }

    // ── Save org settings ─────────────────────────────────────────────────
    private void saveOrgSettings() {
        saveSetting("orgName",    tfOrgName.getText().trim());
        saveSetting("orgEmail",   tfOrgEmail.getText().trim());
        saveSetting("orgPhone",   tfOrgPhone.getText().trim());
        saveSetting("orgAddress", tfOrgAddress.getText().trim());
        MainApp.showInfo("Saved",
            "Organisation settings saved successfully.");
    }

    // ── Save queue settings ───────────────────────────────────────────────
    private void saveQueueSettings() {
        saveSetting("avgServiceTime",
            tfAvgTime.getText().trim());
        saveSetting("maxQueueSize",
            tfMaxQueue.getText().trim());
        saveSetting("autoAdvance",
            tbAutoAdvance.isSelected() ? "true" : "false");
        saveSetting("priorityQueue",
            tbPriority.isSelected() ? "true" : "false");
        MainApp.showInfo("Saved",
            "Queue configuration saved successfully.");
    }

    // ── Save notification settings ────────────────────────────────────────
    private void saveNotifSettings() {
        saveSetting("smsNotifications",
            tbSMS.isSelected()     ? "true" : "false");
        saveSetting("emailReminders",
            tbEmail.isSelected()   ? "true" : "false");
        saveSetting("displayNotifs",
            tbDisplay.isSelected() ? "true" : "false");
        MainApp.showInfo("Saved",
            "Notification settings saved successfully.");
    }

    // ── Load all settings from DB ─────────────────────────────────────────
    private void loadSettings() {
        try {
            String sql = "SELECT settingKey, settingValue "
                       + "FROM system_settings";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString("settingKey");
                String val = rs.getString("settingValue");
                switch (key) {
                    case "orgName"    -> tfOrgName   .setText(val);
                    case "orgEmail"   -> tfOrgEmail  .setText(val);
                    case "orgPhone"   -> tfOrgPhone  .setText(val);
                    case "orgAddress" -> tfOrgAddress.setText(val);
                    case "avgServiceTime" ->
                        tfAvgTime .setText(val);
                    case "maxQueueSize"   ->
                        tfMaxQueue.setText(val);
                    case "autoAdvance" ->
                        tbAutoAdvance.setSelected(
                            val.equals("true")
                        );
                    case "priorityQueue" ->
                        tbPriority.setSelected(
                            val.equals("true")
                        );
                    case "smsNotifications" ->
                        tbSMS.setSelected(val.equals("true"));
                    case "emailReminders"   ->
                        tbEmail.setSelected(val.equals("true"));
                    case "displayNotifs"    ->
                        tbDisplay.setSelected(val.equals("true"));
                }
                styleToggle(tbAutoAdvance);
                styleToggle(tbPriority);
                styleToggle(tbSMS);
                styleToggle(tbEmail);
                styleToggle(tbDisplay);
            }
        } catch (SQLException e) {
            System.err.println("[Settings] load error: "
                + e.getMessage());
        }

        // Load user counts
        try {
            lblActiveUsers.setText(
                String.valueOf(userDAO.countUsers())
            );
            lblAdmins.setText(
                String.valueOf(userDAO.countAdmins())
            );
        } catch (Exception e) {
            System.err.println("[Settings] user count error: "
                + e.getMessage());
        }
    }

    // ── Save a single setting to DB ───────────────────────────────────────
    private void saveSetting(String key, String value) {
        try {
            String sql = "INSERT INTO system_settings "
                       + "(settingKey, settingValue) VALUES (?, ?) "
                       + "ON DUPLICATE KEY UPDATE settingValue = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, key);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Settings] save error: "
                + e.getMessage());
        }
    }

    // ── Manage users dialog ───────────────────────────────────────────────
    private void showManageUsersDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Manage Users");
        dialog.setHeaderText("Registered Users");
        dialog.getDialogPane().getButtonTypes().add(
            ButtonType.CLOSE
        );
        dialog.getDialogPane().setPrefWidth(500);

        TableView<String[]> table = new TableView<>();
        table.setPrefHeight(300);
        MainApp.styleTable(table);

        TableColumn<String[], String> colID =
            new TableColumn<>("ID");
        colID.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                d.getValue()[0]
            )
        );
        colID.setMaxWidth(50);

        TableColumn<String[], String> colName =
            new TableColumn<>("Name");
        colName.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                d.getValue()[1]
            )
        );

        TableColumn<String[], String> colEmail =
            new TableColumn<>("Email");
        colEmail.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                d.getValue()[2]
            )
        );

        TableColumn<String[], String> colRole =
            new TableColumn<>("Role");
        colRole.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                d.getValue()[3]
            )
        );

        table.getColumns().addAll(colID, colName, colEmail, colRole);

        try {
            ResultSet rs = userDAO.getAllUsers();
            while (rs != null && rs.next()) {
                table.getItems().add(new String[]{
                    String.valueOf(rs.getInt("userID")),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            System.err.println("[Settings] load users error: "
                + e.getMessage());
        }

        dialog.getDialogPane().setContent(table);
        dialog.showAndWait();

        // Refresh counts after closing
        lblActiveUsers.setText(
            String.valueOf(userDAO.countUsers())
        );
        lblAdmins.setText(
            String.valueOf(userDAO.countAdmins())
        );
    }

    // ── UI helpers ────────────────────────────────────────────────────────

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-font-size:13px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        return l;
    }

    private Label smallLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        return l;
    }

    private VBox fieldGroup(String label, TextField field) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private HBox statusRow(String label, String value,
                            String colour) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + colour + ";"
          + "-fx-font-weight:bold;"
        );
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    private ToggleButton toggleBtn(String title, String subtitle) {
        ToggleButton tb = new ToggleButton(title + "\n" + subtitle);
        tb.setMaxWidth(Double.MAX_VALUE);
        tb.setWrapText(true);
        styleToggle(tb);
        tb.selectedProperty().addListener(
            (obs, old, val) -> styleToggle(tb)
        );
        return tb;
    }

    private void styleToggle(ToggleButton tb) {
        if (tb == null) return;
        if (tb.isSelected()) {
            tb.setStyle(
                "-fx-background-color:" + MainApp.BLUE + "22;"
              + "-fx-text-fill:" + MainApp.BLUE + ";"
              + "-fx-font-size:12px;"
              + "-fx-alignment:CENTER_LEFT;"
              + "-fx-padding:8 12;"
              + "-fx-background-radius:8;"
              + "-fx-border-color:" + MainApp.BLUE + ";"
              + "-fx-border-radius:8;"
              + "-fx-border-width:1;"
              + "-fx-cursor:hand;"
            );
        } else {
            tb.setStyle(
                "-fx-background-color:transparent;"
              + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
              + "-fx-font-size:12px;"
              + "-fx-alignment:CENTER_LEFT;"
              + "-fx-padding:8 12;"
              + "-fx-background-radius:8;"
              + "-fx-border-color:" + MainApp.BORDER + ";"
              + "-fx-border-radius:8;"
              + "-fx-border-width:1;"
              + "-fx-cursor:hand;"
            );
        }
    }
}