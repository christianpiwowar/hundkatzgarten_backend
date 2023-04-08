package com.example.hundkatzgarten_backend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "kunde")
public class Kunde {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "anrede")
    private String anrede;
    @Column(name = "vorname")
    private String vorname;
    @Column(name = "nachname")
    private String nachname;
    @Column(name = "strasse")
    private String strasse;
    @Column(name = "ort")
    private String ort;
    @Column(name = "plz")
    private String plz;
    @Column(name = "land")
    private String land;
    @Column(name = "kundennummer")
    private String kundennummer;
    @Column(name = "telefonnummer")
    private String telefonnummer;
    @Column(name = "email")
    private String email;
//    @OneToMany(mappedBy = "kunde")
//    private List<Auftrag> auftraege;

    public Kunde() {

    }

    public Kunde(String anrede, String vorname, String nachname,
                 String strasse, String ort, String plz, String land,
                 String kundennummer, String telefonnummer, String email) {
        this.anrede = anrede;
        this.vorname = vorname;
        this.nachname = nachname;
        this.strasse = strasse;
        this.ort = ort;
        this.plz = plz;
        this.land = land;
        this.kundennummer = kundennummer;
        this.telefonnummer = telefonnummer;
        this.email = email;
    }

    public Kunde(String anrede, String vorname, String nachname,
                 String strasse, String ort, String plz, String land,
                 String kundennummer, String telefonnummer, String email, List<Auftrag> auftraege) {
        this.anrede = anrede;
        this.vorname = vorname;
        this.nachname = nachname;
        this.strasse = strasse;
        this.ort = ort;
        this.plz = plz;
        this.land = land;
        this.kundennummer = kundennummer;
        this.telefonnummer = telefonnummer;
        this.email = email;
//        this.auftraege = auftraege;
    }

    public long getId() {
        return id;
    }

    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public List<Auftrag> getAuftraege() {
//        return auftraege;
//    }
//
//    public void setAuftraege(List<Auftrag> auftraege) {
//        this.auftraege = auftraege;
//    }

    @Override
    public String toString() {
        return "Kunde{" +
                "id=" + id +
                ", anrede='" + anrede + '\'' +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", strasse='" + strasse + '\'' +
                ", ort='" + ort + '\'' +
                ", plz='" + plz + '\'' +
                ", land='" + land + '\'' +
                ", kundennummer='" + kundennummer + '\'' +
                ", telefonnummer='" + telefonnummer + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
