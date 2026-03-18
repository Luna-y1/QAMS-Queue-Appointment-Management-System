package queueflow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * LoginController - Handles login and registration UI and logic
 */
public class LoginController {

    private final UserDAO userDAO = new UserDAO();

    // ── Form fields ───────────────────────────────────────────────────────
    private TextField     tfEmail;
    private PasswordField pfPassword;
    private TextField     tfRegName;
    private TextField     tfRegEmail;
    private PasswordField pfRegPassword;
    private ComboBox<String> cbRole;

    // ── Tab state ─────────────────────────────────────────────────────────
    private boolean isSignIn = true;
    private VBox    signInForm;
    private VBox    registerForm;
    private Button  btnSignInTab;
    private Button  btnRegisterTab;

    /**
     * Builds and returns the full login view.
     */
    public StackPane getView() {

        // Outer background
        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color:" + MainApp.BG_GREY + ";"
        );

        // Center card
        VBox card = new VBox(0);
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color:" + MainApp.WHITE + ";"
          + "-fx-background-radius:16;"
          + "-fx-border-color:" + MainApp.BORDER + ";"
          + "-fx-border-radius:16;"
          + "-fx-border-width:1;"
        );
        card.setEffect(null);

        // ── Card header ───────────────────────────────────────────────────
        VBox header = new VBox(6);
        header.setPadding(new Insets(32, 32, 20, 32));
        header.setAlignment(Pos.CENTER);

        Label iconLabel = new Label("🕐");
        iconLabel.setStyle(
            "-fx-background-color:" + MainApp.BLUE + ";"
          + "-fx-background-radius:14;"
          + "-fx-padding:10 14;"
          + "-fx-font-size:22px;"
        );

        Label title = new Label("Welcome to QueueFlow");
        title.setStyle(
            "-fx-font-size:20px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        Label subtitle = new Label(
            "Streamline your queue and appointment management"
        );
        subtitle.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        subtitle.setWrapText(true);
        subtitle.setAlignment(Pos.CENTER);

        header.getChildren().addAll(iconLabel, title, subtitle);

        // ── Tab row ───────────────────────────────────────────────────────
        HBox tabRow = new HBox(0);
        tabRow.setPadding(new Insets(0, 32, 0, 32));

        btnSignInTab  = tabBtn("Sign In",       true);
        btnRegisterTab = tabBtn("Create Account", false);

        btnSignInTab.setOnAction(e  -> switchTab(true));
        btnRegisterTab.setOnAction(e -> switchTab(false));

        tabRow.getChildren().addAll(btnSignInTab, btnRegisterTab);

        // ── Sign in form ──────────────────────────────────────────────────
        signInForm = buildSignInForm();

        // ── Register form ─────────────────────────────────────────────────
        registerForm = buildRegisterForm();
        registerForm.setVisible(false);
        registerForm.setManaged(false);

        // ── Demo box ──────────────────────────────────────────────────────
        VBox demoBox = new VBox(4);
        demoBox.setPadding(new Insets(0, 32, 28, 32));
        demoBox.setStyle(
            "-fx-background-color:" + MainApp.BLUE + "11;"
          + "-fx-background-radius:8;"
          + "-fx-padding:10 14;"
        );
        Label demoTitle = new Label("✅  Quick Demo Access:");
        demoTitle.setStyle(
            "-fx-font-weight:bold;"
          + "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.BLUE + ";"
        );
        Label demoInfo = new Label(
            "Email: demo@queueflow.com    Password: demo123"
        );
        demoInfo.setStyle(
            "-fx-font-size:11px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        demoBox.getChildren().addAll(demoTitle, demoInfo);

        VBox demoWrap = new VBox(demoBox);
        demoWrap.setPadding(new Insets(0, 32, 28, 32));

        card.getChildren().addAll(
            header, tabRow, signInForm, registerForm, demoWrap
        );

        StackPane.setAlignment(card, Pos.CENTER);
        root.getChildren().add(card);
        return root;
    }

    // ── Sign in form builder ──────────────────────────────────────────────
    private VBox buildSignInForm() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(20, 32, 16, 32));

        // Get Started label
        Label lbl = new Label("Get Started");
        lbl.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );
        Label sub = new Label(
            "Sign in to your account or create a new one to continue"
        );
        sub.setStyle(
            "-fx-font-size:12px;"
          + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
        );
        sub.setWrapText(true);

        // Email
        VBox emailBox = new VBox(5);
        Label emailLbl = fieldLabel("Email Address");
        tfEmail = MainApp.styledField("you@example.com");
        tfEmail.setMaxWidth(Double.MAX_VALUE);
        emailBox.getChildren().addAll(emailLbl, tfEmail);

        // Password
        VBox passBox = new VBox(5);
        Label passLbl = fieldLabel("Password");
        pfPassword = new PasswordField();
        pfPassword.setPromptText("Enter your password");
        pfPassword.setStyle(
            "-fx-background-color:" + MainApp.WHITE + ";"
          + "-fx-border-color:" + MainApp.BORDER + ";"
          + "-fx-border-radius:6;"
          + "-fx-padding:8 12;"
          + "-fx-font-size:13px;"
        );
        passBox.getChildren().addAll(passLbl, pfPassword);

        // Sign in button
        Button btnSignIn = MainApp.primaryBtn("Sign In to Dashboard  →");
        btnSignIn.setMaxWidth(Double.MAX_VALUE);
        btnSignIn.setOnAction(e -> handleLogin());

        // Allow Enter key to trigger login
        pfPassword.setOnAction(e -> handleLogin());
        tfEmail.setOnAction(e    -> handleLogin());

        // Forgot password
        Label forgot = new Label("Forgot your password?");
        forgot.setStyle(
            "-fx-text-fill:" + MainApp.BLUE + ";"
          + "-fx-font-size:12px;"
          + "-fx-cursor:hand;"
        );
        HBox forgotRow = new HBox(forgot);
        forgotRow.setAlignment(Pos.CENTER);

        form.getChildren().addAll(
            lbl, sub, emailBox, passBox, btnSignIn, forgotRow
        );
        return form;
    }

    // ── Register form builder ─────────────────────────────────────────────
    private VBox buildRegisterForm() {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20, 32, 16, 32));

        Label lbl = new Label("Create Account");
        lbl.setStyle(
            "-fx-font-size:15px;"
          + "-fx-font-weight:bold;"
          + "-fx-text-fill:" + MainApp.TEXT_DARK + ";"
        );

        // Full name
        VBox nameBox = new VBox(5);
        nameBox.getChildren().addAll(
            fieldLabel("Full Name"),
            tfRegName = MainApp.styledField("Enter your full name")
        );
        tfRegName.setMaxWidth(Double.MAX_VALUE);

        // Email
        VBox emailBox = new VBox(5);
        emailBox.getChildren().addAll(
            fieldLabel("Email Address"),
            tfRegEmail = MainApp.styledField("you@example.com")
        );
        tfRegEmail.setMaxWidth(Double.MAX_VALUE);

        // Password
        VBox passBox = new VBox(5);
        pfRegPassword = new PasswordField();
        pfRegPassword.setPromptText("Create a password");
        pfRegPassword.setStyle(
            "-fx-background-color:" + MainApp.WHITE + ";"
          + "-fx-border-color:" + MainApp.BORDER + ";"
          + "-fx-border-radius:6;"
          + "-fx-padding:8 12;"
          + "-fx-font-size:13px;"
        );
        passBox.getChildren().addAll(fieldLabel("Password"), pfRegPassword);

        // Role
        VBox roleBox = new VBox(5);
        cbRole = MainApp.styledCombo("Select role");
        cbRole.getItems().addAll("Customer", "Staff", "Admin");
        cbRole.setValue("Customer");
        roleBox.getChildren().addAll(fieldLabel("Role"), cbRole);

        // Register button
        Button btnRegister = MainApp.primaryBtn("Create Account  →");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setOnAction(e -> handleRegister());

        form.getChildren().addAll(
            lbl, nameBox, emailBox, passBox, roleBox, btnRegister
        );
        return form;
    }

    // ── Login handler ─────────────────────────────────────────────────────
    private void handleLogin() {
        String email    = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            MainApp.showError("Login Failed",
                "Please enter your email and password.");
            return;
        }

        try {
            java.sql.ResultSet rs = userDAO.loginUser(email, password);
            if (rs != null && rs.next()) {
                String name = rs.getString("fullName");
                String role = rs.getString("role");
                MainApp.showMainApp(name, role);
            } else {
                MainApp.showError("Login Failed",
                    "Invalid email or password. Please try again.");
            }
        } catch (Exception ex) {
            MainApp.showError("Login Error", ex.getMessage());
        }
    }

    // ── Register handler ──────────────────────────────────────────────────
    private void handleRegister() {
        String name     = tfRegName.getText().trim();
        String email    = tfRegEmail.getText().trim();
        String password = pfRegPassword.getText().trim();
        String role     = cbRole.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            MainApp.showError("Registration Failed",
                "Please fill in all fields.");
            return;
        }

        if (!email.contains("@")) {
            MainApp.showError("Registration Failed",
                "Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            MainApp.showError("Registration Failed",
                "Password must be at least 6 characters.");
            return;
        }

        boolean success = userDAO.registerUser(name, email, password, role);
        if (success) {
            MainApp.showInfo("Account Created",
                "Account created successfully! You can now sign in.");
            switchTab(true);
            tfEmail.setText(email);
        } else {
            MainApp.showError("Registration Failed",
                "Email already exists. Please use a different email.");
        }
    }

    // ── Tab switcher ──────────────────────────────────────────────────────
    private void switchTab(boolean toSignIn) {
        isSignIn = toSignIn;

        signInForm.setVisible(toSignIn);
        signInForm.setManaged(toSignIn);
        registerForm.setVisible(!toSignIn);
        registerForm.setManaged(!toSignIn);

        styleActiveTab(btnSignInTab,   toSignIn);
        styleActiveTab(btnRegisterTab, !toSignIn);
    }

    // ── Tab button builder ────────────────────────────────────────────────
    private Button tabBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b, Priority.ALWAYS);
        styleActiveTab(b, active);
        return b;
    }

    private void styleActiveTab(Button b, boolean active) {
        if (active) {
            b.setStyle(
                "-fx-background-color:" + MainApp.WHITE + ";"
              + "-fx-border-color:" + MainApp.BLUE + ";"
              + "-fx-border-width:0 0 2 0;"
              + "-fx-font-weight:bold;"
              + "-fx-font-size:13px;"
              + "-fx-text-fill:" + MainApp.BLUE + ";"
              + "-fx-padding:10 16;"
              + "-fx-cursor:hand;"
              + "-fx-background-radius:0;"
            );
        } else {
            b.setStyle(
                "-fx-background-color:" + MainApp.BG_GREY + ";"
              + "-fx-border-color:transparent;"
              + "-fx-font-size:13px;"
              + "-fx-text-fill:" + MainApp.TEXT_GREY + ";"
              + "-fx-padding:10 16;"
              + "-fx-cursor:hand;"
              + "-fx-background-radius:0;"
            );
        }
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
