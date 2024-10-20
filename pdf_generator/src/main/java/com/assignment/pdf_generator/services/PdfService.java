package com.assignment.pdf_generator.services;

import com.assignment.pdf_generator.model.Invoice;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PdfService {
    private static long uniq_id = 0;


    //fetching location to store the pdf file in local storge
    @Value("${pdf.storage.location}")
    private String pdfStorageLocation;

    private final SpringTemplateEngine templateEngine;

    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generatePdf(Invoice invoice) throws IOException {
        // Function calling for creating unique file name
        String pdfFilename = getFileNameForInvoice(invoice);

        // Create a file object, and check if pdf is already in the file with same name.
        File pdfFile = new File(pdfStorageLocation + pdfFilename);
        if (pdfFile.exists()) {
            return pdfFilename;  // Return existing file if data matches
        }

        // If not present at location, generate pdf content
        Context context = new Context();
        context.setVariable("invoice", invoice);
        String htmlContent = templateEngine.process("invoice_template", context);

        // Convert html file to PDF
        FileOutputStream fos = new FileOutputStream(pdfFile);
        HtmlConverter.convertToPdf(htmlContent, fos);

        fos.close();
        return pdfFilename;
    }

    //Geting file content of prestored file
    public byte[] getPdfContent(String pdfFilename) throws IOException {
        return Files.readAllBytes(Paths.get(pdfStorageLocation + pdfFilename));
    }

    //generating unique file name
    private String getFileNameForInvoice(Invoice invoice) {
        // Create a unique filename using the buyer name and a UUID to prevent collisions
        int hc = invoice.hashCode();

        return invoice.getBuyer().replaceAll("\\s+", "_")
                + "_" +invoice.getBuyerGstin().replaceAll("\\s+" , "_")
                + "_" + Integer.toString(hc) + ".pdf";
    }
}

