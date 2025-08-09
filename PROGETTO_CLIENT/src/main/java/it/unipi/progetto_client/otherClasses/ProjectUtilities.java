package it.unipi.progetto_client.otherClasses;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectUtilities {

    /*
    Classe di utilità:
    
    racchiude due funzioni utili a più classi
    
        - setMessage: responsabile degli aoutput a video sulla GUI
    
        - postResponse: generalizza il processo di connessione verso il server
     */
    private static final Logger log = LogManager.getLogger(ProjectUtilities.class);

    @FXML
    public static void setMessage(Label res, String s, char col) {
        Paint p = null;
        switch (col) {
            case 'r':
                p = javafx.scene.paint.Color.rgb(0xe7, 0x4c, 0x3c);   //#e74c3c
                break;
            case 'g':
                p = javafx.scene.paint.Color.rgb(0x2e, 0xcc, 0x71);   //#2ecc71
                break;
            case 'b':
                p = javafx.scene.paint.Color.rgb(0x34, 0x98, 0xdb);   //#3498db
                break;
            default:
                p = javafx.scene.paint.Color.rgb(0, 0, 0);
        }
        final Paint P = p;
        Platform.runLater(() -> {
            res.setText(s);
            res.setTextFill(P);
        });
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    log.warn("Un thread che deve manipolare il testo è stato interrotto");
                }
                if (res.getText().equals(s)) {
                    Platform.runLater(() -> {
                        res.setText("");
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public static HttpResponse<String> postResponse(String path, DynamicMessage message) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/616157/" + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(message.toGson())) //(token, mese, anno)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
