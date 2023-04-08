package com.example.hundkatzgarten_backend.service.dto;

import java.util.List;

public record CreateRechnungWithMultipleIdDTO(

        List<Long> auftragIds,
        String faelligkeitsdatum,
        int rechnungsnummer
) {

}
