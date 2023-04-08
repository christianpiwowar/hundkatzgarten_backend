package com.example.hundkatzgarten_backend.service.pdfCreation;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;

public class PdfTabellenCreator {
    //PDDocument document;
    PDPageContentStream contentStream;
    private int[] colWidths;
    private int cellHeight;
    private int yPosition;
    private int xPosition;
    private int colPosition;
    private int xStartPosition;
    private float fontSize;
    private PDFont font;
    private Color fontcolor;
    private boolean isHeadline;
    private int lastYposition;

    public PdfTabellenCreator(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        this.fontcolor = Color.black;
        this.font = PDType1Font.HELVETICA;
        this.fontSize = 9;
    }

    void setTable(int[] colWidths, int cellHeight, int xPosition, int yPosition) {
        this.colWidths = colWidths;
        this.cellHeight = cellHeight;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.xStartPosition = xPosition;
        this.isHeadline = true;
    }

    void setTableFont(PDFont font, float fontSize, Color fontColor) {
        this.font = font;
        this.fontSize = fontSize;
        this.fontcolor = fontColor;
    }

    void addCell(String text) throws IOException {
        contentStream.setNonStrokingColor(1f);
        contentStream.addRect(xPosition, yPosition, colWidths[colPosition], cellHeight);
        contentStream.beginText();
        contentStream.setNonStrokingColor(fontcolor);
        contentStream.setFont(font, fontSize);

        if (!isHeadline && colPosition == 2 || colPosition == 4 || colPosition == 6) {
            //contentStream.newLineAtOffset(xPosition+(colWidths[colPosition]/2)-(textWidth/2),yPosition+10); //mittig
            contentStream.newLineAtOffset(xPosition + (colWidths[colPosition] - 20) - getTextWidth(text, font, fontSize), yPosition + 10); //rechts
        } else {
            contentStream.newLineAtOffset(xPosition + 20, yPosition + 10); //links
        }

        contentStream.showText(text);
        contentStream.endText();
        xPosition = xPosition + colWidths[colPosition];
        colPosition++;

        if (colPosition == colWidths.length) {
            colPosition = 0;
            xPosition = xStartPosition;
            yPosition -= cellHeight;
            isHeadline = false;
        }
    }

    void addCellSummeTable(String text) throws IOException {
        contentStream.setNonStrokingColor(1f);
        contentStream.addRect(xPosition, yPosition, colWidths[colPosition], cellHeight);
        contentStream.beginText();
        contentStream.setNonStrokingColor(fontcolor);
        contentStream.setFont(font, fontSize);

        if (colPosition == 2) {
            //contentStream.newLineAtOffset(xPosition+(colWidths[colPosition]/2)-(textWidth/2),yPosition+10); //mittig
            contentStream.newLineAtOffset(xPosition + (colWidths[colPosition] - 20) - getTextWidth(text, font, fontSize), yPosition + 10); //rechts
        } else {
            contentStream.newLineAtOffset(xPosition + 20, yPosition + 10); //links
        }

        contentStream.showText(text);
        contentStream.endText();
        xPosition = xPosition + colWidths[colPosition];
        colPosition++;

        if (colPosition == colWidths.length) {
            colPosition = 0;
            xPosition = xStartPosition;
            yPosition -= cellHeight;
            isHeadline = false;
        }
    }

    private float getTextWidth(String text, PDFont font, float fontsize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontsize;
    }


    public int getyPosition() {
        return yPosition;
    }
}


