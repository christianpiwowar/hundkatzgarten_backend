package com.example.hundkatzgarten_backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "auftragsposition")
public class Auftragsposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "auftrag-id")
    private long auftragId;
    @Column(name = "dienstleistung-id")
    private long dienstleistungId;

    @Column(name = "current-preis-dl")
    private BigDecimal currentPreisDl;
    @Column(name = "dl-name")
    private String dlName;

    @Column(name = "menge")
    private int menge;
    @Column(name = "total-position")
    private BigDecimal totalPosition;

    public Auftragsposition() {

    }

    public Auftragsposition(long auftragId, long dienstleistungId) {
        this.auftragId = auftragId;
        this.dienstleistungId = dienstleistungId;
    }

    public Auftragsposition(long dienstleistungId, int menge, BigDecimal currentPreisDl, String dlName) {

        this.dienstleistungId = dienstleistungId;
        this.menge = menge;
        this.currentPreisDl = currentPreisDl;
        this.dlName = dlName;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCurrentPreisDl() {
        return currentPreisDl;
    }

    public void setCurrentPreisDl(BigDecimal currentPreisDl) {
        this.currentPreisDl = currentPreisDl;
    }

    public String getDlName() {
        return dlName;
    }

    public void setDlName(String dlName) {
        this.dlName = dlName;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public BigDecimal getTotalPosition() {
        return totalPosition;
    }

    public BigDecimal multiplyMengeAndCurrentPreis() {
        return currentPreisDl.multiply(new BigDecimal(menge));
    }


    public void setTotalPosition() {
        this.totalPosition = this.currentPreisDl.multiply(BigDecimal.valueOf(this.menge));
    }

    public long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(long auftragId) {
        this.auftragId = auftragId;
    }

    public long getDienstleistungId() {
        return dienstleistungId;
    }

    public void setDienstleistungId(long dienstleistungId) {
        this.dienstleistungId = dienstleistungId;
    }
}
