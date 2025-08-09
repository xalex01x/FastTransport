package it.unipi.progetto_client;

import it.unipi.progetto_client.otherClasses.DynamicMessage;
import it.unipi.progetto_client.otherClasses.HashFunction;
import it.unipi.progetto_client.otherClasses.ProjectUtilities;
import static it.unipi.progetto_client.otherClasses.ProjectUtilities.postResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterController {

    private static final Logger log = LogManager.getLogger(RegisterController.class);
    @FXML
    TextField user;
    @FXML
    PasswordField pass;
    @FXML
    ChoiceBox<String> role;
    @FXML
    Label res;

    @FXML
    public void initialize() {
        String[] fields = {"worker", "admin"};
        role.getItems().addAll(fields);
        role.setValue(fields[0]);
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void registrati() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                if (pass.getText().isEmpty()) {
                    ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                    return null;
                }
                try {
                    String[] strs = {user.getText(), HashFunction.hash(pass.getText()), role.getValue()};
                    DynamicMessage message = new DynamicMessage(strs);
                    if (message.hasEmptyValue(3)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }

                    HttpResponse<String> response = postResponse("register", message);

                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        ProjectUtilities.setMessage(res, "Registrazione completata con successo!", 'g');
                    }
                } catch (Exception e) {
                    log.error("Errore durante la registrazione: " + e.getMessage());
                    ProjectUtilities.setMessage(res, "Errore imprevisto", 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
