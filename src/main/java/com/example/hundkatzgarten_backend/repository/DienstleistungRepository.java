package com.example.hundkatzgarten_backend.repository;

import com.example.hundkatzgarten_backend.model.Dienstleistung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DienstleistungRepository extends JpaRepository<Dienstleistung, Long> {
    List<Dienstleistung> findByNameContaining(String name);
}
