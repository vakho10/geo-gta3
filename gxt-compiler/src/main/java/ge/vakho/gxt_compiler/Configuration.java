package ge.vakho.gxt_compiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path jsonFilePath = Paths.get("config.json");

    private static Configuration INSTANCE;

    private String inputFile;
    private List<String> outputFiles = new ArrayList<>();

    private Configuration() {
    }

    public static synchronized Configuration getInstance() {
        if (INSTANCE == null) {
            if (Files.exists(jsonFilePath)) {
                try (BufferedReader br = Files.newBufferedReader(jsonFilePath)) {
                    // Read from JSON configuration file
                    INSTANCE = GSON
                            .fromJson(br, Configuration.class);
                    if (INSTANCE == null) {
                        INSTANCE = new Configuration();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                INSTANCE = new Configuration();
            }
        }
        return INSTANCE;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
        saveToJsonFile();
    }

    public List<String> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(List<String> outputFiles) {
        this.outputFiles = outputFiles;
        saveToJsonFile();
    }

    private void saveToJsonFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(jsonFilePath);
             JsonWriter jw = new JsonWriter(bw)) {
            // Write to JSON configuration file
            GSON
                    .toJson(this, Configuration.class, jw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
