package se2203b.assignments;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import se2203b.assignments.service.UserAccountService;

public class iSkyApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private static Stage primaryStage;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(JavaFXSpringApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.getIcons().add(new Image(
                "file:src/main/resources/se2203b/assignments/images/WesternLogo.png"));

        UserAccountService userAccountService = springContext.getBean(UserAccountService.class);

        String fxml;
        String title;

        if (!userAccountService.isInitialized()) {
            fxml  = "/se2203b/assignments/views/InitializeISky-view.fxml";
            title = "Initialize iSky (one-time)";
        } else {
            fxml  = "/se2203b/assignments/views/Login-view.fxml";
            title = "iSky – Login";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}