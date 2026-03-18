package queueflow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * MainApp - JavaFX entry point and shared styling/utility methods.
 */
public class MainApp extends Application {

    // ── Shared colour constants ───────────────────────────────────────────
    public static final String BG_GREY   = "#F8F9FA";
    public static final String WHITE     = "#FFFFFF";
    public static final String BLUE      = "#2563EB";
    public static final String GREEN     = "#16A34A";
    public static final String AMBER     = "#D97706";
    public static final String RED       = "#DC2626";
    public static final String PURPLE    = "#7B1FA2";
    public static final String TEXT_DARK = "#1E293B";
    public static final String TEXT_GREY = "#64748B";
    public static final String BORDER    = "#E2E8F0";
    public static final String CARD_BG   = "#FFFFFF";

    // ── Session state ─────────────────────────────────────────────────────
    private static Stage  primaryStage;
    private static String loggedInUser = "";
    private static String loggedInRole = "";

    // ── Navigation controllers ────────────────────────────────────────────
    private static DashboardController   dashCtrl;
    private static QueueController       queueCtrl;
    private static AppointmentController apptCtrl;
    private static SettingsController    settCtrl;

    // ── Active nav button tracker ─────────────────────────────────────────
    private static Button btnNavDash;
    private static Button btnNavQueue;
    private static Button btnNavAppt;
    private static Button btnNavSett;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("QueueFlow — Queue & Appointment Management");
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(640);
        showLogin();
        primaryStage.show();
    }

    // ── Login screen ──────────────────────────────────────────────────────
    public static void showLogin() {
        // Reset all controllers so role restrictions
        // rebuild fresh on next login
        dashCtrl  = null;
        queueCtrl = null;
        apptCtrl  = null;
        settCtrl  = null;

        // Reset session
        loggedInUser = "";
        loggedInRole = "";

        LoginController login = new LoginController();
        Scene scene = new Scene(login.getView(), 960, 640);
        primaryStage.setScene(scene);
    }

    // ── Main app shell (sidebar + content area) ───────────────────────────
    public static void showMainApp(String userName, String role) {
        loggedInUser = userName;
        loggedInRole = role;

        dashCtrl  = new DashboardController();
        queueCtrl = new QueueController(role);
        apptCtrl  = new AppointmentController(role,  userName);
        settCtrl  = new SettingsController();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG_GREY + ";");

        // ── Sidebar ───────────────────────────────────────────────────────
        VBox sidebar = buildSidebar(root, role);
        root.setLeft(sidebar);

        // ── Default view: Dashboard ───────────────────────────────────────
        root.setCenter(dashCtrl.getView());
        setActiveNav(btnNavDash);

        Scene scene = new Scene(root, 960, 640);
        primaryStage.setScene(scene);
    }

    // ── Sidebar builder ───────────────────────────────────────────────────
    private static VBox buildSidebar(BorderPane root, String role) {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(200);
        sidebar.setStyle(
            "-fx-background-color:" + WHITE + ";"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-width:0 1 0 0;"
        );

        // Brand header
        VBox brand = new VBox(2);
        brand.setPadding(new Insets(20, 16, 16, 16));
        brand.setStyle(
            "-fx-border-color:" + BORDER + ";"
          + "-fx-border-width:0 0 1 0;"
        );
        Label brandIcon = new Label("🕐");
        brandIcon.setStyle(
            "-fx-background-color:" + BLUE + ";"
          + "-fx-background-radius:10;"
          + "-fx-padding:6 10;"
          + "-fx-font-size:18px;"
        );
        Label brandName = new Label("QueueFlow");
        brandName.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + TEXT_DARK + ";"
        );
        Label brandSub = new Label("Management System");
        brandSub.setStyle(
            "-fx-font-size:11px;"
          + "-fx-text-fill:" + TEXT_GREY + ";"
        );
        brand.getChildren().addAll(brandIcon, brandName, brandSub);

        // Nav buttons
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 8, 8));

        btnNavDash  = navBtn("🏠  Dashboard");
        btnNavQueue = navBtn("👥  Queue");
        btnNavAppt  = navBtn("📅  Appointments");
        btnNavSett  = navBtn("⚙️  Settings");

        // Hide Settings for non-admin roles
        if (!role.equals("Admin")) {
            btnNavSett.setVisible(false);
            btnNavSett.setManaged(false);
        }

        btnNavDash.setOnAction(e -> {
            root.setCenter(dashCtrl.getView());
            setActiveNav(btnNavDash);
        });
        btnNavQueue.setOnAction(e -> {
            root.setCenter(queueCtrl.getView());
            setActiveNav(btnNavQueue);
        });
        btnNavAppt.setOnAction(e -> {
            root.setCenter(apptCtrl.getView());
            setActiveNav(btnNavAppt);
        });
        btnNavSett.setOnAction(e -> {
            root.setCenter(settCtrl.getView());
            setActiveNav(btnNavSett);
        });

        nav.getChildren().addAll(
            btnNavDash, btnNavQueue, btnNavAppt, btnNavSett
        );

        // Spacer pushes logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User info + logout
        VBox bottom = new VBox(6);
        bottom.setPadding(new Insets(12, 12, 16, 12));
        bottom.setStyle(
            "-fx-border-color:" + BORDER + ";"
          + "-fx-border-width:1 0 0 0;"
        );
        Label lblUser = new Label("👤 " + loggedInUser);
        lblUser.setStyle(
            "-fx-font-size:12px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + TEXT_DARK + ";"
        );
        Label lblRole = new Label(loggedInRole);
        lblRole.setStyle(
            "-fx-font-size:11px;"
          + "-fx-text-fill:" + TEXT_GREY + ";"
        );
        Button btnLogout = new Button("Logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle(
            "-fx-background-color:transparent;"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-radius:6;"
          + "-fx-font-size:12px;"
          + "-fx-text-fill:" + RED + ";"
          + "-fx-cursor:hand;"
          + "-fx-padding:6 12;"
        );
        btnLogout.setOnAction(e -> showLogin());
        bottom.getChildren().addAll(lblUser, lblRole, btnLogout);

        sidebar.getChildren().addAll(brand, nav, spacer, bottom);
        return sidebar;
    }

    // ── Nav button active state ───────────────────────────────────────────
    private static void setActiveNav(Button active) {
        for (Button b : new Button[]{
                btnNavDash, btnNavQueue, btnNavAppt, btnNavSett}) {
            if (b == null) continue;
            if (b == active) {
                b.setStyle(
                    "-fx-background-color:" + BLUE + "11;"
                  + "-fx-border-color:transparent transparent "
                  + "transparent " + BLUE + ";"
                  + "-fx-border-width:0 0 0 3;"
                  + "-fx-text-fill:" + BLUE + ";"
                  + "-fx-font-weight:bold;"
                  + "-fx-font-size:13px;"
                  + "-fx-alignment:CENTER_LEFT;"
                  + "-fx-padding:10 12;"
                  + "-fx-background-radius:0;"
                  + "-fx-cursor:hand;"
                );
            } else {
                b.setStyle(
                    "-fx-background-color:transparent;"
                  + "-fx-text-fill:" + TEXT_GREY + ";"
                  + "-fx-font-size:13px;"
                  + "-fx-alignment:CENTER_LEFT;"
                  + "-fx-padding:10 12;"
                  + "-fx-background-radius:6;"
                  + "-fx-cursor:hand;"
                );
            }
        }
    }

    // ── Shared UI factory methods ─────────────────────────────────────────

    /** Creates a styled navigation button. */
    public static Button navBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
            "-fx-background-color:transparent;"
          + "-fx-text-fill:" + TEXT_GREY + ";"
          + "-fx-font-size:13px;"
          + "-fx-alignment:CENTER_LEFT;"
          + "-fx-padding:10 12;"
          + "-fx-background-radius:6;"
          + "-fx-cursor:hand;"
        );
        return b;
    }

    /** Creates a styled page header with title and subtitle. */
    public static VBox pageHeader(String title, String subtitle) {
        VBox box = new VBox(4);
        Label t = new Label(title);
        t.setStyle(
            "-fx-font-size:22px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + TEXT_DARK + ";"
        );
        Label s = new Label(subtitle);
        s.setStyle(
            "-fx-font-size:13px;"
          + "-fx-text-fill:" + TEXT_GREY + ";"
        );
        box.getChildren().addAll(t, s);
        return box;
    }

    /** Creates a white rounded card VBox. */
    public static VBox card() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color:" + CARD_BG + ";"
          + "-fx-background-radius:12;"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-radius:12;"
          + "-fx-border-width:1;"
        );
        return card;
    }

    /** Creates a blue primary button. */
    public static Button primaryBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + BLUE + ";"
          + "-fx-text-fill:white;"
          + "-fx-font-weight:bold;"
          + "-fx-font-size:13px;"
          + "-fx-padding:8 20;"
          + "-fx-background-radius:8;"
          + "-fx-cursor:hand;"
        );
        return b;
    }

    /** Creates a green success button. */
    public static Button successBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + GREEN + ";"
          + "-fx-text-fill:white;"
          + "-fx-font-weight:bold;"
          + "-fx-font-size:12px;"
          + "-fx-padding:6 14;"
          + "-fx-background-radius:6;"
          + "-fx-cursor:hand;"
        );
        return b;
    }

    /** Creates a red danger button. */
    public static Button dangerBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + RED + ";"
          + "-fx-text-fill:white;"
          + "-fx-font-weight:bold;"
          + "-fx-font-size:12px;"
          + "-fx-padding:6 14;"
          + "-fx-background-radius:6;"
          + "-fx-cursor:hand;"
        );
        return b;
    }

    /** Creates an outlined secondary button. */
    public static Button outlineBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:transparent;"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-radius:6;"
          + "-fx-font-size:12px;"
          + "-fx-text-fill:" + TEXT_DARK + ";"
          + "-fx-padding:6 14;"
          + "-fx-cursor:hand;"
        );
        return b;
    }

    /** Applies consistent styling to any TableView. */
    public static <T> void styleTable(TableView<T> table) {
        table.setStyle(
            "-fx-background-color:" + WHITE + ";"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-radius:8;"
          + "-fx-table-cell-border-color:" + BORDER + ";"
          + "-fx-font-size:13px;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /** Creates a styled text field. */
    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color:" + WHITE + ";"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-radius:6;"
          + "-fx-padding:8 12;"
          + "-fx-font-size:13px;"
        );
        return tf;
    }

    /** Creates a styled combo box. */
    public static ComboBox<String> styledCombo(String prompt) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle(
            "-fx-background-color:" + WHITE + ";"
          + "-fx-border-color:" + BORDER + ";"
          + "-fx-border-radius:6;"
          + "-fx-font-size:13px;"
        );
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    /** Creates a coloured status badge label. */
    public static Label badge(String text, String colour) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-background-color:" + colour + "22;"
          + "-fx-text-fill:"        + colour + ";"
          + "-fx-background-radius:6;"
          + "-fx-padding:2 8;"
          + "-fx-font-size:11px;"
          + "-fx-font-weight:bold;"
        );
        return l;
    }

    /** Shows an information alert dialog. */
    public static void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    /** Shows an error alert dialog. */
    public static void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    /** Shows a confirmation dialog. Returns true if OK clicked. */
    public static boolean showConfirm(String title, String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        return a.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .isPresent();
    }

    /** Returns the currently logged in user's name. */
    public static String getLoggedInUser() { return loggedInUser; }

    /** Returns the currently logged in user's role. */
    public static String getLoggedInRole() { return loggedInRole; }

    public static void main(String[] args) {
        launch(args);
    }
}


