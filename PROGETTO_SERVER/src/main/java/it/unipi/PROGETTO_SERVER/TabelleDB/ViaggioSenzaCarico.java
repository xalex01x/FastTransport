package it.unipi.PROGETTO_SERVER.TabelleDB;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "viaggio", schema = "616157")
public class ViaggioSenzaCarico {

    @EmbeddedId
    private ViaggioId id;

    public ViaggioSenzaCarico() {
    }

    public ViaggioSenzaCarico(ViaggioId id) {
        this.id = id;
    }

    public ViaggioId getId() {
        return id;
    }

    public void setId(ViaggioId id) {
        this.id = id;
    }

    public String[] getStrings() {
        String[] res = {id.getDipendente(), id.getVeicolo(), id.getData().toString(), id.getPartenza(), id.getArrivo()};
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViaggioSenzaCarico viaggio = (ViaggioSenzaCarico) o;
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
                + '}';
    }
}
