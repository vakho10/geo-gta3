module gxt.compiler {
    requires com.google.gson;
    requires javafx.controls;
    requires javafx.fxml;

    opens ge.vakho.gxt_compiler to javafx.fxml, com.google.gson;
    exports ge.vakho.gxt_compiler;
}