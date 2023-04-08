package com.example.hundkatzgarten_backend.repository;

import com.example.hundkatzgarten_backend.model.Auftragsposition;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuftragspositionRepository extends JpaRepository<Auftragsposition, Long> {
    List<Auftragsposition> findAuftragspositionByAuftragId(Long auftragId);

    @Transactional
    void deleteByAuftragId(long auftragId);
}
