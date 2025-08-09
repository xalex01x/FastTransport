package it.unipi.progetto_client;

import it.unipi.progetto_client.otherClasses.Utente;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.progetto_client.otherClasses.DynamicMessage;
import it.unipi.progetto_client.otherClasses.ProjectUtilities;
import static it.unipi.progetto_client.otherClasses.ProjectUtilities.postResponse;
import it.unipi.progetto_client.otherClasses.Viaggio;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecondaryController {

    private static final Logger log = LogManager.getLogger(SecondaryController.class);
    private int[] dayArray = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private ZonedDateTime currentDate;
    private ZonedDateTime today;
    @FXML
    Pane calendario;
    @FXML
    Label dateString;
    @FXML
    Pane adminContainer;
    @FXML
    Button adminButton;
    @FXML
    Label res;
    @FXML
    ContextMenu menu;
    @FXML
    TableView<Viaggio> tabella = new TableView<>();
    private ObservableList<Viaggio> ol;
    private ArrayList<Viaggio> viaggi;

    @FXML
    private void switchToAdmin() throws IOException {
        App.setRoot("panel");
    }

    @FXML
    private void colorDays() {
        for (Node n : calendario.getChildren()) {
            Label l = (Label) n;
            for (Viaggio v : viaggi) {
                if (!l.getText().isEmpty() && Integer.parseInt(l.getText()) == v.getData().getDayOfMonth()) {
                    l.setStyle("-fx-text-fill: white; -fx-border-color: black; -fx-alignment: center; -fx-background-color: #3498db;");
                    break;
                }
            }
        }
    }

    @FXML
    private void drawCalendar() {
        int CELLWIDTH = 50;
        int CELLHEIGTH = 50;
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int daysNum;
        if (month == 2 && (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) {
            daysNum = 29;
        } else {
            daysNum = dayArray[month - 1];
        }
        int currentDay = 1;
        int columnOffset = ZonedDateTime.of(currentDate.getYear(), currentDate.getMonthValue(), 1, 0, 0, 0, 0, currentDate.getZone()).getDayOfWeek().getValue();
        if (columnOffset == 7) {
            columnOffset = 0;
            currentDay = 2;
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (i == 0 && j < columnOffset - 1) {
                    continue;
                }
                if (currentDay > daysNum) {
                    break;
                }
                ZonedDateTime dayDate = ZonedDateTime.of(currentDate.getYear(), currentDate.getMonthValue(), currentDay, 0, 0, 0, 0, currentDate.getZone());
                if (dayDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    currentDay++;
                }
                if (dayDate.toLocalDate().equals(today.toLocalDate()) == true) {
                    Label now = new Label();
                    now.setPrefWidth(CELLWIDTH - 2);
                    now.setPrefHeight(CELLHEIGTH - 2);
                    now.setStyle("-fx-background-color: #e74c3c;");
                    calendario.getChildren().add(now);
                    now.setTranslateX(CELLWIDTH * j + 1);
                    now.setTranslateY(CELLHEIGTH * i);
                }
                Label cell = new Label();
                cell.setPrefWidth(CELLWIDTH - 2);
                cell.setPrefHeight(CELLHEIGTH - 2);
                cell.setStyle("-fx-border-color: black;");
                cell.setAlignment(Pos.CENTER);
                calendario.getChildren().add(cell);
                cell.setTranslateX(CELLWIDTH * j + 1);
                cell.setTranslateY(CELLHEIGTH * i);
                cell.setText(String.valueOf(currentDay));
                final int value = currentDay;
                cell.setOnMouseClicked(event -> handleClick(value, month, year, cell));
                currentDay++;
            }
        }
    }

    @FXML
    Label oldCell;

    @FXML
    private void handleClick(int day, int month, int year, Label cell) {
        if (oldCell == cell) {
            return;
        }
        log.info("L'utente ha cliccato su " + day + "-" + month + "-" + year);
        if (oldCell != null) {
            oldCell.setStyle("-fx-border-color: black; -fx-alignment: center;");
            for (Viaggio v : viaggi) {
                if (Integer.parseInt(oldCell.getText()) == v.getData().getDayOfMonth()) {
                    oldCell.setStyle("-fx-text-fill: white; -fx-border-color: black; -fx-alignment: center; -fx-background-color: #3498db;");
                    break;
                }
            }
        }
        cell.setStyle("-fx-background-color: orange;  -fx-font-weight: bold; -fx-text-fill: white; -fx-border-color: black; -fx-alignment: center;");
        oldCell = cell;
        ol.clear();
        for (Viaggio v : viaggi) {
            if (v.getData().getDayOfMonth() == day) {
                ol.add(v);
            }
        }
    }

    @FXML
    private void subMonth() {
        currentDate = currentDate.minusMonths(1);
        dateString.setText("" + currentDate.getMonthValue() + "/" + currentDate.getYear());
        log.info("mm/yyyy: " + currentDate.getMonthValue() + "-" + currentDate.getYear());
        calendario.getChildren().clear();
        drawCalendar();
        getViaggi();
    }

    @FXML
    private void addMonth() {
        currentDate = currentDate.plusMonths(1);
        dateString.setText("" + currentDate.getMonthValue() + "/" + currentDate.getYear());
        log.info("mm/yyyy: " + currentDate.getMonthValue() + "-" + currentDate.getYear());
        calendario.getChildren().clear();
        drawCalendar();
        getViaggi();
    }

    @FXML
    private void getViaggi() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Utente u = Utente.getIstance();
                    String[] args = {u.getToken(), "" + currentDate.getMonthValue(), "" + currentDate.getYear()};
                    DynamicMessage message = new DynamicMessage(args);  //(token, month, year)
                    if (message.hasEmptyValue(3)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }
                    HttpResponse<String> response = ProjectUtilities.postResponse("users/" + Utente.getIstance().getUsername() + "/view", message);
                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        Type listType = new TypeToken<ArrayList<DynamicMessage>>() {
                        }.getType();
                        ArrayList<DynamicMessage> result = new Gson().fromJson(response.body(), listType);
                        viaggi.clear();
                        while (result.size() != 0) {
                            viaggi.add(new Viaggio(result.remove(0).getStrings()));
                        }
                        Platform.runLater(() -> colorDays());
                    }
                } catch (Exception e) {
                    log.error("Errore durante il fetch dei viaggi: " + e.getMessage());
                    ProjectUtilities.setMessage(res, "Errore imprevisto", 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void removeEntry() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Utente u = Utente.getIstance();
                    Viaggio v = tabella.getSelectionModel().getSelectedItem();
                    String args[] = {u.getToken(), (String) v.getDipendente(), (String) v.getVeicolo(), v.getData().toString(), (String) v.getPartenza(), (String) v.getArrivo()};
                    DynamicMessage message = new DynamicMessage(args); //(toke, usr, veicolo, data, part, arr)
                    if (message.hasEmptyValue(6)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }
                    HttpResponse<String> response = postResponse("users/" + Utente.getIstance().getUsername() + "/remove", message);
                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        ol.remove(v);
                        int index = 0;
                        for (Viaggio iter : viaggi) {
                            if (iter.getDipendente().equals(args[1])
                                    && iter.getVeicolo().equals(args[2])
                                    && iter.getData() == v.getData()
                                    && iter.getPartenza().equals(args[4])
                                    && iter.getArrivo().equals(args[5])) {
                                viaggi.remove(index);
                                break;
                            }
                            index++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Errore duerante la cancellazione del viaggio: " + e.getMessage());
                    ProjectUtilities.setMessage(res, "Errore imprevisto: " + e.getMessage(), 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void scaricaDocumento() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Utente u = Utente.getIstance();
                    Viaggio v = tabella.getSelectionModel().getSelectedItem();
                    String args[] = {u.getToken(), (String) v.getDipendente(), (String) v.getVeicolo(), v.getData().toString(), (String) v.getPartenza(), (String) v.getArrivo()};
                    DynamicMessage message = new DynamicMessage(args);  //(token, usr, veicolo, data, partenza, arrivo)
                    if (message.hasEmptyValue(6)) {
                        ProjectUtilities.setMessage(res, "Compila tutti i campi", 'r');
                        return null;
                    }

                    HttpResponse<String> response = postResponse("users/" + Utente.getIstance().getUsername() + "/download", message);

                    if (response.statusCode() != 200) {
                        ProjectUtilities.setMessage(res, response.body(), 'r');
                    } else {
                        String carico = (new Gson()).fromJson(response.body(), String.class);
                        String userHome = System.getProperty("user.home");
                        String nome = args[1];
                        for (int i = 2; i < args.length; i++) {
                            nome += ("_" + args[i]);
                        }
                        String DIR = "/src/main/resources/documentiDicarico/" + nome + ".json";
                        String PATH = new File("").getAbsolutePath() + DIR;
                        try ( FileWriter writer = new FileWriter(PATH)) {
                            writer.write(new String(carico));
                            ProjectUtilities.setMessage(res, "File JSON salvato con successo!", 'b');
                        } catch (IOException e) {
                            log.error("Impossibile salvare il file:" + e.getMessage());
                            ProjectUtilities.setMessage(res, "Errore nel salvataggio del file", 'r');
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    ProjectUtilities.setMessage(res, "Errore imprevisto: " + e.getMessage(), 'r');
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void initialize() {
        today = ZonedDateTime.now();
        currentDate = today;
        dateString.setText("" + currentDate.getMonthValue() + "/" + currentDate.getYear());
        viaggi = new ArrayList<>();

        TableColumn dipCol = new TableColumn("Dipendente");
        dipCol.setCellValueFactory(new PropertyValueFactory<>("dipendente"));
        dipCol.setPrefWidth(100);
        TableColumn veiCol = new TableColumn("Veicolo");
        veiCol.setCellValueFactory(new PropertyValueFactory<>("veicolo"));
        veiCol.setPrefWidth(100);
        /*
        TableColumn datCol = new TableColumn("Data");
        datCol.setCellValueFactory(new PropertyValueFactory<>("data"));
         */
        TableColumn parCol = new TableColumn("Partenza");
        parCol.setCellValueFactory(new PropertyValueFactory<>("partenza"));
        parCol.setPrefWidth(100);
        TableColumn arrCol = new TableColumn("Arrivo");
        arrCol.setCellValueFactory(new PropertyValueFactory<>("arrivo"));
        arrCol.setPrefWidth(100);
        tabella.getColumns().addAll(dipCol, veiCol, /*datCol,*/ parCol, arrCol);
        ol = FXCollections.observableArrayList();
        tabella.setItems(ol);

        if (Utente.getIstance().getRuolo().equals("admin")) { // aggiunte alla GUI nel caso in cui l'utente sia un admin

        } else {    // rimozioni dalla GUI nel caso non lo sia
            adminContainer.getChildren().remove(adminButton);
            menu.getItems().remove(0);
        }

        drawCalendar();
        getViaggi();
    }
}
