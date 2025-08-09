package it.unipi.PROGETTO_SERVER.TabelleDB;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class ViaggioId implements Serializable {

    @Column(name = "dipendente", length = 45)
    private String dipendente;

    @Column(name = "veicolo", length = 45)
    private String veicolo;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "partenza", length = 45)
    private String partenza;

    @Column(name = "arrivo", length = 45)
    private String arrivo;

    public ViaggioId() {
    }

    public ViaggioId(String dipendente, String veicolo, LocalDate data, String partenza, String arrivo) {
        this.dipendente = dipendente;
        this.veicolo = veicolo;
        this.data = data;
        this.partenza = partenza;
        this.arrivo = arrivo;
    }

    public String getDipendente() {
        return dipendente;
    }

    public void setDipendente(String dipendente) {
        this.dipendente = dipendente;
    }

    public String getVeicolo() {
        return veicolo;
    }

    public void setVeicolo(String veicolo) {
        this.veicolo = veicolo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getPartenza() {
        return partenza;
    }

    public void setPartenza(String partenza) {
        this.partenza = partenza;
    }

    public String getArrivo() {
        return arrivo;
    }

    public void setArrivo(String arrivo) {
        this.arrivo = arrivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViaggioId viaggioId = (ViaggioId) o;
        return Objects.equals(dipendente, viaggioId.dipendente)
                && Objects.equals(veicolo, viaggioId.veicolo)
                && Objects.equals(data, viaggioId.data)
                && Objects.equals(partenza, viaggioId.partenza)
                && Objects.equals(arrivo, viaggioId.arrivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dipendente, veicolo, data, partenza, arrivo);
    }

    @Override
    public String toString() {
        return "ViaggioId{"
                + "dipendente='" + dipendente + '\''
                + ", veicolo='" + veicolo + '\''
                + ", data=" + data
                + ", partenza='" + partenza + '\''
                + ", arrivo='" + arrivo + '\''
                + '}';
    }
}
