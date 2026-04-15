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
public class LoginController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final UserAccountService userAccountService;

    @Autowired
    public LoginController(UserAccountService service) {
        this.userAccountService = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        System.out.println("LOGIN CLICKED");

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Invalid user ID or password.");
            return;
        }

        UserAccount user = userAccountService.authenticate(username, password);


        if (user == null) {
            lblError.setText("Invalid user ID or password.");
            txtPassword.clear();
            return;
        }

        // Force password change if flagged
        if (user.isMustChangePassword()) {
            ChangePasswordController.setCurrentUser(user);
            SceneNavigator.navigateTo("ChangePassword-view.fxml", "iSky – Change Password");
            return;
        }

        // Route based on role
        routeAfterLogin(user);
    }

    public static void routeAfterLogin(UserAccount user) {
        UserAccountService svc = se2203b.assignments.iSkyApplication
                .getSpringContext().getBean(UserAccountService.class);

        // Master: check if provisioning is needed
        if (user.getRole() == Role.MASTER) {
            boolean needsProvisioning =
                    svc.businessManagersCount() == 0 ||
                            svc.networkAdministratorsCount() == 0 ||
                            svc.lineOfBusinessesCount() == 0;

            if (needsProvisioning) {
                InitialProvisioningController.setCurrentUser(user);
                SceneNavigator.navigateTo("InitialProvisioning-View.fxml",
                        "iSky – User Role Creations");
                return;
            }
        }

        // All other roles (or master with all roles provisioned) → Main Portal
        MainPortalController.setCurrentUser(user);
        SceneNavigator.navigateTo("MainPortal-view.fxml", "iSky Portal");
    }

    @FXML
    private void handleExit() {
        txtUsername.getScene().getWindow().hide();
    }
}