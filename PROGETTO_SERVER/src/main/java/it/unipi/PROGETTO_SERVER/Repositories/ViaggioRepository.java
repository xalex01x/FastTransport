package it.unipi.PROGETTO_SERVER.Repositories;

import it.unipi.PROGETTO_SERVER.TabelleDB.Viaggio;
import it.unipi.PROGETTO_SERVER.TabelleDB.ViaggioId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViaggioRepository extends CrudRepository<Viaggio, ViaggioId> {
}
