package se2203b.assignments.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se2203b.assignments.SceneNavigator;
import se2203b.assignments.domain.Role;
import se2203b.assignments.domain.UserAccount;
import se2203b.assignments.service.UserAccountService;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class InitialProvisioningController implements Initializable {

    @FXML private Label lblBMStatus, lblNAStatus, lblLOBStatus, lblError, lblCurrentUser;
    @FXML private TextField txtUsernameBM, txtUsernameNA, txtUsernameLOB;
    @FXML private PasswordField txtPasswordBM, txtPasswordNA, txtPasswordLOB;
    @FXML private Button btnProceed;

    private final UserAccountService userAccountService;
    private static UserAccount currentUser;
    private static final int MIN_LEN = 12;

    public static void setCurrentUser(UserAccount user) { currentUser = user; }

    @Autowired
    public InitialProvisioningController(UserAccountService service) {
        this.userAccountService = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentUser == null || currentUser.getRole() != Role.MASTER) {
            SceneNavigator.navigateTo("Login-view.fxml", "iSky – Login");
            return;
        }
        lblCurrentUser.setText("You are signed in as: " + currentUser.getUsername() + " (MASTER)");
        refreshStatus();
    }

    private void refreshStatus() {
        int bm = userAccountService.businessManagersCount();
        int na = userAccountService.networkAdministratorsCount();
        int lob = userAccountService.lineOfBusinessesCount();

        updateStatusLabel(lblBMStatus, bm);
        updateStatusLabel(lblNAStatus, na);
        updateStatusLabel(lblLOBStatus, lob);

        // 👇 Disable input fields once one exists
        boolean bmExists = bm > 0;
        boolean naExists = na > 0;
        boolean lobExists = lob > 0;

        txtUsernameBM.setDisable(bmExists);
        txtPasswordBM.setDisable(bmExists);

        txtUsernameNA.setDisable(naExists);
        txtPasswordNA.setDisable(naExists);

        txtUsernameLOB.setDisable(lobExists);
        txtPasswordLOB.setDisable(lobExists);

        // Proceed button logic (unchanged)
        btnProceed.setDisable(!(bmExists && naExists && lobExists));
    }

    private void updateStatusLabel(Label lbl, int count) {
        if (count == 0) {
            lbl.setText("Missing");
            lbl.setStyle("-fx-text-fill: #b00020; -fx-font-weight: bold;");
        } else {
            lbl.setText("Exists (" + count + ")");
            lbl.setStyle("-fx-text-fill: #1b5e20; -fx-font-weight: bold;");
        }
    }

    @FXML private void handleCreateBM() { process(txtUsernameBM, txtPasswordBM, Role.BUSINESS_MANAGER); }
    @FXML private void handleCreateNA() { process(txtUsernameNA, txtPasswordNA, Role.NETWORK_ADMINISTRATOR); }
    @FXML private void handleCreateLOB() { process(txtUsernameLOB, txtPasswordLOB, Role.LINE_OF_BUSINESS_EXECUTIVE); }

    private void process(TextField userField, PasswordField passField, Role role) {
        String user = userField.getText().trim();
        String pass = passField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Username and password are required.");
            return;
        }
        if (pass.length() < MIN_LEN) {
            lblError.setText("Password must be at least " + MIN_LEN + " characters.");
            return;
        }

        try {
            userAccountService.createUser(user, pass, role);
            userField.clear();
            passField.clear();
            lblError.setText("Account created successfully!");
            refreshStatus();
        } catch (Exception e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML private void handleProceed() {
        MainPortalController.setCurrentUser(currentUser);
        SceneNavigator.navigateTo("MainPortal-view.fxml", "iSky Portal");
    }
}