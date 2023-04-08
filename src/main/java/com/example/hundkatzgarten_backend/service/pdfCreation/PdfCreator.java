package com.example.hundkatzgarten_backend.service.pdfCreation;


import com.example.hundkatzgarten_backend.model.Auftrag;
import com.example.hundkatzgarten_backend.model.Auftragsposition;
import com.example.hundkatzgarten_backend.model.Kunde;
import com.example.hundkatzgarten_backend.repository.AuftragRepository;
import com.example.hundkatzgarten_backend.repository.AuftragspositionRepository;
import com.example.hundkatzgarten_backend.repository.KundeRepository;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

//https://www.javatpoint.com/java-create-pdf

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PdfCreator {

    //https://www.youtube.com/watch?v=VcBilR9oO1w

    private final KundeRepository kundeRepository;
    private final AuftragspositionRepository auftragspositionRepository;
    private final AuftragRepository auftragRepository;

    private final String rechnungsstellerName = "Andreas Hoffleit";
    private final String rechnungsstellerAdresse = "Wienerstraße 174";
    private final String rechnungsstellerPLZundOrt = "2013 Göllersdorf 174";
    private final String BIC = "BAWAATWWXXX";
    private final String IBAN = "AT836000000078413942";


    //fälligkeitsdatum = today += 30 tage
    @Transactional
    public byte[] createPdfWithSingleId(Long auftragId) throws IOException, WriterException //verschlüsseln?  berechtigungen?
    {
        Auftrag auftrag = auftragRepository.findById(auftragId).get();
        Kunde kunde = kundeRepository.findById(auftrag.getKundeId()).get();
        List<Auftragsposition> auftragspositionList = auftragspositionRepository.findAuftragspositionByAuftragId(auftragId);
        LocalDate rechnungsdatum = new DateTimeFactory().today();
        LocalDate faelligkeitsdatum = rechnungsdatum.plusDays(30);
        BigDecimal summe = new BigDecimal("0.00");

        //Dokumenterstellung
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDFont font = PDType1Font.HELVETICA;
        int fontSize = 9;
        Color textColor = Color.black;
        int pageWidth = (int) page.getTrimBox().getWidth();
        int pageHeight = (int) page.getTrimBox().getHeight();

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        List<String> kundenDaten = fillKundeArray(kunde);

        //Kundendaten
        addMultiline(contentStream, kundenDaten, 10.0f, 75, pageHeight - 220, font, fontSize, textColor);

        //Beginn der Dientleistungstabelle //dynamisch machen
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
        contentStream.newLineAtOffset(40, pageHeight - 300);
        contentStream.showText("Rechnung");
        contentStream.newLine();
        contentStream.setFont(font, fontSize);
        contentStream.newLine();
        contentStream.showText("Wir erlauben uns, folgende Leistungen in Rechnung zu stellen:");
        contentStream.endText();

//        Rechnungs Info (rechts)
        String[] rechnungstellerDaten = {rechnungsstellerName, "Dein Dogsitter", rechnungsstellerAdresse, rechnungsstellerPLZundOrt, "", "", ""
                , "Rechnungsdatum: " + rechnungsdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), "Fälligkeitsdatum: " + faelligkeitsdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                , "Kundennr.: " + kunde.getKundennummer().trim(), "Rechnungsnr.: " + auftrag.getRechnungsnummer()};

        addMultilineRightAligned(contentStream, rechnungstellerDaten, pageWidth - 110,
                pageHeight - 150, font, fontSize, textColor);

        //Rechnungspositionen Tabelle

        //linie an Tabellenanfang
        addSingleLine(contentStream, "_______________________________________________________" +
                        "_______________________________________________"
                , 40, pageHeight - 350, font, fontSize, textColor);

        PdfTabellenCreator dienstleistungsTabelle = new PdfTabellenCreator(contentStream);
        int[] cellWith = {30, 160, 60, 120, 60, 50, 50};
        dienstleistungsTabelle.setTable(cellWith, 15, 25, pageHeight - 360);

        //Headline
        dienstleistungsTabelle.addCell("Nr");
        dienstleistungsTabelle.addCell("Bezeichnung");
        dienstleistungsTabelle.addCell("Menge");
        dienstleistungsTabelle.addCell("Eh.");
        dienstleistungsTabelle.addCell("E-Preis");
        dienstleistungsTabelle.addCell("USt");
        dienstleistungsTabelle.addCell("Netto");

        int rechnungsPositionNr = 1;

        for (Auftragsposition auftragsposition : auftragspositionList) {
            dienstleistungsTabelle.addCell(Integer.toString(rechnungsPositionNr));
            dienstleistungsTabelle.addCell(auftragsposition.getDlName());
            dienstleistungsTabelle.addCell(new BigDecimal(auftragsposition.getMenge()).toString());
            dienstleistungsTabelle.addCell("Pau");
            dienstleistungsTabelle.addCell(auftragsposition.getCurrentPreisDl().toString());
            dienstleistungsTabelle.addCell("0 %");
            dienstleistungsTabelle.addCell(auftragsposition.multiplyMengeAndCurrentPreis().toString());

            summe = summe.add(auftragsposition.multiplyMengeAndCurrentPreis());
            rechnungsPositionNr++;
        }

//Tabelle für Brutto und Netto Summe
        PdfTabellenCreator summeBruttoNettoTabelle = new PdfTabellenCreator(contentStream);
        int[] cellWith2 = {80, 50, 50};

        summeBruttoNettoTabelle.setTableFont(PDType1Font.HELVETICA_BOLD, 9, Color.black);
        summeBruttoNettoTabelle.setTable(cellWith2, 10, 375, dienstleistungsTabelle.getyPosition());
        summeBruttoNettoTabelle.addCellSummeTable("Nettosumme");
        summeBruttoNettoTabelle.addCellSummeTable("EUR");
        summeBruttoNettoTabelle.addCellSummeTable(summe.toString());

        summeBruttoNettoTabelle.setTableFont(font, fontSize, Color.black);
        summeBruttoNettoTabelle.addCellSummeTable("Umsatzsteuer");
        summeBruttoNettoTabelle.addCellSummeTable("0 %");
        summeBruttoNettoTabelle.addCellSummeTable("0,00");

        summeBruttoNettoTabelle.setTableFont(PDType1Font.HELVETICA_BOLD, 9, Color.black);
        summeBruttoNettoTabelle.addCellSummeTable("Bruttosumme");
        summeBruttoNettoTabelle.addCellSummeTable("EUR");
        summeBruttoNettoTabelle.addCellSummeTable(summe.toString());

        //Linie am ende der Tabelle
        addSingleLine(contentStream, "_______________________________________________________" +
                        "_______________________________________________"
                , 40, summeBruttoNettoTabelle.getyPosition() + 20, font, fontSize, textColor);

        //Zahlungsinfos
        String[] zahlungsInfos = {"Zahlungsbedingung: Betrag dankend in Bar erhalten.", "", "Unsere Kontoinformation"
                , "Institut: BAWAG PSK", "Inhaber: " + rechnungsstellerName, "BIC: " + BIC, "IBAN: " + IBAN};
        addMultiline(contentStream, zahlungsInfos, 10.0f, 40, 350, font, fontSize, textColor);

        //URL
        String url = "www.dogsitteratwork.at";
        PDActionURI action = new PDActionURI();
        action.setURI(url);

        String[] infos = {"Bequem die Zahlung mit Ihrer Smartphone-", "BankingApp beauftragen!", ""
                , "Vielen Dank für Ihr Vertrauen! Weitere Informationen finden Sie auf unserer Homepage: " + url};

        addMultiline(contentStream, infos, 10.0f, 40, 165, font, fontSize, textColor);

        //Seitennummerierung
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(pageWidth / 2, 85);
        contentStream.showText("1/" + document.getNumberOfPages()); //hardcode ersetzen
        contentStream.endText();

        //Fusszeile
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(60, 55);
        contentStream.showText("Andreas Hoffleit - Dogsitter at Work | BIC: BAWAATWW | IBAN: AT83 6000 0000 7841 3942 | Gerichtsstandort: Hollabrunn");
        contentStream.endText();

        //QR-Code
        // https://www.mysamplecode.com/2019/05/generate-barcodes-in-pdf-document.html
        BufferedImage bufferedImage = QRgenerator.generate(rechnungsstellerName, BIC, IBAN, summe, "Rechnungsnr.: " + auftrag.getRechnungsnummer()); //rechnungsnummer hardcode ersetzen
        PDImageXObject qrImage = JPEGFactory.createFromImage(document, bufferedImage);
        contentStream.drawImage(qrImage, 50, 180, 100, 100);

        //Firmenlogo  -speicherpfad
        //PDImageXObject logo = PDImageXObject.createFromFile(ClassLoader.getSystemResource("Firmenlogo.png").getFile(),document); //ist das ein absoluter pfad?
        PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/Firmenlogo.png", document);

        //Logo
        contentStream.drawImage(logo, pageWidth - 180, pageHeight - 130, 135, 103);
        contentStream.close(); //stream-ende

        //Documentinfos
        PDDocumentInformation documentInfo = new PDDocumentInformation(); //notwendige docinfos adden //refferenz auf pdfbox
        //documentInfo.setTitle();           //titel
        documentInfo.setCreationDate(Calendar.getInstance());

        //Outputstream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //speichern
        //document.save(ordnerPfad+"\\"+dateiname+".pdf");
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }


    @Transactional
    public byte[] createPdfWithMultipleAuftragIds(List<Long> auftragIds, String faelligkeitsdatum, int rechnungsnummer) throws IOException, WriterException //verschlüsseln?  berechtigungen?
    {
        LocalDate rechnungsdatum = new DateTimeFactory().today();
        ArrayList<Auftragsposition> auftragspositionList = new ArrayList<>();
        ArrayList<Auftragsposition> auftragspositionListSorted = new ArrayList<>();

        BigDecimal summe = new BigDecimal("0.00");
        Kunde kunde = kundeRepository.findById(auftragIds.get(0)).get();

        for (Long auftragId : auftragIds) {
            auftragspositionList.addAll(auftragspositionRepository.findAuftragspositionByAuftragId(auftragId));
        }

        //sortierte Liste
        auftragspositionListSorted = groupByDienstleistungId(auftragspositionList);


        //erstellung des Dokuments
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDFont font = PDType1Font.HELVETICA;
        int fontSize = 9;
        Color textColor = Color.black;
        int pageWidth = (int) page.getTrimBox().getWidth();
        int pageHeight = (int) page.getTrimBox().getHeight();

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        List<String> kundenDaten = fillKundeArray(kunde);

        //Kundendaten
        addMultiline(contentStream, kundenDaten, 10.0f, 75, pageHeight - 220, font, fontSize, textColor);

        //Beginn der Dientleistungstabelle //dynamisch machen
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
        contentStream.newLineAtOffset(40, pageHeight - 300);
        contentStream.showText("Rechnung");
        contentStream.newLine();
        contentStream.setFont(font, fontSize);
        contentStream.newLine();
        contentStream.showText("Wir erlauben uns, folgende Leistungen in Rechnung zu stellen:");
        contentStream.endText();

//        Rechnungs Info (rechts)
        String[] rechnungstellerDaten = {rechnungsstellerName, "Dein Dogsitter", rechnungsstellerAdresse, rechnungsstellerPLZundOrt, "", "", ""
                , "Rechnungsdatum: " + rechnungsdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), "Fälligkeitsdatum: " + faelligkeitsdatum.trim()
                , "Kundennr.: " + kunde.getKundennummer().trim(), "Rechnungsnr.: " + rechnungsnummer};

        addMultilineRightAligned(contentStream, rechnungstellerDaten, pageWidth - 110,
                pageHeight - 150, font, fontSize, textColor);

        //Rechnungspositionen Tabelle

        //linie an Tabellenanfang
        addSingleLine(contentStream, "_______________________________________________________" +
                        "_______________________________________________"
                , 40, pageHeight - 350, font, fontSize, textColor);

        PdfTabellenCreator dienstleistungsTabelle = new PdfTabellenCreator(contentStream);
        int[] cellWith = {30, 160, 60, 120, 60, 50, 50};
        dienstleistungsTabelle.setTable(cellWith, 15, 25, pageHeight - 360);

        //Headline
        dienstleistungsTabelle.addCell("Nr");
        dienstleistungsTabelle.addCell("Bezeichnung");
        dienstleistungsTabelle.addCell("Menge");
        dienstleistungsTabelle.addCell("Eh.");
        dienstleistungsTabelle.addCell("E-Preis");
        dienstleistungsTabelle.addCell("USt");
        dienstleistungsTabelle.addCell("Netto");


        int rechnungsPositionNr = 1;

        for (Auftragsposition auftragsposition : auftragspositionListSorted) {
            dienstleistungsTabelle.addCell(Integer.toString(rechnungsPositionNr));
            dienstleistungsTabelle.addCell(auftragsposition.getDlName());
            dienstleistungsTabelle.addCell(new BigDecimal(auftragsposition.getMenge()).toString());
            dienstleistungsTabelle.addCell("Pau");
            dienstleistungsTabelle.addCell(auftragsposition.getCurrentPreisDl().toString());
            dienstleistungsTabelle.addCell("0 %");
            dienstleistungsTabelle.addCell(auftragsposition.multiplyMengeAndCurrentPreis().toString());

            summe = summe.add(auftragsposition.multiplyMengeAndCurrentPreis());

            rechnungsPositionNr++;
        }

//Tabelle für Brutto und Netto Summe
        PdfTabellenCreator summeBruttoNettoTabelle = new PdfTabellenCreator(contentStream);
        int[] cellWith2 = {80, 50, 50};

        summeBruttoNettoTabelle.setTableFont(PDType1Font.HELVETICA_BOLD, 9, Color.black);
        summeBruttoNettoTabelle.setTable(cellWith2, 10, 375, dienstleistungsTabelle.getyPosition());
        summeBruttoNettoTabelle.addCellSummeTable("Nettosumme");
        summeBruttoNettoTabelle.addCellSummeTable("EUR");
        summeBruttoNettoTabelle.addCellSummeTable(summe.toString());

        summeBruttoNettoTabelle.setTableFont(font, fontSize, Color.black);
        summeBruttoNettoTabelle.addCellSummeTable("Umsatzsteuer");
        summeBruttoNettoTabelle.addCellSummeTable("0 %");
        summeBruttoNettoTabelle.addCellSummeTable("0,00");

        summeBruttoNettoTabelle.setTableFont(PDType1Font.HELVETICA_BOLD, 9, Color.black);
        summeBruttoNettoTabelle.addCellSummeTable("Bruttosumme");
        summeBruttoNettoTabelle.addCellSummeTable("EUR");
        summeBruttoNettoTabelle.addCellSummeTable(summe.toString());

        //Linie am ende der Tabelle
        addSingleLine(contentStream, "_______________________________________________________" +
                        "_______________________________________________"
                , 40, summeBruttoNettoTabelle.getyPosition() + 20, font, fontSize, textColor);

        //Zahlungsinfos
        String[] zahlungsInfos = {"Zahlungsbedingung: Betrag dankend in Bar erhalten.", "", "Unsere Kontoinformation"
                , "Institut: BAWAG PSK", "Inhaber: " + rechnungsstellerName, "BIC: " + BIC, "IBAN: " + IBAN};
        addMultiline(contentStream, zahlungsInfos, 10.0f, 40, 350, font, fontSize, textColor);

        //URL
        String url = "www.dogsitteratwork.at";
        PDActionURI action = new PDActionURI();
        action.setURI(url);

        String[] infos = {"Bequem die Zahlung mit Ihrer Smartphone-", "BankingApp beauftragen!", ""
                , "Vielen Dank für Ihr Vertrauen! Weitere Informationen finden Sie auf unserer Homepage: " + url};

        addMultiline(contentStream, infos, 10.0f, 40, 165, font, fontSize, textColor);

        //Seitennummerierung
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(pageWidth / 2, 85);
        contentStream.showText("1/" + document.getNumberOfPages()); //hardcode ersetzen
        contentStream.endText();

        //Fusszeile
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(60, 55);
        contentStream.showText("Andreas Hoffleit - Dogsitter at Work | BIC: BAWAATWW | IBAN: AT83 6000 0000 7841 3942 | Gerichtsstandort: Hollabrunn");
        contentStream.endText();

        //QR-Code
        // https://www.mysamplecode.com/2019/05/generate-barcodes-in-pdf-document.html
        BufferedImage bufferedImage = QRgenerator.generate(rechnungsstellerName, BIC, IBAN, summe, "Rechnungsnr.: " + rechnungsnummer); //rechnungsnummer hardcode ersetzen
        PDImageXObject qrImage = JPEGFactory.createFromImage(document, bufferedImage);
        contentStream.drawImage(qrImage, 50, 180, 100, 100);

        //Firmenlogo  -speicherpfad
        PDImageXObject logo = PDImageXObject.createFromFile(ClassLoader.getSystemResource("Firmenlogo.png").getFile(), document); //ist das ein absoluter pfad?

        //Logo
        contentStream.drawImage(logo, pageWidth - 180, pageHeight - 130, 135, 103);
        contentStream.close(); //stream-ende

        //Documentinfos
        PDDocumentInformation documentInfo = new PDDocumentInformation(); //notwendige docinfos adden //refferenz auf pdfbox
        //documentInfo.setTitle();           //titel
        documentInfo.setCreationDate(Calendar.getInstance());

        //Outputstream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        //speichern
        document.save(outputStream);
        //document.save(ordnerPfad+"\\"+dateiname+".pdf");
        document.close();

        return outputStream.toByteArray();

    }


    public ArrayList<Auftragsposition> groupByDienstleistungId(List<Auftragsposition> positions) {
        Map<Long, Auftragsposition> map = new HashMap<>();
        for (Auftragsposition position : positions) {
            Auftragsposition existingPosition = map.get(position.getDienstleistungId());
            if (existingPosition == null) {
                map.put(position.getDienstleistungId(), new Auftragsposition(position.getDienstleistungId(), position.getMenge(), position.getCurrentPreisDl(), position.getDlName()));
            } else {
                existingPosition = new Auftragsposition(existingPosition.getDienstleistungId(), existingPosition.getMenge() + position.getMenge(), existingPosition.getCurrentPreisDl(), existingPosition.getDlName());
                map.put(existingPosition.getDienstleistungId(), existingPosition);
            }
        }
        return new ArrayList<>(map.values());
    }

    private void addSingleLine(PDPageContentStream contentStream, String text, int xPosition, int yPosition, PDFont font, float fontsize, Color color) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontsize);
        contentStream.setNonStrokingColor(color);
        contentStream.newLineAtOffset(xPosition, yPosition);
        contentStream.showText(text);
        contentStream.endText();

    }

    private void addMultiline(PDPageContentStream contentStream, List<String> textArray, float leading, int xPosition, int yPosition, PDFont font, float fontsize, Color color) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontsize);
        contentStream.setNonStrokingColor(color);
        contentStream.setLeading(leading);
        contentStream.newLineAtOffset(xPosition, yPosition);

        for (String text : textArray) {
            contentStream.showText(text);
            contentStream.newLine();
        }
        contentStream.endText();
    }

    private void addMultiline(PDPageContentStream contentStream, String[] textArray, float leading, int xPosition, int yPosition, PDFont font, float fontsize, Color color) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontsize);
        contentStream.setNonStrokingColor(color);
        contentStream.setLeading(leading);
        contentStream.newLineAtOffset(xPosition, yPosition);

        for (String text : textArray) {
            contentStream.showText(text);
            contentStream.newLine();
        }
        contentStream.endText();
    }

    private void addMultilineRightAligned(PDPageContentStream contentStream, List<String> textArray, int xPosition, int yPosition, PDFont font, float fontsize, Color color) throws IOException {
        int counter = 0;
        int xEnd = xPosition + (int) getTextWidth(textArray.get(0), font, fontsize);
        for (String text : textArray) {
            if (counter == 0) {
                addSingleLine(contentStream, text, xPosition, yPosition, font, fontsize, color);
                yPosition = yPosition - 10;
            } else {
                int textWidth = (int) getTextWidth(text, font, fontsize);
                xPosition = xEnd - textWidth;
                addSingleLine(contentStream, text, xPosition, yPosition, font, fontsize, color);
                yPosition = yPosition - 10;
            }
            counter++;
        }
    }

    private void addMultilineRightAligned(PDPageContentStream contentStream, String[] textArray, int xPosition, int yPosition, PDFont font, float fontsize, Color color) throws IOException {
        int counter = 0;
        int xEnd = xPosition + (int) getTextWidth(textArray[0], font, fontsize);
        for (String text : textArray) {
            if (counter == 0) {
                addSingleLine(contentStream, text, xPosition, yPosition, font, fontsize, color);
                yPosition = yPosition - 10;
            } else {
                int textWidth = (int) getTextWidth(text, font, fontsize);
                xPosition = xEnd - textWidth;

                addSingleLine(contentStream, text, xPosition, yPosition, font, fontsize, color);

                yPosition = yPosition - 10;
            }
            counter++;
        }
    }

    private float getTextWidth(String text, PDFont font, float fontsize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontsize;
    }

    private static List<String> fillKundeArray(Kunde kunde) {
        ArrayList<String> kundenDaten = new ArrayList<>();
        kundenDaten.add(kunde.getAnrede());
        kundenDaten.add(kunde.getVorname().trim() + " " + kunde.getNachname().trim());
        kundenDaten.add(kunde.getStrasse().trim());
        kundenDaten.add(kunde.getPlz() + " " + kunde.getOrt().trim());
        kundenDaten.add(kunde.getLand().trim());

        return kundenDaten;
    }


}
