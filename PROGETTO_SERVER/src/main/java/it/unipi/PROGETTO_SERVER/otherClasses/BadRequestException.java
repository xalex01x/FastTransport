package it.unipi.PROGETTO_SERVER.otherClasses;

import it.unipi.PROGETTO_SERVER.ProgettoServerApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BadRequestException extends RuntimeException {

    private static final Logger log = LogManager.getLogger(ProgettoServerApplication.class);

    public BadRequestException() {
        super();
        log.error("BadRequest received");
    }

    public BadRequestException(String s) {
        super(s);
        log.error("BadRequest: " + s);
    }
}
