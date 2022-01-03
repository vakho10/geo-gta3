package ge.vakho.gxt_compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.prefs.Preferences;

public class ConfigPreferences {

    private static final String INPUT_FILE_PATH = "ge.vakho.gxt_compiler.inputFilePath";
    private static final String OUTPUT_FILE_PATHS = "ge.vakho.gxt_compiler.outputFilePaths";

    private final Preferences prefs;

    private File inputFile;
    private List<File> outputFiles = new ArrayList<>();

    public ConfigPreferences() {
        prefs = Preferences.userRoot().node("geo_gta3_gxt_compiler");

        final String inputFilePath = prefs.get(INPUT_FILE_PATH, null);
        if (inputFilePath != null) {
            inputFile = new File(inputFilePath);
        }

        final String outputFilePaths = prefs.get(OUTPUT_FILE_PATHS, null);
        if (outputFilePaths != null) {
            for (String outputFilePath : outputFilePaths.split(File.pathSeparator)) {
                outputFiles.add(new File(outputFilePath));
            }
        }
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        if (inputFile != null) {
            prefs.put(INPUT_FILE_PATH, inputFile.getAbsolutePath());
        } else {
            prefs.remove(INPUT_FILE_PATH);
        }
        this.inputFile = inputFile;
    }

    public List<File> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(List<File> outputFiles) {
        if (outputFiles != null) {
            StringJoiner stringJoiner = new StringJoiner(File.pathSeparator);
            outputFiles.forEach(of -> stringJoiner.add(of.getAbsolutePath()));
            prefs.put(OUTPUT_FILE_PATHS, stringJoiner.toString());
        } else {
            prefs.remove(OUTPUT_FILE_PATHS);
        }
        this.outputFiles = outputFiles;
    }
}
