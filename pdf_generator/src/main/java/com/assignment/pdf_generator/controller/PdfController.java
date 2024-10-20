package com.assignment.pdf_generator.controller;

import com.assignment.pdf_generator.model.Invoice;
import com.assignment.pdf_generator.services.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;
    // To check the health of the application
    @GetMapping("health-check")
    public String health_check(){return "All fine with pdf generator";}

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }


    //To generate the pdf using given information
    @PostMapping("/generate-pdf")
    public ResponseEntity<String> generatePdf(@RequestBody Invoice invoice) {
        try {
            String pdfFilename = pdfService.generatePdf(invoice);
            return ResponseEntity.ok("PDF generated successfully: " + pdfFilename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }


    //To download the pdf using pdf name
    @GetMapping("/download/{pdfFilename}")
    public ResponseEntity<StreamingResponseBody> downloadPdf(@PathVariable String pdfFilename) {
        try {
            byte[] pdfContent = pdfService.getPdfContent(pdfFilename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFilename)
                    .body(outputStream -> outputStream.write(pdfContent));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

