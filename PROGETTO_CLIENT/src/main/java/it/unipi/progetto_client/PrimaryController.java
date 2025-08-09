package it.unipi.progetto_client;

import it.unipi.progetto_client.otherClasses.Utente;
import it.unipi.progetto_client.otherClasses.DynamicMessage;
import it.unipi.progetto_client.otherClasses.HashFunction;
import it.unipi.progetto_client.otherClasses.ProjectUtilities;
import static it.unipi.progetto_client.otherClasses.ProjectUtilities.postResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrimaryController {

    private static final Logger log = LogManager.getLogger(PrimaryController.class);
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    Label res;
    @FXML
    ImageView image;

    @FXML
    private void switchToHomePage() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                if (password.getText().isEmpty()) {
                    ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                    return null;
                }
                try {
                    String[] strs = {username.getText(), HashFunction.hash(password.getText())};
                    DynamicMessage message = new DynamicMessage(strs);
                    if (message.hasEmptyValue(2)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }

                    HttpResponse<String> response = postResponse("login", message);

                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                        return null;
                    } else {
                        String[] dm = DynamicMessage.fromGson(response.body()).getStrings();
                        Utente.getIstance().setFields(dm[1], dm[0], username.getText());
                        try {
                            App.setRoot("secondary");
                        } catch (IOException e) {
                            log.error("Impossibile cambiare finestra");
                        }
                    }
                } catch (Exception e) {
                    log.error("Errore durante il login");
                    ProjectUtilities.setMessage(res, "Errore imprevisto: " + e.getMessage(), 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void switchToRegister() throws IOException {
        App.setRoot("register");
    }

    @FXML
    public void initialize() {
        image.setImage(new Image(getClass().getResource("fastTransport.png").toExternalForm()));
    }
}
