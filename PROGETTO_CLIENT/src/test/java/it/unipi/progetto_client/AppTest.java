package it.unipi.progetto_client;

import it.unipi.progetto_client.otherClasses.DynamicMessage;
import static it.unipi.progetto_client.otherClasses.ProjectUtilities.postResponse;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppTest {

    public AppTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /*
    
    TEST DI PROGETTO
    il test consiste nell'accedere al server come "marco rossi" per tentare di eseguire operazioni livello "admin"
    il livello di accesso di questo utente è "worker", quindi di norma non gli sarebbe consentito
    il test ha esito positivo se e solo se viene impedito a marco rossi di accedere a tutte le informazioni del database
    
     */
    String username;
    String password = "fIzMhsEWVK8ClFfZD92dATzm+wEe6P2xN0gyJozI2Wc=";
    String ruolo = "admin"; // l'utente si crede admin

    @Test
    void accessTest() throws Exception {
        username = URLEncoder.encode("marco rossi", "UTF-8");
        String name = "marco rossi";
        String[] args = {name, password};
        DynamicMessage message = new DynamicMessage(args);

        HttpResponse<String> response = postResponse("login", message);

        if (response.statusCode() != 200) {
            fail("Errore login: " + response.body());
        }

        String token = DynamicMessage.fromGson(response.body()).getStrings()[1];    // useremo questo token da qui in poi

        // proviamo ad ottenere tutti i dati del database
        args = new String[]{token};
        message = new DynamicMessage(args);

        response = postResponse("users/" + username + "/possibilities", message);

        if (response.statusCode() == 200) {
            System.out.println(response.body());
            fail("Test fallito: i dati del database sono stati esposti");
        }

        System.out.println("IL TEST HA AVUTO ESITO POSITIVO: IL CONTROLLO SUGLI ACCESSI FUNZIONA CORRETTAMENTE (" + response.body() + ")");

    }
}
