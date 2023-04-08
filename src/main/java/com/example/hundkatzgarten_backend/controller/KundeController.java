package com.example.hundkatzgarten_backend.controller;

import com.example.hundkatzgarten_backend.model.Kunde;
import com.example.hundkatzgarten_backend.repository.KundeRepository;
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
public class KundeController {

    @Autowired
    KundeRepository kundeRepository;


    @GetMapping("/kunden")
    public ResponseEntity<List<Kunde>> getAllKunden(@RequestParam(required = false) String nachname) {
        try {
            List<Kunde> kunden = new ArrayList<Kunde>();

            if (nachname == null)
                kundeRepository.findAll().forEach(kunden::add);
            else
                kundeRepository.findByNachnameContaining(nachname).forEach(kunden::add);

            if (kunden.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(kunden, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/*
    @GetMapping("/kunden")
    public ResponseEntity<List<Kunde>> getKundenByKundennummer(@RequestParam(required = false) String kundennummer){
        try {
            List<Kunde> kunden = new ArrayList<Kunde>();

            if (kundennummer == null)
                kundeRepository.findAll().forEach(kunden::add);
            else
                kundeRepository.findByKundennummerContaining(kundennummer).forEach(kunden::add);

            if (kunden.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(kunden, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 */

    @GetMapping("/kunden/{id}")
    public ResponseEntity<Kunde> getKundeById(@PathVariable("id") long id) {
        Optional<Kunde> kundeData = kundeRepository.findById(id);

        if (kundeData.isPresent()) {
            return new ResponseEntity<>(kundeData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/kunden")
    public ResponseEntity<Kunde> createKunde(@RequestBody Kunde kunde) {
        try {
            Kunde _kunde = kundeRepository
                    .save(new Kunde(kunde.getAnrede(), kunde.getVorname(), kunde.getNachname(),
                            kunde.getStrasse(), kunde.getOrt(), kunde.getPlz(), kunde.getLand(),
                            kunde.getKundennummer(), kunde.getTelefonnummer(), kunde.getEmail()));
            return new ResponseEntity<>(_kunde, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/kunden/{id}")
    public ResponseEntity<Kunde> updateKunde(@PathVariable("id") long id, @RequestBody Kunde kunde) {
        Optional<Kunde> kundeData = kundeRepository.findById(id);

        if (kundeData.isPresent()) {
            Kunde _kunde = kundeData.get();
            _kunde.setAnrede(kunde.getAnrede());
            _kunde.setVorname(kunde.getVorname());
            _kunde.setNachname(kunde.getNachname());
            _kunde.setStrasse(kunde.getStrasse());
            _kunde.setOrt(kunde.getOrt());
            _kunde.setPlz(kunde.getPlz());
            _kunde.setLand(kunde.getLand());
            _kunde.setKundennummer(kunde.getKundennummer());
            _kunde.setTelefonnummer(kunde.getTelefonnummer());
            _kunde.setEmail(kunde.getEmail());
            return new ResponseEntity<>(kundeRepository.save(_kunde), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/kunden/{id}")
    public ResponseEntity<HttpStatus> deleteKunde(@PathVariable("id") long id) {
        try {
            kundeRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/kunden")
    public ResponseEntity<HttpStatus> deleteAllKunden() {
        try {
            kundeRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
