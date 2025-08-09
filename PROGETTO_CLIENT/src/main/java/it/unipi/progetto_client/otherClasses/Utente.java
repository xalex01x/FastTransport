package it.unipi.progetto_client.otherClasses;

import java.net.URLEncoder;

public class Utente {

    private static Utente istance;
    private static String sessionToken;
    private static String ruolo;
    private static String username;

    public static void setFields(String t, String r, String u) {
        sessionToken = t;
        ruolo = r;
        username = u;
    }

    public static String getToken() {
        return sessionToken;
    }

    public static String getRuolo() {
        return ruolo;
    }

    public static String getUsername() throws Exception {    // viene chiamata solo per generare l'indirizzo di connessione, tantovale codificarlo
        return URLEncoder.encode(username, "UTF-8");
    }

    private Utente() {
    }

    public static Utente getIstance() {
        if (istance == null) {
            istance = new Utente();
        }
        return istance;
    }
}
