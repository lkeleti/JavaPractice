package dev.lkeleti.invotraxapp.service;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import dev.lkeleti.invotraxapp.model.Invoice;
import dev.lkeleti.invotraxapp.model.InvoiceItem;
import dev.lkeleti.invotraxapp.model.Partner;
import dev.lkeleti.invotraxapp.model.ZipCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Cell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class PdfInvoiceGeneratorService {
    public byte[] generateInvoicePdf(Invoice invoice, ZipCode zipCodeSeller, ZipCode zipCodeBuyer,  String password) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(
                        password.getBytes(),
                        password.getBytes(),
                        EncryptionConstants.ALLOW_PRINTING,
                        EncryptionConstants.ENCRYPTION_AES_128
                );

        PdfWriter writer = new PdfWriter(byteStream, writerProperties);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            var font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            document.setFont(font);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create pdf document", e);
        }

        // Számla címe
        Paragraph title = new Paragraph("SZÁMLA")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Eladó és Vevő
        Table parties = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));

        parties.addCell(createNoBorderCell("Eladó:\n" + formatPartner(invoice.getSeller(), zipCodeSeller)));
        parties.addCell(createNoBorderCell("Vevő:\n" + formatPartner(invoice.getBuyer(), zipCodeBuyer)));

        document.add(parties);

        // Számlaadatok
        Table invoiceInfo = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .setWidth(UnitValue.createPercentValue(100));
        invoiceInfo.addCell(createBoldCell("Számla száma:"));
        invoiceInfo.addCell(new Cell().add(new Paragraph(invoice.getInvoiceNumber())));
        invoiceInfo.addCell(createBoldCell("Teljesítés kelte:"));
        invoiceInfo.addCell(new Cell().add(new Paragraph(invoice.getFulfillmentAt().toString())));
        invoiceInfo.addCell(createBoldCell("Számla kelte:"));
        invoiceInfo.addCell(new Cell().add(new Paragraph(invoice.getIssuedAt().toString())));
        invoiceInfo.addCell(createBoldCell("Fizetési határidő:"));
        invoiceInfo.addCell(new Cell().add(new Paragraph(invoice.getDueDate().toString())));
        invoiceInfo.addCell(createBoldCell("Fizetési mód:"));
        invoiceInfo.addCell(new Cell().add(new Paragraph(invoice.getPaymentMethod().getName())));
        document.add(invoiceInfo);

        // Tételsorok
        Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{50, 10, 20, 20}))
                .setWidth(UnitValue.createPercentValue(100));

        itemsTable.addHeaderCell("Megnevezés");
        itemsTable.addHeaderCell("Mennyiség");
        itemsTable.addHeaderCell("Nettó érték");
        itemsTable.addHeaderCell("ÁFA");

        for (InvoiceItem item : invoice.getItems()) {
            itemsTable.addCell(item.getProduct().getName());
            itemsTable.addCell(String.valueOf(item.getQuantity()));
            itemsTable.addCell(item.getNetAmount().toString());
            itemsTable.addCell(item.getGrossAmount().subtract(item.getNetAmount()).toString());
        }

        document.add(itemsTable);

        // Nettó összesen
        BigDecimal netTotal = invoice.getItems().stream()
                .map(InvoiceItem::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        document.add(new Paragraph("Nettó összesen: " + netTotal));

        // ÁFA csoportosítás
        Map<String, BigDecimal> vatGroups = invoice.getItems().stream()
                .collect(Collectors.groupingBy(
                        i -> i.getVatRate().getName(),
                        Collectors.reducing(BigDecimal.ZERO, invoiceItem-> invoiceItem.getGrossAmount().subtract(invoiceItem.getNetAmount()), BigDecimal::add)
                ));

        Table vatTable = new Table(UnitValue.createPercentArray(new float[]{80, 20}))
                .setWidth(UnitValue.createPercentValue(100));
        vatTable.addHeaderCell("ÁFA szerinti csoportosítás");
        vatTable.addHeaderCell("ÁFA érték");

        for (var entry : vatGroups.entrySet()) {
            vatTable.addCell(entry.getKey());
            vatTable.addCell(entry.getValue().toString());
        }

        document.add(vatTable);

        // Bruttó összesen
        BigDecimal grossTotal = invoice.getItems().stream()
                .map(InvoiceItem::getGrossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Paragraph grossTotalPara = new Paragraph("Bruttó összesen: " + grossTotal)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(grossTotalPara);

        // Szöveges megjegyzés
        // Üres sor a szövegblokk előtt
        document.add(new Paragraph("\n"));

        // Aláírás és átvétel sor balra és jobbra igazítva egy sorban
        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();
        signatureTable.addCell(new Cell().add(new Paragraph("Átvétel igazolása:").setFontSize(10)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatureTable.addCell(new Cell().add(new Paragraph("Aláírás:").setFontSize(10)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        document.add(signatureTable);

        // Egyéb szabad szöveges tartalom balra igazítva
        document.add(new Paragraph("K ö s z ö n j ü k,  h o g y  n á l u n k  v á s á r o l t !").setFontSize(10));
        document.add(new Paragraph("Késedelmes fizetés esetén - késedelmi kamatként - a mindenkori jegybanki kamat kétszeresét számítjuk fel!").setFontSize(10));
        document.add(new Paragraph("A számla kiegyenlítéséig az áru az ELADÓ tulajdonát képezi.").setFontSize(10));
        document.add(new Paragraph("Amennyiben Ön nem ismeri, vagy rosszul méri fel számítógépe teljesítményét csak saját felelősségére vásároljon szoftvert, vagy alkatrészt, mert ilyen").setFontSize(10));
        document.add(new Paragraph("indokkal terméket nem áll módunkban visszavenni, vagy cserélni.").setFontSize(10));
        document.add(new Paragraph("A TERMÉKEN TALÁLHATÓ AZONOSÍTÓK (MATRICÁK, CIMKÉK) ELTÁVOLÍTÁSA GARANCIAVESZTÉSSEL JÁR!").setFontSize(10));

        // Elválasztó vonal
        document.add(new Paragraph("===============================================================================================================================").setFontSize(10));

        // Példányszám megjegyzés
        document.add(new Paragraph("Készült lapnyomtatóra 1 .példányban. 1.példány").setFontSize(10));
        document.close();
        return byteStream.toByteArray();
    }

    private static String formatPartner(Partner partner, ZipCode zipCode) {
        return partner.getName() + "\n" +
                zipCode.getZip() + " " + zipCode.getCity() + "\n" +
                partner.getStreetName() + " " +
                partner.getStreetType() + " " +
                partner.getHouseNumber() + " " +
                partner.getBuilding() + " " +
                partner.getStaircase() + " " +
                partner.getFloor() + " " +
                partner.getDoor() + " " +
                partner.getLandRegistryNumber() + " " + "\n" +
                "Adószám: " + partner.getTaxNumber();
    }

    private static Cell createNoBorderCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER);
    }

    private static Cell createBoldCell(String text) {
        return new Cell().add(new Paragraph(text).setBold()).setBorder(Border.NO_BORDER);
    }

    public byte[] decryptPdfToMemory(byte[] encryptedPdf, String password) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ReaderProperties readerProperties = new ReaderProperties().setPassword(password.getBytes());

            PdfReader reader = new PdfReader(new ByteArrayInputStream(encryptedPdf), readerProperties);
            PdfWriter writer = new PdfWriter(output);

            PdfDocument pdfDoc = new PdfDocument(reader, writer);
            pdfDoc.close();
        } catch (Exception e) {
          throw new IllegalStateException("Cannot decrypt pdf", e);
        }
        return output.toByteArray();
    }
}