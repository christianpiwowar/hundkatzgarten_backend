package com.example.hundkatzgarten_backend.service;


import com.example.hundkatzgarten_backend.model.Auftrag;
import com.example.hundkatzgarten_backend.repository.AuftragRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuftragService {
    private final AuftragRepository auftragRepository;


    public int getLargestRechnunsnummer() {
        int largest = 0;
        List<Auftrag> auftragList = auftragRepository.findAll();

        for (Auftrag auftrag : auftragList) {
            if (auftrag.getRechnungsnummer() > largest) {
                largest = auftrag.getRechnungsnummer();
            }
        }
        return largest;

    }
}
