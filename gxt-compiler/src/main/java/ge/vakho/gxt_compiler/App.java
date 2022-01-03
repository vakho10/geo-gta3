package ge.vakho.gxt_compiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent parent;
        try (InputStream inputStream = App.class.getResourceAsStream("/views/main.fxml")) {
            parent = fxmlLoader.load(inputStream);
        }
        Scene scene = new Scene(parent);
        MainController mainController = fxmlLoader.getController();
        mainController.setStage(stage); // Pass stage to controller
        mainController.setExecutor(executor);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("GXT Compiler for GEO GTA III");
        try (InputStream is = App.class.getResourceAsStream("/images/gta3.png")) {
            stage.getIcons().add(new Image(is));
        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
