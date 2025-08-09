package it.unipi.PROGETTO_SERVER;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class ProgettoServerApplication {

    private static final Logger log = LogManager.getLogger(ProgettoServerApplication.class);

    private static void databaseInit() {
        // privamo a connetterci al db
        // se non esiste va creato
        try ( Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306", "root", "root")) {
            ResultSet cat = con.getMetaData().getCatalogs();
            boolean found = false;
            while (cat.next()) {
                log.info("Trovato database: " + cat.getString(1));
                if (cat.getString(1).equals("616157")) {
                    log.info("Database di progetto trovato");
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.info("Database non trovato, creazione");
                String DBPATH = "/src/main/resources/initFiles/616157.sql";
                String PATH = new File("").getAbsolutePath() + DBPATH;
                Reader reader = Files.newBufferedReader(Path.of(PATH));
                ScriptRunner scriptRunner = new ScriptRunner(con);
                scriptRunner.setStopOnError(true);
                scriptRunner.runScript(reader);
            }
        } catch (Exception e) {
            log.fatal("Impossibile inizializzare il database:" + e.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        databaseInit();
        SpringApplication.run(ProgettoServerApplication.class, args);
    }

}
