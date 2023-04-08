package com.example.hundkatzgarten_backend.service.pdfCreation;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateTimeFactory {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDate currentTime() {
        return LocalDate.now();
    }
}
