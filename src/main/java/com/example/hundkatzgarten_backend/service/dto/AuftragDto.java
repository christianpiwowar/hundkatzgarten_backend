package com.example.hundkatzgarten_backend.service.dto;

import com.example.hundkatzgarten_backend.model.Auftragsposition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuftragDto {

    private Long id;
    private Long kundenId;

    private Date dateCreated;

    private BigDecimal totalAuftrag;

    private boolean bezahlt;
    private List<Long> dienstleistungIds;


    //private List<Optional<Dienstleistung>> dienstleistungList;

    private List<Integer> mengen;

    private List<Auftragsposition> auftragspositionList;


    public AuftragDto() {
        this.dienstleistungIds = new ArrayList<>();
        this.mengen = new ArrayList<>();
        this.auftragspositionList = new ArrayList<>();

    }

    public AuftragDto(Long kundenId, List<Long> dienstleistungIds, List<Integer> mengen) {
        this.kundenId = kundenId;
        this.dienstleistungIds = dienstleistungIds;
        this.mengen = mengen;
    }

//    public AuftragDto(Long kundenId, List<Auftragsposition> auftragspositionList) {
//        this.kundenId = kundenId;
//        this.auftragspositionList = auftragspositionList;
//    }


//    public List<Optional<Dienstleistung>> getDienstleistungList() {
//        return dienstleistungList;
//    }
//
//    public void setDienstleistungList(List<Optional<Dienstleistung>> dienstleistungList) {
//        this.dienstleistungList = dienstleistungList;
//    }


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

    public List<Integer> getMengen() {
        return mengen;
    }

    public void setMenge(List<Integer> menge) {
        this.mengen = menge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Auftragsposition> getAuftragspositionList() {
        return auftragspositionList;
    }

    public void setAuftragspositionList(List<Auftragsposition> auftragspositionList) {
        this.auftragspositionList = auftragspositionList;
    }

    public Long getKundenId() {
        return kundenId;
    }

    public void setKundenId(Long kundenId) {
        this.kundenId = kundenId;
    }

    public List<Long> getDienstleistungIds() {
        return dienstleistungIds;
    }

    public void setDienstleistungIds(List<Long> dienstleistungIds) {
        this.dienstleistungIds = dienstleistungIds;
    }
}
