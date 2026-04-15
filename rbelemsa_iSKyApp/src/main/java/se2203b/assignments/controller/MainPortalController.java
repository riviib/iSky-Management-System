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
public class MainPortalController implements Initializable {

    @FXML private MenuBar menuBar;
    @FXML private Menu menuUser;
    @FXML private Label lblWelcome;
    @FXML private Label lblRole;

    // MISSING DECLARATIONS ADDED BELOW
    // Business Manager Menus
    @FXML private Menu menuAnalyze;
    @FXML private Menu menuFreqFlier;
    @FXML private Menu menuFlight;
    @FXML private Menu menuUsersBM;

    // Network Admin Menus
    @FXML private Menu menuUserAccount;
    @FXML private Menu menuWeb;
    @FXML private Menu menuDB;
    @FXML private Menu menuNetUsage;

    // LOB Executive Menus
    @FXML private Menu menuRes;
    @FXML private Menu menuFStatus;
    @FXML private Menu menuTStatus;
    @FXML private Menu menuConfirm;

    private final UserAccountService userAccountService;
    private static UserAccount currentUser;

    public static void setCurrentUser(UserAccount user) { currentUser = user; }

    @Autowired
    public MainPortalController(UserAccountService service) {
        this.userAccountService = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentUser == null) {
            SceneNavigator.navigateTo("Login-view.fxml", "iSky – Login");
            return;
        }

        Role role = currentUser.getRole();

        // 1. Set the Dynamic Labels (No hard-coding!)
        lblWelcome.setText("Welcome, " + currentUser.getUsername());
        lblRole.setText("Role: " + role.toString());
        menuUser.setText("👤 " + currentUser.getUsername());

        // 2. Logic: Show menus based on role
        // BUSINESS MANAGER
        boolean isBM = (role == Role.BUSINESS_MANAGER);
        menuAnalyze.setVisible(isBM);
        menuFreqFlier.setVisible(isBM);
        menuFlight.setVisible(isBM);
        menuUsersBM.setVisible(isBM);

        // NETWORK ADMIN
        boolean isNA = (role == Role.NETWORK_ADMINISTRATOR);
        menuUserAccount.setVisible(isNA);
        menuWeb.setVisible(isNA);
        menuDB.setVisible(isNA);
        menuNetUsage.setVisible(isNA);

        // LOB EXECUTIVE
        boolean isLOB = (role == Role.LINE_OF_BUSINESS_EXECUTIVE);
        menuRes.setVisible(isLOB);
        menuFStatus.setVisible(isLOB);
        menuTStatus.setVisible(isLOB);
        menuConfirm.setVisible(isLOB);

        // MASTER Role (role 10) results in all booleans being false,
        // showing only File, About, and User menus.
    }

    @FXML private Label lblStatus; // Match the fx:id in FXML

    @FXML
    private void handleMenuAction() {
        Role role = currentUser.getRole();

        // Changing the line based on who clicked "Action 1"
        if (role == Role.NETWORK_ADMINISTRATOR) {
            lblStatus.setText("Network Admin Functionalities (stub).");
        } else if (role == Role.LINE_OF_BUSINESS_EXECUTIVE) {
            lblStatus.setText("LOB Exec Functionalities.");
        } else if (role == Role.BUSINESS_MANAGER) {
            lblStatus.setText("Business Manager Functionalities (stub).");
        }
    }

    @FXML
    private void handleAddUserRequest() {
        lblStatus.setText("Business Manager: Request Adding User Account.");
    }

    @FXML
    private void handleRemoveUserRequest() {
        lblStatus.setText("Business Manager: Request Removing User Account.");
    }

    @FXML
    private void handleLogout() {
        currentUser = null;
        SceneNavigator.navigateTo("Login-view.fxml", "iSky – Login");
    }

    @FXML
    private void handleChangePassword() {
        ChangePasswordController.setCurrentUser(currentUser);
        SceneNavigator.navigateTo("ChangePassword-view.fxml", "iSky – Change Password");
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About iSky");
        alert.setHeaderText("iSky Airline Management System");
        alert.setContentText("© 2026 iSky Airlines. All rights reserved.\n\nSE2203b – Software Design\nRivka Belemsaga");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        menuBar.getScene().getWindow().hide();
    }
}