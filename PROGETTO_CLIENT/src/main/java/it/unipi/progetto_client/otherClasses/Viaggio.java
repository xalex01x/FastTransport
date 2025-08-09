package it.unipi.progetto_client.otherClasses;

import java.time.LocalDate;

public class Viaggio {

    private final String dipendente;
    private final String veicolo;
    private final LocalDate data;
    private final String partenza;
    private final String arrivo;

    public String getDipendente() {
        return dipendente;
    }

    public String getVeicolo() {
        return veicolo;
    }

    public LocalDate getData() {
        return data;
    }

    public String getPartenza() {
        return partenza;
    }

    public String getArrivo() {
        return arrivo;
    }

    public Viaggio(String[] args) {
        this.dipendente = args[0];
        this.veicolo = args[1];
        this.data = LocalDate.parse(args[2]);
        this.partenza = args[3];
        this.arrivo = args[4];
    }

    public String toString() {
        return dipendente + " " + veicolo + " " + partenza + " " + arrivo;
    }
}
