package it.unipi.PROGETTO_SERVER.TabelleDB;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "viaggio", schema = "616157")
public class Viaggio {

    @EmbeddedId
    private ViaggioId id;

    @Lob
    @Column(name = "carico", columnDefinition = "blob")
    private byte[] carico;

    public Viaggio() {
    }

    public Viaggio(ViaggioId id, byte[] carico) {
        this.id = id;
        this.carico = carico;
    }

    public ViaggioId getId() {
        return id;
    }

    public void setId(ViaggioId id) {
        this.id = id;
    }

    public byte[] getCarico() {
        return carico;
    }

    public void setCarico(byte[] carico) {
        this.carico = carico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Viaggio viaggio = (Viaggio) o;
        return Objects.equals(id, viaggio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Viaggio{"
                + "id=" + id
                + ", carico=" + carico
                + '}';
    }
}
