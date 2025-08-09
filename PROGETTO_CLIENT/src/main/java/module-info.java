module it.unipi.progetto_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires org.apache.logging.log4j;

    opens it.unipi.progetto_client to javafx.fxml;
    opens it.unipi.progetto_client.otherClasses to com.google.gson, javafx.fxml, javafx.base;
    exports it.unipi.progetto_client;
}
