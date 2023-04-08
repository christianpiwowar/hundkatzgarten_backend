package com.example.hundkatzgarten_backend.controller;

import com.example.hundkatzgarten_backend.model.Auftrag;
import com.example.hundkatzgarten_backend.model.Auftragsposition;
import com.example.hundkatzgarten_backend.model.Dienstleistung;
import com.example.hundkatzgarten_backend.repository.AuftragRepository;
import com.example.hundkatzgarten_backend.repository.AuftragspositionRepository;
import com.example.hundkatzgarten_backend.repository.DienstleistungRepository;
import com.example.hundkatzgarten_backend.repository.KundeRepository;
import com.example.hundkatzgarten_backend.service.AuftragService;
import com.example.hundkatzgarten_backend.service.dto.AuftragDto;
import com.example.hundkatzgarten_backend.service.dto.CreateRechnungWithMultipleIdDTO;
import com.example.hundkatzgarten_backend.service.dto.CreateRechnungWithSingleIDDTO;
import com.example.hundkatzgarten_backend.service.pdfCreation.PdfCreator;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/auftrag")
public class AuftragController {

    @Autowired
    AuftragRepository auftragRepository;
    @Autowired
    AuftragspositionRepository auftragspositionRepository;
    @Autowired
    KundeRepository kundeRepository;
    @Autowired
    private DienstleistungRepository dienstleistungRepository;
    @Autowired
    private AuftragService auftragService = new AuftragService(auftragRepository);


    @Autowired
    private final PdfCreator pdfCreator = new PdfCreator(kundeRepository, auftragspositionRepository, auftragRepository);


    @PutMapping("/updateBezahlt/{id}")
    public ResponseEntity<Auftrag> updateAuftragBezahlt(@PathVariable("id") long id, @RequestBody boolean bezahlt) {
        Optional<Auftrag> auftragData = auftragRepository.findById(id);

        if (auftragData.isPresent()) {
            Auftrag _auftrag = auftragData.get();
            _auftrag.setBezahlt(bezahlt);
            return new ResponseEntity<>(auftragRepository.save(_auftrag), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auftrag> getAuftragById(@PathVariable("id") long id) {
        Optional<Auftrag> auftragData = auftragRepository.findById(id);

        if (auftragData.isPresent()) {
            return new ResponseEntity<>(auftragData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/kunden/{id}")
    public ResponseEntity<List<Auftrag>> getAuftragByKundeId(@PathVariable("id") long id) {
        Optional<List<Auftrag>> auftragData = Optional.ofNullable(auftragRepository.findAuftragByKundeId(id));

        if (auftragData.isPresent()) {
            return new ResponseEntity<>(auftragData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("/byDate/")
//    public ResponseEntity<List<Auftrag>> findAuftragByDateCreatedBetween(@RequestParam("datum_anfang") Date datumAnfang, @RequestParam("datum_anfang") Date datumEnde) {
//        Optional<List<Auftrag>> auftragData = Optional.ofNullable(auftragRepository.findAuftragByDateCreatedBetween(datumAnfang, datumEnde));
//
//        if (auftragData.isPresent()) {
//            return new ResponseEntity<>(auftragData.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

//    @GetMapping("/")
//    public ResponseEntity<List<Auftrag>> getAllAuftrag() {
//
//        try {
//            List<Auftrag> auftragData = auftragRepository.findAll();
//            System.out.println(auftragData.size());
//            if (!auftragData.isEmpty()) {
//                return new ResponseEntity<>(auftragData, HttpStatus.OK);
//                //return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//            } else {
//                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage() + e.getCause());
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/auftragAndPositionen")
    public ResponseEntity<List<AuftragDto>> getAllAuftragAndPositionen() {

        try {
            List<AuftragDto> auftragDtoData = new ArrayList<>();

            List<Auftrag> auftragData = auftragRepository.findAll();
            List<Dienstleistung> dienstleistungList = new ArrayList<>();
            for (Auftrag af : auftragData) {
                AuftragDto tempAuftragDto = new AuftragDto();
                tempAuftragDto.setId(af.getId());
                tempAuftragDto.setKundenId(af.getKundeId());
                tempAuftragDto.setAuftragspositionList(auftragspositionRepository.
                        findAuftragspositionByAuftragId(af.getId()));
                tempAuftragDto.setTotalAuftrag(af.getTotalAuftrag());
                tempAuftragDto.setDateCreated(af.getDateCreated());
                tempAuftragDto.setBezahlt(af.isBezahlt());
//                List<Optional<Dienstleistung>> tempDienstleistungList = new ArrayList<>();
//                for(Auftragsposition ap : tempAuftragDto.getAuftragspositionList()){
//                    tempDienstleistungList.add(dienstleistungRepository.findById(ap.getDienstleistungId()));
//                }
//                tempAuftragDto.setDienstleistungList(tempDienstleistungList);
                auftragDtoData.add(tempAuftragDto);
            }


            System.out.println(auftragDtoData.size());
            if (!auftragDtoData.isEmpty()) {
                return new ResponseEntity<>(auftragDtoData, HttpStatus.OK);
                //return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + e.getCause());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAuftrag(@PathVariable("id") long id) {
        try {
            if (auftragRepository.findById(id).isPresent()) {
                auftragspositionRepository.deleteByAuftragId(id);
                auftragRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<HttpStatus> deleteAllKunden() {
        try {
            auftragRepository.deleteAll();
            auftragspositionRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Auftrag> createAuftrag(@RequestBody long kundeId) {
        try {
            if (kundeRepository.findById(kundeId).isPresent()) {
                Auftrag _auftrag = auftragRepository
                        .save(new Auftrag(kundeId));
                return new ResponseEntity<>(_auftrag, HttpStatus.CREATED);
            } else {
                System.out.println("errrrorrrrrrrro");
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + e.getCause());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/auftragFromDto")
    public ResponseEntity<Auftrag> createAuftragFromDto(@RequestBody AuftragDto auftragDto) {
        try {
            Long kundenId = auftragDto.getKundenId();
            List<Long> dienstleistungIds = auftragDto.getDienstleistungIds();
            List<Integer> mengen = auftragDto.getMengen();

            if (kundenId == null || dienstleistungIds == null || dienstleistungIds.isEmpty() || mengen.isEmpty() || mengen == null) {
                System.out.println("something is null");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (kundeRepository.findById(kundenId).isPresent()) {
                Auftrag auftrag = auftragRepository.save(new Auftrag(kundenId, auftragService.getLargestRechnunsnummer() + 1));
                List<Auftragsposition> auftragspositionen = new ArrayList<>();

//                for (Long dienstleistungId : dienstleistungIds) {
//                    Auftragsposition auftragsposition = new Auftragsposition(auftrag.getId(), dienstleistungId);
//                    Optional<Dienstleistung> tempDl = dienstleistungRepository.findById(dienstleistungId);
//                    auftragsposition.setDlName(tempDl.get().getName());
//                    auftragsposition.setCurrentPreisDl(tempDl.get().getPreis());
//                    auftragsposition.setTotalPosition();
//
//                    auftragspositionen.add(auftragsposition);
//                }
                for (int i = 0; i < dienstleistungIds.size(); i++) {
                    Long dienstleistungId = dienstleistungIds.get(i);
                    Integer menge = mengen.get(i);
                    Auftragsposition auftragsposition = new Auftragsposition(auftrag.getId(), dienstleistungId);
                    Optional<Dienstleistung> tempDl = dienstleistungRepository.findById(dienstleistungId);
                    auftragsposition.setDlName(tempDl.get().getName());
                    auftragsposition.setCurrentPreisDl(tempDl.get().getPreis());
                    auftragsposition.setMenge(menge);
                    auftragsposition.setTotalPosition();
                    auftrag.setTotalAuftrag(auftrag.getTotalAuftrag().add(auftragsposition.getTotalPosition()));

                    auftragspositionen.add(auftragsposition);
                }
                auftragspositionRepository.saveAll(auftragspositionen);

                return new ResponseEntity<>(auftrag, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    //methode zur erstellung einer Rechnung mit 1 AuftragId
//    @PostMapping("/createRechnung")
//    public ResponseEntity<CreateRechnungWithSingleIDDTO> createRechnungWithSingleAuftragId(@RequestBody CreateRechnungWithSingleIDDTO dto) {
//        try {
//                //System.out.println(""+dto.auftragId()+dto.faelligkeitsdatum()+dto.rechnungsnummer()+dto.ordnerPfad()+dto.dateiname());
//            //if(auftragRepository.findById(dto.auftragId()).isPresent()){
//            if (true)
//            {
//
//                pdfCreator.createPdfWithSingleId(dto.auftragId(), dto.faelligkeitsdatum(), dto.rechnungsnummer(), dto.dateiname(), dto.ordnerPfad());
//
//
//                return new ResponseEntity<>(dto, HttpStatus.CREATED);
//            }
//            else {
//                System.out.println("errrrorrrrrrrror");
//                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage() + e.getCause());
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    //methode zur erstellung einer Rechnung mit mehreren AuftragId
//    @PostMapping("/createRechnungMultipleId")
//    public ResponseEntity<CreateRechnungWithMultipleIdDTO> createRechnungWithMultipleAuftragIds(@RequestBody CreateRechnungWithMultipleIdDTO dto) {
//        try {
//
//                System.out.println(""+dto.auftragIds()+dto.faelligkeitsdatum()+dto.rechnungsnummer()+dto.ordnerPfad()+dto.dateiname());
//            //if(auftragRepository.findById(dto.auftragId()).isPresent()){
//            if (true) //replace
//            {
//                System.out.println("x");
//
//                pdfCreator.createPdfWithMultipleAuftragIds(dto.auftragIds(), dto.faelligkeitsdatum(), dto.rechnungsnummer(), dto.dateiname(), dto.ordnerPfad());
//
//                return new ResponseEntity<>(dto, HttpStatus.CREATED);
//            }
//            else {
//                System.out.println("errrrorrrrrrrror");
//                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage() + e.getCause());
//            System.out.println("y");
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/createRechnungMultipleId")
    public ResponseEntity<CreateRechnungWithMultipleIdDTO> createPdfWithMultipleIds(@RequestBody CreateRechnungWithMultipleIdDTO dto, HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            //response.setHeader("Content-Disposition", "attachment;filename=myfile.pdf");

            byte[] pdf = pdfCreator.createPdfWithMultipleAuftragIds(dto.auftragIds(), dto.faelligkeitsdatum(), dto.rechnungsnummer());
            response.getOutputStream().write(pdf);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createRechnung")
    public ResponseEntity<byte[]> createPdf(@RequestBody CreateRechnungWithSingleIDDTO dto, HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=myfile.pdf");

            byte[] pdf = pdfCreator.createPdfWithSingleId(dto.auftragId());
            response.getOutputStream().write(pdf);
            return new ResponseEntity<>(pdf, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " - " + e.getCause() + e.toString());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
