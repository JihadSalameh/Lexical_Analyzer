module com.example.lexical_analyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lexical_analyzer to javafx.fxml;
    exports com.example.lexical_analyzer;
}