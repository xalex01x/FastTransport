package it.unipi.PROGETTO_SERVER.TabelleDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "dipendente", schema = "616157")
public class Dipendente implements Serializable {

    @Id
    @Column(name = "username", length = 45)
    private String username;
    @Column(name = "password", length = 64)
    private String password;
    @Column(name = "ruolo", length = 45)
    private String ruolo;
    @Column(name = "sessiontoken", length = 64)
    private String sessionToken;

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public Dipendente(String username, String password, String ruolo, String sessionToken) {
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
        this.sessionToken = sessionToken;
    }

    public Dipendente() {
    }
}
