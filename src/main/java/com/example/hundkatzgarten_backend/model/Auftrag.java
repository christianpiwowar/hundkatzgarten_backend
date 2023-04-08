package com.example.hundkatzgarten_backend.model;

//import com.example.bezkoder_tutorial.model.Auftragsposition;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "auftrag")
public class Auftrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kundenId")
    private Long kundeId;

    // TODO: add Date dateCreated, BigDecimal totalAuftrag, boolean bezahlt- getters setters (automatic) AuftragController create updaten

    @Column(name = "date_created")
    private Date dateCreated;
    @Column(name = "total_auftrag")
    private BigDecimal totalAuftrag;

    @Column(name = "bezahlt")
    private boolean bezahlt;

    @Column(name = "rechnungsnummer")
    private int rechnungsnummer;

    public Auftrag() {
        this.dateCreated = new Date();
        this.totalAuftrag = BigDecimal.valueOf(0);
    }

    public Auftrag(Long kundeId) {
        this.kundeId = kundeId;
        this.dateCreated = new Date();
        this.totalAuftrag = BigDecimal.valueOf(0);

    }

    public Auftrag(Long kundeId, int rechnungsnummer) {
        this.kundeId = kundeId;
        this.dateCreated = new Date();
        this.totalAuftrag = BigDecimal.valueOf(0);
        this.rechnungsnummer = rechnungsnummer;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public BigDecimal getTotalAuftrag() {
        return totalAuftrag;
    }

    public void setTotalAuftrag(BigDecimal totalAuftrag) {
        this.totalAuftrag = totalAuftrag;
    }

    public boolean isBezahlt() {
        return bezahlt;
    }

    public void setBezahlt(boolean bezahlt) {
        this.bezahlt = bezahlt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKundeId() {
        return kundeId;
    }

    public void setKundeId(Long kundeId) {
        this.kundeId = kundeId;
    }

    public int getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(int rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }
}
