package queueflow;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * QueueController - Queue management screen matching Figure 8.
 * Role-based access: Staff and Admin can manage queue.
 * Customers can only view the queue.
 *
 * OOP: Encapsulation, Single Responsibility Principle
 *
 * @author Luna Twayana (2533423)
 * @version 1.1 - CIS096-1 Week 9
 */
public class QueueController {

    private final QueueDAO qDAO = new QueueDAO();
    private final String   role;

    private Label lblWaiting;
    private Label lblServing;
    private Label lblCompleted;

    private TableView<QueueToken> queueTable;

    /**
     * Constructor accepts role for access control.
     */
    public QueueController(String role) {
        this.role = role;
    }

    /**
     * Builds and returns the full queue management view.
     */
    public ScrollPane getView() {

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );

        // ── Page header + Add button ──────────────────────────────────────
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = MainApp.pageHeader(
            "Queue Management",
            "Manage and track customer queue in real-time."
        );
        HBox.setHgrow(header, Priority.ALWAYS);

        // Only Staff and Admin can add to queue
        if (isStaffOrAdmin()) {
            Button btnAdd = MainApp.primaryBtn("+ Add to Queue");
            btnAdd.setOnAction(e -> showAddDialog());
            topRow.getChildren().addAll(header, btnAdd);
        } else {
            // Customer sees a read-only label instead
            Label lblReadOnly = new Label("👁  View Only");
            lblReadOnly.setStyle(
                "-fx-background-color:" + MainApp.AMBER + "22;"
              + "-fx-text-fill:" + MainApp.AMBER + ";"
              + "-fx-background-radius:8;"
              + "-fx-padding:6 14;"
              + "-fx-font-size:12px;"
              + "-fx-font-weight:bold;"
            );
            topRow.getChildren().addAll(header, lblReadOnly);
        }

        // ── Stat mini cards ───────────────────────────────────────────────
        lblWaiting   = new Label("—");
        lblServing   = new Label("—");
        lblCompleted = new Label("—");

        HBox statsRow = new HBox(16,
            miniStatCard("🕐", "Waiting",     lblWaiting,   MainApp.AMBER),
            miniStatCard("▶",  "Now Serving", lblServing,   MainApp.BLUE),
            miniStatCard("✓",  "Completed",   lblCompleted, MainApp.GREEN)
        );
        for (int i = 0; i < 3; i++)
            HBox.setHgrow(statsRow.getChildren().get(i), Priority.ALWAYS);

        // ── Queue list card ───────────────────────────────────────────────
        VBox queueCard = buildQueueListPanel();

        // ── Refresh button ────────────────────────────────────────────────
        Button btnRefresh = MainApp.outlineBtn("🔄  Refresh");
        btnRefresh.setOnAction(e -> refresh());
        HBox btnRow = new HBox(btnRefresh);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(
            topRow, statsRow, queueCard, btnRow
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
          + "-fx-background-radius:50%;"
          + "-fx-padding:10 12;"
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

    // ── Queue list panel ──────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildQueueListPanel() {
        VBox card = MainApp.card();

        Label title = new Label("Queue List");
        title.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        queueTable = new TableView<>();
        MainApp.styleTable(queueTable);
        queueTable.setPrefHeight(380);
        queueTable.setPlaceholder(
            new Label("No patients in queue today.")
        );

        // Token column
        TableColumn<QueueToken, String> colToken =
            new TableColumn<>("Token");
        colToken.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getTokenLabel()
            )
        );
        colToken.setMaxWidth(75);

        // Patient column
        TableColumn<QueueToken, String> colPatient =
            new TableColumn<>("Patient");
        colPatient.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getPatientName()
            )
        );

        // Service column
        TableColumn<QueueToken, String> colService =
            new TableColumn<>("Service");
        colService.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getServiceName()
            )
        );

        // Joined time column
        TableColumn<QueueToken, String> colTime =
            new TableColumn<>("Joined");
        colTime.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getIssueTime()
            )
        );
        colTime.setMaxWidth(90);

        // Status column with badge
        TableColumn<QueueToken, String> colStatus =
            new TableColumn<>("Status");
        colStatus.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getStatus()
            )
        );
        colStatus.setCellFactory(c -> statusBadgeCell());
        colStatus.setMaxWidth(100);

        // Actions column — only shown to Staff and Admin
        if (isStaffOrAdmin()) {
            TableColumn<QueueToken, Void> colActions =
                new TableColumn<>("Actions");
            colActions.setCellFactory(c -> actionCell());
            colActions.setMinWidth(200);
            colActions.setMaxWidth(220);
            queueTable.getColumns().addAll(
                colToken, colPatient, colService,
                colTime, colStatus, colActions
            );
        } else {
            // Customers see no actions column
            queueTable.getColumns().addAll(
                colToken, colPatient, colService,
                colTime, colStatus
            );
        }

        card.getChildren().addAll(title, queueTable);
        return card;
    }

    // ── Action buttons cell (Staff + Admin only) ──────────────────────────
    private TableCell<QueueToken, Void> actionCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                QueueToken token = getTableView()
                    .getItems().get(getIndex());

                HBox box = new HBox(8);
                box.setAlignment(Pos.CENTER_LEFT);

                if (token.getStatus().equals("Waiting")) {
                    Button btnServe = new Button("▶  Serve");
                    btnServe.setStyle(
                        "-fx-background-color:" + MainApp.BLUE + "22;"
                      + "-fx-text-fill:" + MainApp.BLUE + ";"
                      + "-fx-font-size:11px;"
                      + "-fx-font-weight:bold;"
                      + "-fx-padding:5 12;"
                      + "-fx-background-radius:6;"
                      + "-fx-cursor:hand;"
                      + "-fx-border-color:" + MainApp.BLUE + ";"
                      + "-fx-border-radius:6;"
                      + "-fx-border-width:1;"
                    );
                    btnServe.setOnAction(e -> {
                        qDAO.updateStatus(
                            token.getTokenID(), "Serving"
                        );
                        refresh();
                    });
                    box.getChildren().add(btnServe);

                } else if (token.getStatus().equals("Serving")) {
                    Button btnComplete = new Button("✓  Complete");
                    btnComplete.setStyle(
                        "-fx-background-color:" + MainApp.GREEN + "22;"
                      + "-fx-text-fill:" + MainApp.GREEN + ";"
                      + "-fx-font-size:11px;"
                      + "-fx-font-weight:bold;"
                      + "-fx-padding:5 12;"
                      + "-fx-background-radius:6;"
                      + "-fx-cursor:hand;"
                      + "-fx-border-color:" + MainApp.GREEN + ";"
                      + "-fx-border-radius:6;"
                      + "-fx-border-width:1;"
                    );
                    btnComplete.setOnAction(e -> {
                        qDAO.updateStatus(
                            token.getTokenID(), "Completed"
                        );
                        refresh();
                    });
                    box.getChildren().add(btnComplete);
                }

                // Remove button
                Button btnRemove = new Button("✕");
                btnRemove.setStyle(
                    "-fx-background-color:transparent;"
                  + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
                  + "-fx-font-size:12px;"
                  + "-fx-padding:5 10;"
                  + "-fx-background-radius:6;"
                  + "-fx-cursor:hand;"
                  + "-fx-border-color:" + MainApp.BORDER + ";"
                  + "-fx-border-radius:6;"
                  + "-fx-border-width:1;"
                );
                btnRemove.setOnAction(e -> {
                    boolean ok = MainApp.showConfirm(
                        "Remove Token",
                        "Remove " + token.getPatientName()
                      + " from the queue?"
                    );
                    if (ok) {
                        qDAO.removeToken(token.getTokenID());
                        refresh();
                    }
                });

                box.getChildren().add(btnRemove);
                setGraphic(box);
            }
        };
    }

    // ── Status badge cell ─────────────────────────────────────────────────
    private TableCell<QueueToken, String> statusBadgeCell() {
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
                    case "Serving"   -> MainApp.AMBER;
                    case "Completed" -> MainApp.GREEN;
                    case "Skipped"   -> MainApp.TEXT_GREY;
                    default          -> MainApp.BLUE;
                };
                setGraphic(MainApp.badge(item, colour));
                setText(null);
            }
        };
    }

    // ── Add to queue dialog (Staff + Admin only) ──────────────────────────
    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add to Queue");
        dialog.setHeaderText("Enter patient details");

        ButtonType addBtn = new ButtonType(
            "Add to Queue", ButtonBar.ButtonData.OK_DONE
        );
        dialog.getDialogPane().getButtonTypes().addAll(
            addBtn, ButtonType.CANCEL
        );

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(340);

        Label nameLbl = new Label("Patient Name");
        nameLbl.setStyle(
            "-fx-font-size:12px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );
        TextField tfName = MainApp.styledField(
            "Enter patient name"
        );
        tfName.setMaxWidth(Double.MAX_VALUE);

        Label svcLbl = new Label("Service");
        svcLbl.setStyle(
            "-fx-font-size:12px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );
        ComboBox<String> cbService = MainApp.styledCombo(
            "Select service"
        );
        cbService.getItems().addAll(
            "Consultation", "Check-up",
            "Follow-up", "Emergency", "Lab Test"
        );

        form.getChildren().addAll(
            nameLbl, tfName, svcLbl, cbService
        );

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result == addBtn) {
                String name    = tfName.getText().trim();
                String service = cbService.getValue();

                if (name.isEmpty() || service == null) {
                    MainApp.showError("Error",
                        "Please fill in all fields.");
                    return;
                }

                boolean ok = qDAO.addToQueue(name, service);
                if (ok) {
                    MainApp.showInfo("Success",
                        name + " added to queue successfully.");
                    refresh();
                } else {
                    MainApp.showError("Error",
                        "Could not add to queue. Try again.");
                }
            }
        });
    }

    // ── Refresh all data ──────────────────────────────────────────────────
    public void refresh() {
        try {
            lblWaiting  .setText(
                String.valueOf(qDAO.countWaiting())
            );
            lblServing  .setText(
                String.valueOf(qDAO.countServing())
            );
            lblCompleted.setText(
                String.valueOf(qDAO.countCompletedToday())
            );
        } catch (Exception e) {
            System.err.println("[Queue] stats error: "
                + e.getMessage());
        }

        try {
            List<QueueToken> list = qDAO.getTodaysQueue();
            if (list != null) queueTable.getItems().setAll(list);
            else              queueTable.getItems().clear();
        } catch (Exception e) {
            System.err.println("[Queue] table error: "
                + e.getMessage());
            queueTable.getItems().clear();
        }
    }

    // ── Role helper ───────────────────────────────────────────────────────
    private boolean isStaffOrAdmin() {
        return role.equals("Staff") || role.equals("Admin");
    }
}