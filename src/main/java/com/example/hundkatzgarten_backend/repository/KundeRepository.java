package com.example.hundkatzgarten_backend.repository;

import com.example.hundkatzgarten_backend.model.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KundeRepository extends JpaRepository<Kunde, Long> {
    List<Kunde> findByNachnameContaining(String nachname);

    List<Kunde> findByKundennummerContaining(String kundennummer);

}
