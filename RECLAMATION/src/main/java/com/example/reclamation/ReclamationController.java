package com.example.reclamation;

import com.example.reclamation.EmailSmsService;
import com.example.reclamation.Reclamation;
import com.example.reclamation.ReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.File;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {

    @Autowired
    private ReclamationService reclamationService;
   // @Autowired
    // private EmailSmsService emailSmsService;

    @PostMapping("add")
    public ResponseEntity<Reclamation> createReclamation(@RequestBody Reclamation reclamation) {
        // Save the reclamation
        Reclamation savedReclamation = reclamationService.addReclamation(reclamation);

        // Send email notification
       // String adminEmail = "chamekheya1@gmail.com";
        //String emailMessage = "New reclamation added: " + savedReclamation.getClientNom() + " - " + savedReclamation.getProduit();
        //emailSmsService.sendEmail(adminEmail, "New Reclamation Notification", emailMessage);

        // Send SMS notification (using email-to-SMS gateway)
       // String adminPhoneNumber = "93377210";
        //String smsMessage = "New reclamation added: " + savedReclamation.getClientNom() + " - " + savedReclamation.getProduit();
        //emailSmsService.sendSms(adminPhoneNumber, smsMessage);

        return ResponseEntity.ok(savedReclamation);
    }

    // Ajouter une réclamation avec un fichier
    @PostMapping("/addWithFile")
    public ResponseEntity<Reclamation> createReclamationWithFile(
            @RequestParam("clientNom") String clientNom,
            @RequestParam("produit") String produit,
            @RequestParam("message") String message,
            @RequestParam("statut") String statut,
            @RequestParam("file") MultipartFile file,
            @RequestParam("phoneNumber") String phoneNumber){


        Reclamation reclamation = new Reclamation(clientNom, produit, message, statut, phoneNumber);
        Reclamation savedReclamation = reclamationService.addReclamationWithFile(reclamation, file);

        return ResponseEntity.ok(savedReclamation);
    }

    //see the file
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    // Modifier une réclamation
    @PutMapping("/update/{id}")
    public ResponseEntity<Reclamation> updateReclamation(@PathVariable Long id, @RequestBody Reclamation reclamation) {
        return ResponseEntity.ok(reclamationService.updateReclamation(id, reclamation));
    }

    // Supprimer une réclamation
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReclamation(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.deleteReclamation(id));
    }

    // Afficher toutes les réclamations
    @GetMapping
    public ResponseEntity<List<Reclamation>> getAllReclamations() {
        return ResponseEntity.ok(reclamationService.getAllReclamations());
    }

    // Search by client name or product
    @GetMapping("/search")
    public ResponseEntity<List<Reclamation>> searchReclamations(@RequestParam("q") String query) {
        List<Reclamation> results = reclamationService.searchByClientNomOrProduit(query);
        return ResponseEntity.ok(results);
    }

    // Filter by status
    @GetMapping("/filterByStatus")
    public ResponseEntity<List<Reclamation>> filterReclamationsByStatus(
            @RequestParam("status") String status) {

        List<Reclamation> results = reclamationService.filterByStatus(status);
        return ResponseEntity.ok(results);
    }


}

