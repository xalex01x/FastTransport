package it.unipi.PROGETTO_SERVER.TabelleDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sedelogistica", schema = "616157")
public class SedeLogistica {

    @Id
    @Column(name = "nome", length = 45)
    private String nome;

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public SedeLogistica(String nome) {
        this.nome = nome;
    }

    public SedeLogistica() {
    }
}
