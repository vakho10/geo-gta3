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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class MainController implements Initializable {

    private final ConfigPreferences configPreferences = new ConfigPreferences();

    public TextField inputFileTextField;
    public ListView outputFilesListView;
    public StackPane loadingOverlayPane;
    public Button btnGenerateGXT;
    public Button btnRemoveOutputFile;

    private FileChooser inputFileChooser;
    private FileChooser outputFileChooser;

    private File inputFile;
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

        // Load previous configuration information
        loadPreviousInputFileFromConfig();
        loadPreviousOutputFilesFromConfig();
    }

    public void onChooseInputFileClick(ActionEvent actionEvent) {
        File inputFile;
        try {
            inputFile = inputFileChooser.showOpenDialog(stage);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("must be a valid folder")) {
                inputFileChooser.setInitialDirectory(null);
                inputFile = inputFileChooser.showOpenDialog(stage);
            } else {
                throw e;
            }
        }
        setInputFile(inputFile);
    }

    private void setInputFile(File inputFile) {
        if (inputFile == null) {
            return;
        }
        this.inputFile = inputFile;
        inputFileTextField.setText(inputFile.getAbsolutePath());
        configPreferences.setInputFile(inputFile);
        updateInitialInputFileDirectory();
    }

    public void onAddOutputFileClick(ActionEvent actionEvent) {
        File outputFile = outputFileChooser.showOpenDialog(stage);
        addOutputFile(outputFile);
    }

    private void addOutputFile(File outputFile) {
        if (outputFile == null) {
            return;
        }
        if (!outputFilesList.contains(outputFile)) {
            outputFilesList.add(outputFile);
            configPreferences.setOutputFiles(outputFilesList);
            updateInitialOutputFilesDirectory();
        }
    }

    public void onRemoveOutputFileClick(ActionEvent actionEvent) {
        List<File> selectedItems = outputFilesListView.getSelectionModel().getSelectedItems();
        removeOutputFile(selectedItems);
    }

    private void loadPreviousInputFileFromConfig() {
        setInputFile(configPreferences.getInputFile());
        updateInitialInputFileDirectory();
    }

    private void updateInitialInputFileDirectory() {
        inputFileChooser.setInitialDirectory(null);
        if (inputFile != null && inputFile.getParentFile() != null) {
            inputFileChooser.setInitialDirectory(inputFile.getParentFile());
        }
    }

    private void loadPreviousOutputFilesFromConfig() {
        configPreferences.getOutputFiles().forEach(this::addOutputFile);
        updateInitialOutputFilesDirectory();
    }

    private void updateInitialOutputFilesDirectory() {
        if (!outputFilesList.isEmpty()) {
            File lastFile = outputFilesList.get(outputFilesList.size() - 1);
            if (lastFile != null && lastFile.getParentFile() != null) {
                outputFileChooser.setInitialDirectory(lastFile.getParentFile());
                return;
            }
        }
        outputFileChooser.setInitialDirectory(null);
    }

    private void removeOutputFile(List<File> selectedItems) {
        outputFilesList.removeAll(selectedItems);
        configPreferences.setOutputFiles(outputFilesList);
        updateInitialOutputFilesDirectory();
    }

    public void onCompileGXTClick(ActionEvent actionEvent) {
        executor.submit(() -> {
            Platform.runLater(() -> loadingOverlayPane.setVisible(true));
            try {
                Map<String, String> inputMap = new LinkedHashMap<>();
                try (
                        FileInputStream fis = new FileInputStream(inputFile);
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
