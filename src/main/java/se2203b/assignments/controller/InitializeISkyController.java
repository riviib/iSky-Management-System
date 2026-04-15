package se2203b.assignments.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se2203b.assignments.SceneNavigator;
import se2203b.assignments.service.UserAccountService;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Component
@Scope("prototype") // a new controller per FXML load
public class InitializeISkyController implements Initializable {

    @FXML
    private TextField txtMasterUser;
    @FXML
    private TextField txtMasterEmail;

    @FXML
    private PasswordField pwd1;
    @FXML
    private PasswordField pwd2;

    @FXML
    private TextField txtPwd1Visible;
    @FXML
    private TextField txtPwd2Visible;

    @FXML
    private CheckBox chkShowPwd1;
    @FXML
    private CheckBox chkShowPwd2;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblRuleLen;
    @FXML
    private Label lblRuleCommon;
    @FXML
    private Label lblRuleUser;
    @FXML
    private Label lblRuleChars;

    @FXML
    private Button btnCreateMaster;

    private final UserAccountService userAccountService;

    @Autowired
    public InitializeISkyController(UserAccountService service) {
        this.userAccountService = service;
    }

    private final BooleanProperty busy = new SimpleBooleanProperty(false);

    private static final int MIN_LEN = 12;

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final String[] COMMON_BAD = {
            "password", "password123", "12345678", "qwerty", "letmein", "admin", "welcome"
    };


    Scene currentScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Objects.requireNonNull(txtMasterUser);
        Objects.requireNonNull(pwd1);
        Objects.requireNonNull(pwd2);
        Objects.requireNonNull(btnCreateMaster);

        setStatus("", false);

        wireShowHidePassword(chkShowPwd1, pwd1, txtPwd1Visible);
        wireShowHidePassword(chkShowPwd2, pwd2, txtPwd2Visible); // show/hide pattern

        txtMasterUser.textProperty().addListener((obs, o, n) -> updateRulesAndValidate());
        pwd1.textProperty().addListener((obs, o, n) -> updateRulesAndValidate());
        txtPwd1Visible.textProperty().addListener((obs, o, n) -> updateRulesAndValidate());

        pwd2.textProperty().addListener((obs, o, n) -> validate(false));
        txtPwd2Visible.textProperty().addListener((obs, o, n) -> validate(false));
        if (txtMasterEmail != null) {
            txtMasterEmail.textProperty().addListener((obs, o, n) -> validate(false));
        }

        BooleanBinding invalid = Bindings.createBooleanBinding(
                () -> !isFormValid(),
                txtMasterUser.textProperty(),
                pwd1.textProperty(),
                pwd2.textProperty(),
                txtPwd1Visible.textProperty(),
                txtPwd2Visible.textProperty(),
                (txtMasterEmail == null ? txtMasterUser.textProperty() : txtMasterEmail.textProperty()),
                chkShowPwd1.selectedProperty(),
                chkShowPwd2.selectedProperty()
        );

        btnCreateMaster.disableProperty().bind(invalid.or(busy));

        updateRulesAndValidate();

    }

    @FXML
    private void handleCreateMaster() {
        if (!validate(true)) return;

        try {
            userAccountService.createMaster(
                    txtMasterUser.getText().trim(),
                    getPwd1());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Master Account Created");
            alert.setHeaderText("Master account created");
            alert.setContentText("Continue to login to sign in as the master user.");
            alert.showAndWait();

            SceneNavigator.navigateTo("Login-view.fxml", "iSky – Login");

        } catch (IllegalStateException e) {
            setStatus(e.getMessage(), true);
        }

    }

    @FXML
    private void handleExit() {
        if (btnCreateMaster.getScene() != null && btnCreateMaster.getScene().getWindow() != null) {
            btnCreateMaster.getScene().getWindow().hide();
        }
    }

    // ---------- Validation + rules ----------

    private void updateRulesAndValidate() {
        updateRuleLabels();
        validate(false);
    }

    private boolean validate(boolean showMessage) {
        boolean ok = isFormValid();
        if (ok) {
            setStatus("", false);
            return true;
        }
        if (showMessage) {
            setStatus(buildValidationMessage(), true);
        }
        return false;
    }

    private boolean isFormValid() {
        String user = nte(txtMasterUser.getText()).trim();
        if (user.isEmpty()) return false;

        String email = txtMasterEmail == null ? "" : nte(txtMasterEmail.getText()).trim();
        if (!email.isEmpty() && !EMAIL_RX.matcher(email).matches()) return false;

        String p1 = getPwd1();
        String p2 = getPwd2();

        if (p1.isEmpty() || p2.isEmpty()) return false;
        if (!p1.equals(p2)) return false;

        if (p1.length() < MIN_LEN) return false;
        if (p1.equalsIgnoreCase(user)) return false;
        if (isCommonPassword(p1)) return false;

        return true;
    }

    private void updateRuleLabels() {
        String user = nte(txtMasterUser.getText()).trim();
        String p1 = getPwd1();

        boolean lenOk = p1.length() >= MIN_LEN;
        boolean commonOk = !p1.isEmpty() && !isCommonPassword(p1);
        boolean userOk = !p1.isEmpty() && !user.isEmpty() && !p1.equalsIgnoreCase(user);

        setRule(lblRuleLen, "• At least " + MIN_LEN + " characters", lenOk);
        setRule(lblRuleCommon, "• Not a common/weak password", commonOk);
        setRule(lblRuleUser, "• Not the same as the username", userOk);
        setRule(lblRuleChars, "• Spaces and special characters allowed", true);
    }

    private void setRule(Label lbl, String text, boolean ok) {
        if (lbl == null) return;
        lbl.setText(text);
        lbl.setTextFill(ok ? Color.web("#1b5e20") : Color.web("#b00020"));
    }

    private String buildValidationMessage() {
        String user = nte(txtMasterUser.getText()).trim();
        if (user.isEmpty()) return "Please enter a master username.";

        String email = txtMasterEmail == null ? "" : nte(txtMasterEmail.getText()).trim();
        if (!email.isEmpty() && !EMAIL_RX.matcher(email).matches()) {
            return "Please enter a valid email address (or leave it blank).";
        }

        String p1 = getPwd1();
        String p2 = getPwd2();

        if (p1.isEmpty() || p2.isEmpty()) return "Please enter and confirm the password.";
        if (!p1.equals(p2)) return "Passwords do not match.";
        if (p1.length() < MIN_LEN) return "Password does not meet the minimum length.";
        if (p1.equalsIgnoreCase(user)) return "Password must not match the username.";
        if (isCommonPassword(p1)) return "Password is too common. Choose a stronger one.";
        return "Please fix the highlighted fields.";
    }

    private void setStatus(String msg, boolean error) {
        if (lblStatus == null) return;
        lblStatus.setText(msg == null ? "" : msg);
        lblStatus.setTextFill(error ? Color.web("#b00020") : Color.web("#1b5e20"));
    }

    private static boolean isCommonPassword(String p) {
        String x = nte(p).trim().toLowerCase();
        for (String bad : COMMON_BAD) {
            if (x.equals(bad)) return true;
        }
        return false;
    }

    private static String nte(String s) {
        return s == null ? "" : s;
    }

    // ---------- Show/hide password ----------

    private void wireShowHidePassword(CheckBox chk, PasswordField pf, TextField tf) {
        if (chk == null || pf == null || tf == null) return;

        tf.textProperty().bindBidirectional(pf.textProperty());

        chk.selectedProperty().addListener((obs, oldV, show) -> {
            tf.setVisible(show);
            tf.setManaged(show);

            pf.setVisible(!show);
            pf.setManaged(!show);

            if (show) {
                tf.requestFocus();
                tf.positionCaret(tf.getText().length());
            } else {
                pf.requestFocus();
                pf.positionCaret(pf.getText().length());
            }
        });

        boolean show = chk.isSelected();
        tf.setVisible(show);
        tf.setManaged(show);
        pf.setVisible(!show);
        pf.setManaged(!show);
    }

    private String getPwd1() {
        return (txtPwd1Visible != null && txtPwd1Visible.isVisible())
                ? nte(txtPwd1Visible.getText())
                : nte(pwd1.getText());
    }

    private String getPwd2() {
        return (txtPwd2Visible != null && txtPwd2Visible.isVisible())
                ? nte(txtPwd2Visible.getText())
                : nte(pwd2.getText());
    }

    private void setPwd1(String v) {
        pwd1.setText(v);
        if (txtPwd1Visible != null) txtPwd1Visible.setText(v);
    }

    private void setPwd2(String v) {
        pwd2.setText(v);
        if (txtPwd2Visible != null) txtPwd2Visible.setText(v);
    }

}
