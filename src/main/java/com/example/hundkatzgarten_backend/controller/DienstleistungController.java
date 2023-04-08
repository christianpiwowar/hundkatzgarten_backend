package com.example.hundkatzgarten_backend.controller;


import com.example.hundkatzgarten_backend.model.Dienstleistung;
import com.example.hundkatzgarten_backend.repository.DienstleistungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:8081")
@CrossOrigin(origins = {"http://localhost:8081", "http://192.168.8.20:8081"})
@RestController
@RequestMapping("/api")
public class DienstleistungController {

    @Autowired
    DienstleistungRepository dienstleistungRepository;

    @GetMapping("/dienstleistungen")
    public ResponseEntity<List<Dienstleistung>> getAllDienstleistungen(@RequestParam(required = false) String name) {
        try {
            List<Dienstleistung> dienstleistungen = new ArrayList<Dienstleistung>();

            if (name == null)
                dienstleistungRepository.findAll().forEach(dienstleistungen::add);
            else
                dienstleistungRepository.findByNameContaining(name).forEach(dienstleistungen::add);

            if (dienstleistungen.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(dienstleistungen, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dienstleistungen/{id}")
    public ResponseEntity<Dienstleistung> getDienstleistungById(@PathVariable("id") long id) {
        Optional<Dienstleistung> dienstleistungData = dienstleistungRepository.findById(id);

        if (dienstleistungData.isPresent()) {
            return new ResponseEntity<>(dienstleistungData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/dienstleistungen")
    public ResponseEntity<Dienstleistung> createDienstleistung(@RequestBody Dienstleistung dienstleistung) {
        try {
            Dienstleistung _dienstleistung = dienstleistungRepository
                    .save(new Dienstleistung(dienstleistung.getName(),
                            dienstleistung.getBeschreibung(),
                            dienstleistung.getPreis()));
            return new ResponseEntity<>(_dienstleistung, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dienstleistungen/{id}")
    public ResponseEntity<Dienstleistung> updateDienstleistung(@PathVariable("id") long id, @RequestBody Dienstleistung dienstleistung) {
        Optional<Dienstleistung> dienstleistungData = dienstleistungRepository.findById(id);

        if (dienstleistungData.isPresent()) {
            Dienstleistung _dienstleistung = dienstleistungData.get();
            _dienstleistung.setName(dienstleistung.getName());
            _dienstleistung.setBeschreibung(dienstleistung.getBeschreibung());
            _dienstleistung.setPreis(dienstleistung.getPreis());
            return new ResponseEntity<>(dienstleistungRepository.save(_dienstleistung), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/dienstleistungen/{id}")
    public ResponseEntity<HttpStatus> deleteDienstleistung(@PathVariable("id") long id) {
        try {
            dienstleistungRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/dienstleistungen")
    public ResponseEntity<HttpStatus> deleteAllDienstleistungen() {
        try {
            dienstleistungRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
