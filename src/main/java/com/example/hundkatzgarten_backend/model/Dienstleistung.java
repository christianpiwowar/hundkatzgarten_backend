package com.example.hundkatzgarten_backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "dienstleistung")
public class Dienstleistung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "beschreibung")
    private String beschreibung;

    @Column(name = "preis")
    private BigDecimal preis;

//    @OneToMany(mappedBy = "dienstleistung", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    //@JoinColumn(foreignKey = @ForeignKey( name = "fk_auftrag_id"))
//    private List<Auftragsposition> auftragspositionen;


    public Dienstleistung() {

    }

    public Dienstleistung(String name, String beschreibung, BigDecimal preis) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.preis = preis;
    }

    public Dienstleistung(String name, String beschreibung, BigDecimal preis, List<Auftragsposition> auftragspositionen) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.preis = preis;
        //this.auftragspositionen = auftragspositionen;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public BigDecimal getPreis() {
        return preis;
    }

    public void setPreis(BigDecimal preis) {
        this.preis = preis;
    }

    @Override
    public String toString() {
        return "Dienstleistung{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", preis=" + preis +
                '}';
    }
}
