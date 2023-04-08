package com.example.hundkatzgarten_backend.service.pdfCreation;


//https://www.javatpoint.com/generating-qr-code-in-java

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class QRgenerator {

//    public static void main(String[] args)
//    {
//        try
//        {
//            //generate(generateEPCQRtoString("Max Mustermann","DE33100205000001194700",20.50,"Re.Nr.: 2460" ));
//            generate("https://www.google.at/");
//        } catch (WriterException e)
//        {
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//}
//    public static void generate(String data) throws WriterException, IOException
//    {
//        if (data != null)
//        {
//            BitMatrix matrix = new MultiFormatWriter()
//                    .encode(new String(data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat
//                            .QR_CODE, 400, 400);
//            //BufferedImage qr = MatrixToImageWriter.toBufferedImage(matrix);
//
//            MatrixToImageWriter.writeToPath(matrix, "png", new File("D:\\Kolleg\\Diplomprojekt\\Tests\\QRs\\QRcode2.png").toPath());
//        } else
//            System.out.println("Null-Ref, fuer generate(String data)");
//    }

    public static BufferedImage generate(String data) throws WriterException, IOException {
        if (data != null) {
            BitMatrix matrix = new MultiFormatWriter() //error correction code?
                    .encode(new String(data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat
                            .QR_CODE, 400, 400);
            BufferedImage qr = MatrixToImageWriter.toBufferedImage(matrix);
            return qr;
        } else
            System.out.println("Null-Ref, fuer generate(String data)"); //durch exception erstetzen
        return null; //leeres BufferedImage als default return?
    }

    //parameter check
    public static BufferedImage generate(String empfaenger, String bic, String iban, BigDecimal betrag, String zahlungsreferenz) throws IOException, WriterException {
        return generate(generateEPCQRtoString(empfaenger, bic, iban, betrag, zahlungsreferenz));
    }


    public static String generateEPCQRtoString(String empfaenger, String bic, String iban, BigDecimal betrag, String zahlungsreferenz) //private setzen?
    {
        String output = ""; //vollständiger String

        if (empfaenger != null && iban != null && betrag.longValue() > 0 && zahlungsreferenz != null) //bic auf null pfrüfen
        {
            String serviceTag = "BCD"; //must have
            String version = "001"; //must have
            String zeichencodierung = "1"; //must have
            String identification = "SCT"; //must have
            String BIC = bic; //optional
            String zahlungsempfaenger = empfaenger.trim(); //must have
            String IBAN = iban; //must have
            String zahlungsbetrag = "EUR" + betrag; //kommastellen lösung finden----------- //BigDecimal statt double
            String zweck = ""; //4 stelliger code
            String referenz = zahlungsreferenz.trim();
            String hinweis = ""; //optional

            output = serviceTag + "\n" + version + "\n" + zeichencodierung + "\n"
                    + identification + "\n" + BIC + "\n" + zahlungsempfaenger + "\n"
                    + IBAN + "\n" + zahlungsbetrag + "\n" + zweck + "\n"
                    + referenz + "\n" + hinweis;
        } else
            System.out.println("Null-Ref. oder ungueltige Eingabe bei generateEPCQR()"); //durch exception erstetzen
        return output;
    }


}
