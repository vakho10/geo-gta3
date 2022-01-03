package ge.vakho.gxt_compiler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ge.vakho.gxt_compiler.compiler.StringCompiler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class MainController implements Initializable {

    public TextField inputFileTextField;
    public ListView outputFilesListView;
    public StackPane loadingOverlayPane;
    public Button btnGenerateGXT;
    public Button btnRemoveOutputFile;

    private FileChooser inputFileChooser;
    private FileChooser outputFileChooser;

    private Path inputFile;
    private ObservableList<File> outputFilesList = FXCollections.observableArrayList();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private ExecutorService executor;

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        outputFilesListView.setItems(outputFilesList);

        // Input file chooser
        inputFileChooser = new FileChooser();
        inputFileChooser.setTitle("Select JSON Translation File");
        inputFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON file", "*.json"));

        // Output file chooser
        outputFileChooser = new FileChooser();
        outputFileChooser.setTitle("Select Output GXT File");
        outputFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GTA III GXT file", "*.gxt"));

        // Disable compile button if any text field is empty!
        btnGenerateGXT.disableProperty().bind(
                inputFileTextField.textProperty().isEmpty().or(Bindings.isEmpty(outputFilesList)));

        // Disable remove button if nothing is selected
        btnRemoveOutputFile.disableProperty().bind(Bindings.isEmpty(outputFilesListView.getSelectionModel().getSelectedItems()));
    }

    public void onChooseInputFileClick(ActionEvent actionEvent) {
        File inputFile = inputFileChooser.showOpenDialog(stage);
        if (inputFile != null) {
            this.inputFile = inputFile.toPath();
            inputFileTextField.setText(inputFile.getAbsolutePath());
            if (this.inputFile.toAbsolutePath().getParent() != null) {
                inputFileChooser.setInitialDirectory(this.inputFile.toAbsolutePath().getParent().toFile());
            }
        }
    }

    public void onAddOutputFileClick(ActionEvent actionEvent) {
        File outputFile = outputFileChooser.showOpenDialog(stage);
        if (outputFile != null) {
            if (!outputFilesList.contains(outputFile)) {
                outputFilesList.add(outputFile);
                if (outputFile.getParent() != null) {
                    outputFileChooser.setInitialDirectory(outputFile.getParentFile());
                }
            }
        }
    }

    public void onRemoveOutputFileClick(ActionEvent actionEvent) {
        outputFilesList.removeAll(outputFilesListView.getSelectionModel().getSelectedItems());
    }

    public void onCompileGXTClick(ActionEvent actionEvent) {
        executor.submit(() -> {
            Platform.runLater(() -> loadingOverlayPane.setVisible(true));
            try {
                Map<String, String> inputMap = new LinkedHashMap<>();
                try (
                        InputStream fis = Files.newInputStream(inputFile);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);
                ) {
                    Map<String, JsonObject> inputJsonMap = new Gson().fromJson(br, new TypeToken<Map<String, JsonObject>>() {
                    }.getType());
                    for (String key : inputJsonMap.keySet()) {
                        JsonObject jsonObject = inputJsonMap.get(key);
                        String kaValue = jsonObject.get("ka").getAsString();
                        inputMap.put(key, kaValue);
                    }
                }

                StringCompiler stringCompiler = new StringCompiler();

                // For each entry
                for (String key : inputMap.keySet()) {
                    String value = inputMap.get(key);

                    // Turn key to uppercase
                    key = key.toUpperCase();

                    if (value != null && !value.isBlank()) {
                        stringCompiler.addTextLine(key, value);
                    }
                }

                // Save gxt file
                outputFilesList.forEach(outputFile -> {
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        stringCompiler.outputIntoStream(fos);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> loadingOverlayPane.setVisible(false));
            }
        });
    }

    public void onExitApplicationClick(ActionEvent actionEvent) {
        Platform.exit();
    }
}
