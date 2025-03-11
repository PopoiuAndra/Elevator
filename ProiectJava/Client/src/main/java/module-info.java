module org.example.clientel {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;

    opens org.example.clientel to javafx.fxml;
    exports org.example.clientel;
}