package queueflow;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class DashboardController {

    private final QueueDAO       qDAO = new QueueDAO();
    private final AppointmentDAO aDAO = new AppointmentDAO();

    private Label lblQueue;
    private Label lblAppts;
    private Label lblAvgWait;
    private Label lblServed;

    private TableView<QueueToken>  queueTable;
    private TableView<Appointment> upcomingTable;

    /**
     * Builds and returns the full dashboard view.
     */
    public ScrollPane getView() {

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );

        // Page header
        VBox header = MainApp.pageHeader(
            "Dashboard",
            "Welcome back, " + MainApp.getLoggedInUser()
          + "! Here's what's happening today."
        );

        // Stat labels
        lblQueue   = new Label("—");
        lblAppts   = new Label("—");
        lblAvgWait = new Label("—");
        lblServed  = new Label("—");

        // Stats row
        HBox statsRow = new HBox(16,
            statCard("👥", "People in Queue",
                lblQueue,   "+12% from yesterday", MainApp.BLUE),
            statCard("📅", "Today's Appointments",
                lblAppts,   "+8% from yesterday",  MainApp.PURPLE),
            statCard("🕐", "Avg. Wait Time",
                lblAvgWait, "-5% from yesterday",  MainApp.AMBER),
            statCard("📈", "Served Today",
                lblServed,  "+23% from yesterday", MainApp.GREEN)
        );
        for (int i = 0; i < 4; i++)
            HBox.setHgrow(statsRow.getChildren().get(i), Priority.ALWAYS);

        // Bottom panels
        VBox queueCard    = buildCurrentQueuePanel();
        VBox upcomingCard = buildUpcomingPanel();

        HBox bottomRow = new HBox(16, queueCard, upcomingCard);
        HBox.setHgrow(queueCard,    Priority.ALWAYS);
        HBox.setHgrow(upcomingCard, Priority.ALWAYS);

        // Refresh button
        Button btnRefresh = MainApp.primaryBtn("🔄  Refresh");
        btnRefresh.setOnAction(e -> refresh());
        HBox btnRow = new HBox(btnRefresh);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(header, statsRow, bottomRow, btnRow);

        // Load data
        refresh();

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );
        return sp;
    }

    // ── Stat card builder ─────────────────────────────────────────────────
    private VBox statCard(String emoji, String label,
                          Label valueLabel, String trend,
                          String colour) {
        VBox card = MainApp.card();
        card.setSpacing(6);

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label iconCircle = new Label(emoji);
        iconCircle.setStyle(
            "-fx-background-color:" + colour + "22;"
          + "-fx-background-radius:8;"
          + "-fx-padding:8 10;"
          + "-fx-font-size:18px;"
        );

        VBox info = new VBox(2);

        Label lTitle = new Label(label);
        lTitle.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );

        valueLabel.setStyle(
            "-fx-font-size:28px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        Label lTrend = new Label(trend);
        lTrend.setStyle(
            "-fx-font-size:11px;"
          + "-fx-text-fill:" + colour + ";"
        );

        info.getChildren().addAll(lTitle, valueLabel, lTrend);
        top.getChildren().addAll(iconCircle, info);
        card.getChildren().add(top);
        return card;
    }

    // ── Current queue panel ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildCurrentQueuePanel() {
        VBox card = MainApp.card();

        // Panel header
        HBox hdr = new HBox();
        hdr.setAlignment(Pos.CENTER_LEFT);

        Label t = new Label("Current Queue");
        t.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink viewAll = new Hyperlink("View All →");
        viewAll.setStyle(
            "-fx-text-fill:" + MainApp.BLUE + ";"
          + "-fx-font-size:12px;"
        );

        hdr.getChildren().addAll(t, spacer, viewAll);

        // Table
        queueTable = new TableView<>();
        MainApp.styleTable(queueTable);
        queueTable.setPrefHeight(240);
        queueTable.setPlaceholder(
            new Label("No patients in queue today.")
        );

        TableColumn<QueueToken, String> colToken =
            new TableColumn<>("Token");
        colToken.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getTokenLabel()
            )
        );
        colToken.setMaxWidth(70);

        TableColumn<QueueToken, String> colPatient =
            new TableColumn<>("Patient");
        colPatient.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getPatientName()
            )
        );

        TableColumn<QueueToken, String> colService =
            new TableColumn<>("Service");
        colService.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getServiceName()
            )
        );

        TableColumn<QueueToken, String> colTime =
            new TableColumn<>("Check-in");
        colTime.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getIssueTime()
            )
        );

        TableColumn<QueueToken, String> colStatus =
            new TableColumn<>("Status");
        colStatus.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getStatus()
            )
        );
        colStatus.setCellFactory(c -> statusCell());
        colStatus.setMaxWidth(90);

        queueTable.getColumns().addAll(
            colToken, colPatient, colService, colTime, colStatus
        );

        card.getChildren().addAll(hdr, queueTable);
        return card;
    }

    // ── Upcoming appointments panel ───────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildUpcomingPanel() {
        VBox card = MainApp.card();
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        Label t = new Label("Upcoming");
        t.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        upcomingTable = new TableView<>();
        MainApp.styleTable(upcomingTable);
        upcomingTable.setPrefHeight(240);
        upcomingTable.setPlaceholder(
            new Label("No upcoming appointments.")
        );

        TableColumn<Appointment, String> colPat =
            new TableColumn<>("Patient");
        colPat.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getPatientName()
            )
        );

        TableColumn<Appointment, String> colSvc =
            new TableColumn<>("Service");
        colSvc.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getServiceName()
            )
        );

        TableColumn<Appointment, String> colTime =
            new TableColumn<>("Time");
        colTime.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getAppointmentTime()
            )
        );
        colTime.setMaxWidth(80);

        upcomingTable.getColumns().addAll(colPat, colSvc, colTime);
        card.getChildren().addAll(t, upcomingTable);
        return card;
    }

    // ── Refresh all data ──────────────────────────────────────────────────
    public void refresh() {

        // Stats
        try {
            int waiting = qDAO.countWaiting();
            int serving = qDAO.countServing();
            int served  = qDAO.countCompletedToday();
            int appts   = aDAO.countToday();
            lblQueue  .setText(String.valueOf(waiting + serving));
            lblAppts  .setText(String.valueOf(appts));
            lblAvgWait.setText("15 min");
            lblServed .setText(String.valueOf(served));
        } catch (Exception e) {
            System.err.println("[Dashboard] stats error: "
                + e.getMessage());
            lblQueue  .setText("—");
            lblAppts  .setText("—");
            lblAvgWait.setText("—");
            lblServed .setText("—");
        }

        // Queue table
        try {
            List<QueueToken> list = qDAO.getTodaysQueue();
            if (list != null) queueTable.getItems().setAll(list);
            else              queueTable.getItems().clear();
        } catch (Exception e) {
            System.err.println("[Dashboard] queue error: "
                + e.getMessage());
            queueTable.getItems().clear();
        }

        // Upcoming table
        try {
            List<Appointment> list = aDAO.getUpcomingAppointments();
            if (list != null) upcomingTable.getItems().setAll(list);
            else              upcomingTable.getItems().clear();
        } catch (Exception e) {
            System.err.println("[Dashboard] appt error: "
                + e.getMessage());
            upcomingTable.getItems().clear();
        }
    }

    // ── Status badge cell ─────────────────────────────────────────────────
    private TableCell<QueueToken, String> statusCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                setGraphic(MainApp.badge(item, badgeColour(item)));
                setText(null);
            }
        };
    }

    private String badgeColour(String status) {
        if (status == null) return MainApp.BLUE;
        return switch (status) {
            case "Serving"   -> MainApp.AMBER;
            case "Completed" -> MainApp.GREEN;
            case "Skipped"   -> MainApp.TEXT_GREY;
            default          -> MainApp.BLUE;
        };
    }
}