package it.unipi.PROGETTO_SERVER.Repositories;

import it.unipi.PROGETTO_SERVER.TabelleDB.Dipendente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DipendenteRepository extends CrudRepository<Dipendente, String> {

    @Modifying
    @Transactional
    @Query("UPDATE Dipendente d SET d.sessionToken = ?2 WHERE d.username = ?1")
    public void updateToken(String nome, String newToken);
}
