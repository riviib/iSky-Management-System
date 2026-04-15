package se2203b.assignments;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {

    private static final String BASE = "/se2203b/assignments/views/";

    public static void navigateTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneNavigator.class.getResource(BASE + fxmlFile));
            loader.setControllerFactory(iSkyApplication.getSpringContext()::getBean);
            Parent root = loader.load();
            Stage stage = iSkyApplication.getPrimaryStage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}