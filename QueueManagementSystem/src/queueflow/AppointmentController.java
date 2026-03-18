package queueflow;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * AppointmentController - Appointments screen matching Figures 9 & 10.
 * Role-based access:
 *   Customer — book + view own appointments only
 *   Staff    — book + view all + confirm/complete/cancel
 *   Admin    — full access
 *
 * OOP: Encapsulation, Single Responsibility Principle
 *
 * @author Luna Twayana (2533423)
 * @version 1.1 - CIS096-1 Week 9
 */
public class AppointmentController {

    private final AppointmentDAO aDAO = new AppointmentDAO();
    private final String         role;
    private final String         loggedInName;

    private Label lblToday;
    private Label lblUpcoming;
    private Label lblTotal;

    private TableView<Appointment> todayTable;
    private TableView<Appointment> upcomingTable;

    /**
     * Constructor accepts role and logged-in name for access control.
     */
    public AppointmentController(String role, String loggedInName) {
        this.role         = role;
        this.loggedInName = loggedInName;
    }

    /**
     * Builds and returns the full appointments view.
     */
    public ScrollPane getView() {

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );

        // ── Page header + New button ──────────────────────────────────────
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = MainApp.pageHeader(
            "Appointments",
            isCustomer()
                ? "View and manage your appointments."
                : "Schedule and manage customer appointments."
        );
        HBox.setHgrow(header, Priority.ALWAYS);

        // All roles can book appointments
        Button btnNew = MainApp.primaryBtn("+ New Appointment");
        btnNew.setOnAction(e -> showBookDialog());
        topRow.getChildren().addAll(header, btnNew);

        // ── Role badge ────────────────────────────────────────────────────
        if (isCustomer()) {
            Label lblRole = new Label("👤  Customer View");
            lblRole.setStyle(
                "-fx-background-color:" + MainApp.BLUE + "22;"
              + "-fx-text-fill:" + MainApp.BLUE + ";"
              + "-fx-background-radius:8;"
              + "-fx-padding:6 14;"
              + "-fx-font-size:12px;"
              + "-fx-font-weight:bold;"
            );
            topRow.getChildren().add(lblRole);
        }

        // ── Stat mini cards ───────────────────────────────────────────────
        lblToday    = new Label("—");
        lblUpcoming = new Label("—");
        lblTotal    = new Label("—");

        HBox statsRow = new HBox(16,
            miniStatCard("📅", "Today",    lblToday,    MainApp.BLUE),
            miniStatCard("🕐", "Upcoming", lblUpcoming, MainApp.AMBER),
            miniStatCard("👥", "Total",    lblTotal,    MainApp.GREEN)
        );
        for (int i = 0; i < 3; i++)
            HBox.setHgrow(statsRow.getChildren().get(i), Priority.ALWAYS);

        // ── Search bar ────────────────────────────────────────────────────
        TextField tfSearch = MainApp.styledField(
            "🔍  Search appointments..."
        );
        tfSearch.setMaxWidth(Double.MAX_VALUE);
        tfSearch.textProperty().addListener(
            (obs, old, val) -> filterTables(val)
        );

        // ── Tables ────────────────────────────────────────────────────────
        VBox todayCard    = buildTableCard(
            "Today's Appointments", true
        );
        VBox upcomingCard = buildTableCard(
            "Upcoming Appointments", false
        );

        // ── Refresh button ────────────────────────────────────────────────
        Button btnRefresh = MainApp.outlineBtn("🔄  Refresh");
        btnRefresh.setOnAction(e -> refresh());
        HBox btnRow = new HBox(btnRefresh);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(
            topRow, statsRow, tfSearch,
            todayCard, upcomingCard, btnRow
        );

        refresh();

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );
        return sp;
    }

    // ── Mini stat card ────────────────────────────────────────────────────
    private HBox miniStatCard(String icon, String label,
                               Label valueLabel, String colour) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color:" + MainApp.WHITE + ";"
          + "-fx-background-radius:12;"
          + "-fx-border-color:" + MainApp.BORDER + ";"
          + "-fx-border-radius:12;"
          + "-fx-border-width:1;"
        );

        Label iconLbl = new Label(icon);
        iconLbl.setStyle(
            "-fx-background-color:" + colour + "22;"
          + "-fx-background-radius:8;"
          + "-fx-padding:8 10;"
          + "-fx-font-size:16px;"
        );

        VBox info = new VBox(2);
        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        valueLabel.setStyle(
            "-fx-font-size:26px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );
        info.getChildren().addAll(lbl, valueLabel);
        card.getChildren().addAll(iconLbl, info);
        return card;
    }

    // ── Table card builder ────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildTableCard(String title, boolean isToday) {
        VBox card = MainApp.card();

        Label t = new Label(title);
        t.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        TableView<Appointment> table = new TableView<>();
        MainApp.styleTable(table);
        table.setPrefHeight(220);
        table.setPlaceholder(new Label("No appointments found."));

        if (isToday) todayTable    = table;
        else         upcomingTable = table;

        // Patient column
        TableColumn<Appointment, String> colPat =
            new TableColumn<>("Patient");
        colPat.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getPatientName()
            )
        );

        // Service column
        TableColumn<Appointment, String> colSvc =
            new TableColumn<>("Service");
        colSvc.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getServiceName()
            )
        );

        // Date column
        TableColumn<Appointment, String> colDate =
            new TableColumn<>("Date");
        colDate.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getAppointmentDate()
            )
        );
        colDate.setMaxWidth(110);

        // Time column
        TableColumn<Appointment, String> colTime =
            new TableColumn<>("Time");
        colTime.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getAppointmentTime()
            )
        );
        colTime.setMaxWidth(90);

        // Status column with badge
        TableColumn<Appointment, String> colStatus =
            new TableColumn<>("Status");
        colStatus.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getStatus()
            )
        );
        colStatus.setCellFactory(c -> statusBadgeCell());
        colStatus.setMaxWidth(110);

        // Actions column — only Staff and Admin
        if (isStaffOrAdmin()) {
            TableColumn<Appointment, Void> colActions =
                new TableColumn<>("Actions");
            colActions.setCellFactory(
                c -> appointmentActionCell()
            );
            colActions.setMinWidth(200);
            table.getColumns().addAll(
                colPat, colSvc, colDate,
                colTime, colStatus, colActions
            );
        } else {
            // Customers see no action buttons
            table.getColumns().addAll(
                colPat, colSvc, colDate,
                colTime, colStatus
            );
        }

        card.getChildren().addAll(t, table);
        return card;
    }

    // ── Appointment action cell (Staff + Admin only) ──────────────────────
    private TableCell<Appointment, Void> appointmentActionCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Appointment appt = getTableView()
                    .getItems().get(getIndex());

                HBox box = new HBox(8);
                box.setAlignment(Pos.CENTER_LEFT);

                // Confirm button
                if (appt.getStatus().equals("Scheduled")) {
                    Button btnConfirm = new Button("✓ Confirm");
                    btnConfirm.setStyle(
                        "-fx-background-color:"
                      + MainApp.GREEN + "22;"
                      + "-fx-text-fill:" + MainApp.GREEN + ";"
                      + "-fx-font-size:11px;"
                      + "-fx-font-weight:bold;"
                      + "-fx-padding:5 10;"
                      + "-fx-background-radius:6;"
                      + "-fx-cursor:hand;"
                      + "-fx-border-color:" + MainApp.GREEN + ";"
                      + "-fx-border-radius:6;"
                      + "-fx-border-width:1;"
                    );
                    btnConfirm.setOnAction(e -> {
                        aDAO.updateStatus(
                            appt.getAppointmentID(), "Confirmed"
                        );
                        refresh();
                    });
                    box.getChildren().add(btnConfirm);
                }

                // Complete button
                if (appt.getStatus().equals("Confirmed")) {
                    Button btnComplete = new Button("✓ Complete");
                    btnComplete.setStyle(
                        "-fx-background-color:"
                      + MainApp.BLUE + "22;"
                      + "-fx-text-fill:" + MainApp.BLUE + ";"
                      + "-fx-font-size:11px;"
                      + "-fx-font-weight:bold;"
                      + "-fx-padding:5 10;"
                      + "-fx-background-radius:6;"
                      + "-fx-cursor:hand;"
                      + "-fx-border-color:" + MainApp.BLUE + ";"
                      + "-fx-border-radius:6;"
                      + "-fx-border-width:1;"
                    );
                    btnComplete.setOnAction(e -> {
                        aDAO.updateStatus(
                            appt.getAppointmentID(), "Completed"
                        );
                        refresh();
                    });
                    box.getChildren().add(btnComplete);
                }

                // Cancel button
                if (!appt.getStatus().equals("Cancelled")
                 && !appt.getStatus().equals("Completed")) {
                    Button btnCancel = new Button("✕ Cancel");
                    btnCancel.setStyle(
                        "-fx-background-color:transparent;"
                      + "-fx-text-fill:" + MainApp.RED + ";"
                      + "-fx-font-size:11px;"
                      + "-fx-padding:5 10;"
                      + "-fx-background-radius:6;"
                      + "-fx-cursor:hand;"
                      + "-fx-border-color:" + MainApp.RED + ";"
                      + "-fx-border-radius:6;"
                      + "-fx-border-width:1;"
                    );
                    btnCancel.setOnAction(e -> {
                        boolean ok = MainApp.showConfirm(
                            "Cancel Appointment",
                            "Cancel appointment for "
                          + appt.getPatientName() + "?"
                        );
                        if (ok) {
                            aDAO.updateStatus(
                                appt.getAppointmentID(),
                                "Cancelled"
                            );
                            refresh();
                        }
                    });
                    box.getChildren().add(btnCancel);
                }

                setGraphic(box);
            }
        };
    }

    // ── Status badge cell ─────────────────────────────────────────────────
    private TableCell<Appointment, String> statusBadgeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String colour = switch (item) {
                    case "Confirmed"  -> MainApp.GREEN;
                    case "Completed"  -> MainApp.BLUE;
                    case "Cancelled"  -> MainApp.RED;
                    default           -> MainApp.AMBER;
                };
                setGraphic(MainApp.badge(item, colour));
                setText(null);
            }
        };
    }

    // ── Book appointment dialog ───────────────────────────────────────────
    private void showBookDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Appointment");
        dialog.setHeaderText("Book a new appointment");

        ButtonType bookBtn = new ButtonType(
            "Book Appointment", ButtonBar.ButtonData.OK_DONE
        );
        dialog.getDialogPane().getButtonTypes().addAll(
            bookBtn, ButtonType.CANCEL
        );

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(380);

        // Patient name — pre-filled for customers
        Label nameLbl = fieldLabel("Patient Name");
        TextField tfName = MainApp.styledField(
            "Enter patient name"
        );
        tfName.setMaxWidth(Double.MAX_VALUE);
        if (isCustomer()) {
            tfName.setText(loggedInName);
            tfName.setEditable(false);
            tfName.setStyle(
                tfName.getStyle()
              + "-fx-background-color:#F1F5F9;"
            );
        }

        // Service
        Label svcLbl   = fieldLabel("Service");
        ComboBox<String> cbService = MainApp.styledCombo(
            "Select service"
        );
        cbService.getItems().addAll(
            "Consultation", "Check-up",
            "Follow-up", "Emergency", "Lab Test"
        );

        // Staff
        Label staffLbl = fieldLabel("Assigned Staff");
        ComboBox<String> cbStaff = MainApp.styledCombo(
            "Select staff"
        );
        cbStaff.getItems().addAll(
            "Dr. Smith", "Dr. Johnson", "Dr. Williams"
        );

        // Date
        Label dateLbl = fieldLabel("Date (YYYY-MM-DD)");
        TextField tfDate = MainApp.styledField(
            "e.g. 2026-03-20"
        );
        tfDate.setMaxWidth(Double.MAX_VALUE);

        // Time
        Label timeLbl = fieldLabel("Time (HH:MM:SS)");
        TextField tfTime = MainApp.styledField(
            "e.g. 09:00:00"
        );
        tfTime.setMaxWidth(Double.MAX_VALUE);

        // Status — customers can only book as Scheduled
        Label statusLbl = fieldLabel("Status");
        ComboBox<String> cbStatus = MainApp.styledCombo(
            "Select status"
        );
        if (isCustomer()) {
            cbStatus.getItems().add("Scheduled");
        } else {
            cbStatus.getItems().addAll("Scheduled", "Confirmed");
        }
        cbStatus.setValue("Scheduled");

        form.getChildren().addAll(
            nameLbl,   tfName,
            svcLbl,    cbService,
            staffLbl,  cbStaff,
            dateLbl,   tfDate,
            timeLbl,   tfTime,
            statusLbl, cbStatus
        );

        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setPrefHeight(380);
        dialog.getDialogPane().setContent(sp);

        dialog.showAndWait().ifPresent(result -> {
            if (result == bookBtn) {
                String name    = tfName.getText().trim();
                String service = cbService.getValue();
                String staff   = cbStaff.getValue();
                String date    = tfDate.getText().trim();
                String time    = tfTime.getText().trim();
                String status  = cbStatus.getValue();

                if (name.isEmpty() || service == null
                 || date.isEmpty() || time.isEmpty()) {
                    MainApp.showError("Error",
                        "Please fill in all required fields.");
                    return;
                }

                boolean ok = aDAO.bookAppointment(
                    name, service,
                    staff == null ? "" : staff,
                    date, time, status
                );

                if (ok) {
                    MainApp.showInfo("Success",
                        "Appointment booked for " + name + ".");
                    refresh();
                } else {
                    MainApp.showError("Error",
                        "Could not book appointment. Try again.");
                }
            }
        });
    }

    // ── Filter tables by search text ──────────────────────────────────────
    private void filterTables(String query) {
        if (query == null || query.isEmpty()) {
            refresh();
            return;
        }
        String lower = query.toLowerCase();

        List<Appointment> allToday =
            getAppointmentsForRole(true);
        List<Appointment> allUp =
            getAppointmentsForRole(false);

        if (allToday != null) {
            todayTable.getItems().setAll(
                allToday.stream()
                    .filter(a ->
                        a.getPatientName()
                         .toLowerCase().contains(lower)
                     || a.getServiceName()
                         .toLowerCase().contains(lower)
                     || a.getStatus()
                         .toLowerCase().contains(lower)
                    ).toList()
            );
        }

        if (allUp != null) {
            upcomingTable.getItems().setAll(
                allUp.stream()
                    .filter(a ->
                        a.getPatientName()
                         .toLowerCase().contains(lower)
                     || a.getServiceName()
                         .toLowerCase().contains(lower)
                     || a.getStatus()
                         .toLowerCase().contains(lower)
                    ).toList()
            );
        }
    }

    // ── Get appointments filtered by role ─────────────────────────────────
    private List<Appointment> getAppointmentsForRole(
            boolean isToday) {
        List<Appointment> list = isToday
            ? aDAO.getTodaysAppointments()
            : aDAO.getUpcomingAppointments();

        // Customers only see their own appointments
        if (isCustomer() && list != null) {
            String nameLower = loggedInName.toLowerCase();
            return list.stream()
                .filter(a -> a.getPatientName()
                    .toLowerCase().contains(nameLower))
                .toList();
        }
        return list;
    }

    // ── Refresh all data ──────────────────────────────────────────────────
    public void refresh() {

        // Load appointments filtered by role
        try {
            List<Appointment> today =
                getAppointmentsForRole(true);
            if (today != null)
                todayTable.getItems().setAll(today);
            else
                todayTable.getItems().clear();
        } catch (Exception e) {
            System.err.println("[Appt] today error: "
                + e.getMessage());
            todayTable.getItems().clear();
        }

        try {
            List<Appointment> upcoming =
                getAppointmentsForRole(false);
            if (upcoming != null)
                upcomingTable.getItems().setAll(upcoming);
            else
                upcomingTable.getItems().clear();
        } catch (Exception e) {
            System.err.println("[Appt] upcoming error: "
                + e.getMessage());
            upcomingTable.getItems().clear();
        }

        // Update stat counts
        try {
            lblToday.setText(String.valueOf(
                todayTable.getItems().size()
            ));
            lblUpcoming.setText(String.valueOf(
                upcomingTable.getItems().size()
            ));
            lblTotal.setText(String.valueOf(
                isCustomer()
                    ? todayTable.getItems().size()
                    + upcomingTable.getItems().size()
                    : aDAO.countTotal()
            ));
        } catch (Exception e) {
            System.err.println("[Appt] stats error: "
                + e.getMessage());
        }
    }

    // ── Role helpers ──────────────────────────────────────────────────────
    private boolean isCustomer() {
        return role.equals("Customer");
    }

    private boolean isStaffOrAdmin() {
        return role.equals("Staff") || role.equals("Admin");
    }

    // ── Field label helper ────────────────────────────────────────────────
    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-font-size:12px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );
        return l;
    }
}