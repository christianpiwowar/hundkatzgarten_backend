package com.example.hundkatzgarten_backend.controller;


import com.example.hundkatzgarten_backend.repository.AuftragspositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:8081")
@CrossOrigin(origins = {"http://localhost:8081", "http://192.168.8.20:8081"})
@RestController
@RequestMapping("/api/auftragspositionen")
public class AuftragspositionController {

    private final AuftragspositionRepository auftragspositionRepository;


    @Autowired
    public AuftragspositionController(AuftragspositionRepository auftragspositionRepository) {
        this.auftragspositionRepository = auftragspositionRepository;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuftragsposition(@PathVariable Long id) {
        auftragspositionRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
