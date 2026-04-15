package se2203b.assignments.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se2203b.assignments.SceneNavigator;
import se2203b.assignments.domain.UserAccount;
import se2203b.assignments.service.UserAccountService;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class ChangePasswordController implements Initializable {

    @FXML private PasswordField txtCurrent;
    @FXML private PasswordField txtNew;
    @FXML private PasswordField txtConfirm;
    @FXML private Label lblError;

    private final UserAccountService userAccountService;
    private static UserAccount currentUser;

    private static final int MIN_LEN = 12;
    private static final String[] COMMON_BAD = {
            "password", "password123", "12345678", "qwerty", "letmein", "admin", "welcome"
    };

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    @Autowired
    public ChangePasswordController(UserAccountService service) {
        this.userAccountService = service;
    }

    @FXML private Label lblSignedInAs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure the currentUser object is not null before setting text
        if (currentUser != null) {
            // This replaces "[User] ([ROLE])" with actual data like "bm-01 (BUSINESS_MANAGER)"
            lblSignedInAs.setText("Signed in as: " + currentUser.getUsername() +
                    " (" + currentUser.getRole().toString() + ")");
        }
    }

    @FXML
    private void handleChangePassword() {
        String current = txtCurrent.getText();
        String newPwd  = txtNew.getText();
        String confirm = txtConfirm.getText();

        // Validate
        if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            lblError.setText("All fields are required.");
            return;
        }
        if (!newPwd.equals(confirm)) {
            lblError.setText("New passwords do not match.");
            return;
        }
        if (newPwd.length() < MIN_LEN) {
            lblError.setText("New password must be at least " + MIN_LEN + " characters.");
            return;
        }
        if (newPwd.equalsIgnoreCase(currentUser.getUsername())) {
            lblError.setText("Password must not match your username.");
            return;
        }
        if (isCommonPassword(newPwd)) {
            lblError.setText("Password is too common. Choose a stronger one.");
            return;
        }

        try {
            userAccountService.changePassword(currentUser.getUsername(), current, newPwd);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Changed");
            alert.setHeaderText("Password changed successfully.");
            alert.setContentText("Please log in again with your new password.");
            alert.showAndWait();

            // Force re-login
            SceneNavigator.navigateTo("Login-view.fxml", "iSky – Login");

        } catch (IllegalArgumentException e) {
            lblError.setText("Current password is incorrect.");
            txtCurrent.clear();
        }
    }

    @FXML
    private void handleExit() {
        txtCurrent.getScene().getWindow().hide();
    }

    private boolean isCommonPassword(String p) {
        String x = p.trim().toLowerCase();
        for (String bad : COMMON_BAD) if (x.equals(bad)) return true;
        return false;
    }
}