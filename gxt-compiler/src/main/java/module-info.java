module gxt.compiler {
    requires com.google.gson;
    requires javafx.controls;
    requires javafx.fxml;

    opens ge.vakho.gxt_compiler to javafx.fxml;
    exports ge.vakho.gxt_compiler;
}