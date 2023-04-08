package com.example.hundkatzgarten_backend.repository;

import com.example.hundkatzgarten_backend.model.Auftrag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuftragRepository extends JpaRepository<Auftrag, Long> {
    //List<Kunde> findByNachnameContaining(String nachname);
    //List<Kunde> findByKundennummerContaining(String kundennummer);
    List<Auftrag> findAuftragByKundeId(long kundeId);

    List<Auftrag> findAuftragByBezahlt(boolean bezahlt);

//    List<Auftrag> findAuftragByDateCreatedBetween(Date datumAnfang, Date datumEnde);

}