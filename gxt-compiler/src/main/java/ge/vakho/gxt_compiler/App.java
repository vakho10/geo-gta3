package ge.vakho.gxt_compiler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class App {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("You should pass three arguments: [input JSON file] [output GXT file]");
            return;
        }

        Path inputFile = Paths.get(args[0]);
        Path outputFile = Paths.get(args[1]);

        if (Files.notExists(inputFile)) {
            System.err.println("Input JSON file doesn't exist!");
            return;
        }

        if (Files.isDirectory(outputFile)) {
            System.err.println("Output file must not be a directory!");
            return;
        }

        // Parse input file JSON as map and convert GEO characters to RUS
        LinkedTreeMap<String, String> inputMap = new LinkedTreeMap<>();
        try (
                InputStream fis = Files.newInputStream(inputFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
        ) {
            LinkedTreeMap<String, JsonObject> inputJsonMap = new Gson().fromJson(br, new TypeToken<Map<String, JsonObject>>() {
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
        try (OutputStream os = Files.newOutputStream(outputFile)) {
            stringCompiler.outputIntoStream(os);
        }
    }
}
