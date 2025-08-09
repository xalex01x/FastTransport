package it.unipi.PROGETTO_SERVER.otherClasses;

import com.google.gson.Gson;

/*
Si tratta di una classe utilizzata per lo scambio di messaggi tra client e server.
Contiene degli array dove memorizzare tipi di dati diversi
 */
public class DynamicMessage {

    private String strings[];

    public DynamicMessage(String[] strings) {
        this.strings = strings;
    }

    public DynamicMessage() {
    }

    ;
    
    public static DynamicMessage fromGson(String s) {
        return (new Gson()).fromJson(s, DynamicMessage.class);
    }

    public String toGson() {
        return (new Gson()).toJson(this);
    }

    public String[] getStrings() {
        return strings;
    }

    public boolean hasEmptyValue(int desLen) {
        if (strings.length < desLen) {
            return true;
        }
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
