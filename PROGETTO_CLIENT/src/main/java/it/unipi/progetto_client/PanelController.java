package it.unipi.progetto_client;

import it.unipi.progetto_client.otherClasses.Utente;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.progetto_client.otherClasses.DynamicMessage;
import it.unipi.progetto_client.otherClasses.ProjectUtilities;
import static it.unipi.progetto_client.otherClasses.ProjectUtilities.postResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PanelController {

    private static final Logger log = LogManager.getLogger(PanelController.class);
    private File selectedFile;
    @FXML
    ChoiceBox dipendente;
    @FXML
    ChoiceBox veicolo;
    @FXML
    ChoiceBox partenza;
    @FXML
    ChoiceBox arrivo;
    @FXML
    ChoiceBox targetTable;
    @FXML
    TextField value;
    @FXML
    DatePicker data;
    @FXML
    Label res;

    @FXML
    private void goBack() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void initialize() {   // ci aspettiamo in ingresso tre DynamicMessage: uno che contenga tutti i possibili dipendenti, uno i veicoli e uno per le sedilogistiche
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Utente u = Utente.getIstance();
                    String args[] = {u.getToken()}; // inviamo il token per confermare chi siamo
                    DynamicMessage message = new DynamicMessage(args);
                    HttpResponse<String> response = postResponse("users/" + Utente.getIstance().getUsername() + "/possibilities", message);
                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        Type listType = new TypeToken<ArrayList<DynamicMessage>>() {
                        }.getType();
                        ArrayList<DynamicMessage> result = new Gson().fromJson(response.body(), listType);
                        final String[] resDip = result.remove(0).getStrings();
                        Platform.runLater(() -> {
                            dipendente.getItems().addAll(resDip);
                            dipendente.setValue(resDip[0]);
                            final String[] resVecs = result.remove(0).getStrings();
                            veicolo.getItems().addAll(resVecs);
                            veicolo.setValue(resVecs[0]);
                            final String[] resSed = result.remove(0).getStrings();
                            partenza.getItems().addAll(resSed);
                            partenza.setValue(resSed[0]);
                            arrivo.getItems().addAll(resSed);
                            arrivo.setValue(resSed[0]);
                        });
                    }
                } catch (Exception e) {
                    log.error("Errore di inizializzazione");
                    ProjectUtilities.setMessage(res, "Errore imprevisto: " + e.getMessage(), 'r');
                }
                return null;
            }
        };
        new Thread(task).start();

        String[] fields = {"veicolo", "sedelogistica"};
        targetTable.getItems().addAll(fields);
        targetTable.setValue(fields[0]);
    }

    /*  Le seguenti due funzioni sono state prese da Stackoverflow
    https://stackoverflow.com/questions/32534113/javafx-drag-and-drop-a-file-into-a-program
    Consentono all'utente di includere un file coem documento di carico
     */
    @FXML
    void handleFileOverEvent(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    @FXML
    void handleFileDroppedEvent(DragEvent event) {
        Dragboard db = event.getDragboard();
        File file = db.getFiles().get(0);
        selectedFile = file;
        ProjectUtilities.setMessage(res, "Selezionato: " + selectedFile, 'b');
    }

    @FXML
    public void submitViaggio() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Utente u = Utente.getIstance();
                    String content = null;
                    if (selectedFile != null) {
                        content = Files.readString(selectedFile.toPath());
                    }
                    String dataString = null;
                    if (data.getValue() != null) {
                        dataString = data.getValue().toString();
                    }
                    String args[] = {u.getToken(), "viaggio", (String) dipendente.getValue(), (String) veicolo.getValue(), dataString, (String) partenza.getValue(), (String) arrivo.getValue(), content};
                    DynamicMessage message = new DynamicMessage(args);  //(token, tabella, dipendente, veicolo, data, partenza, arrivo, data)
                    if (message.hasEmptyValue(8)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }
                    HttpResponse<String> response = postResponse("users/" + Utente.getIstance().getUsername() + "/insert", message);
                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        ProjectUtilities.setMessage(res, "Viaggio inserito", 'g');
                    }
                } catch (Exception e) {
                    log.error("Errore durante l'inserimento del viaggio");
                    ProjectUtilities.setMessage(res, "Errore imprevisto: " + e.getMessage(), 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void submitTabella() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Utente u = Utente.getIstance();
                    String args[] = {u.getToken(), (String) targetTable.getValue(), value.getText()};
                    DynamicMessage message = new DynamicMessage(args);  //(token, tabella, nome)
                    if (message.hasEmptyValue(3)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }
                    HttpResponse<String> response = postResponse("users/" + Utente.getIstance().getUsername() + "/insert", message);
                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        ProjectUtilities.setMessage(res, "Inserimento effettuato", 'g');
                        // inseriamo nel rispettivo choicebox
                        if (targetTable.getValue().equals("veicolo")) {
                            Platform.runLater(() -> {
                                veicolo.getItems().add(value.getText());
                                veicolo.setValue(veicolo.getItems().get(0));
                            });
                        } else {    // se non è veicolo è sedelogistica
                            Platform.runLater(() -> {
                                partenza.getItems().add(value.getText());
                                partenza.setValue(partenza.getItems().get(0));
                                arrivo.getItems().add(value.getText());
                                arrivo.setValue(arrivo.getItems().get(0));
                            });
                        }
                    }
                } catch (Exception e) {
                    log.error("Errore durante l'inserimento del valore");
                    ProjectUtilities.setMessage(res, "Errore imprevisto: " + e.getMessage(), 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
