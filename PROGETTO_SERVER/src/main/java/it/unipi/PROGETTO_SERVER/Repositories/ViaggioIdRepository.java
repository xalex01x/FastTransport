package it.unipi.PROGETTO_SERVER.Repositories;

import it.unipi.PROGETTO_SERVER.TabelleDB.ViaggioId;
import it.unipi.PROGETTO_SERVER.TabelleDB.ViaggioSenzaCarico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViaggioIdRepository extends CrudRepository<ViaggioSenzaCarico, ViaggioId> {

    @Query("SELECT v FROM ViaggioSenzaCarico v WHERE FUNCTION('MONTH', v.id.data) = ?1 AND FUNCTION('YEAR', v.id.data) = ?2")
    Iterable<ViaggioSenzaCarico> monthCalendar(int month, int year);   // per ritornare tutti i viaggi previsti per un mese

    @Query("SELECT v FROM ViaggioSenzaCarico v WHERE v.id.dipendente = ?1 AND FUNCTION('MONTH', v.id.data) = ?2 AND FUNCTION('YEAR', v.id.data) = ?3")
    Iterable<ViaggioSenzaCarico> monthByUserId(String dipendente, int month, int year);   // per ritornare tutti i viaggi previsti per un mese per un dato dipendente
}
