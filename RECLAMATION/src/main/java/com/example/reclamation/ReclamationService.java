package com.example.reclamation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ReclamationService {

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Autowired
    private ReclamationRepository reclamationRepository;
    @Autowired
    private EmailSmsService smsService;

    // Ajouter une réclamation
    public Reclamation addReclamation(Reclamation reclamation) {
        return reclamationRepository.save(reclamation);
    }


// Update the status of a reclamation and send SMS
    public Reclamation updateReclamation(Long id, Reclamation newReclamation) {
        Optional<Reclamation> optionalReclamation = reclamationRepository.findById(id);
        if (optionalReclamation.isPresent()) {
            Reclamation existingReclamation = optionalReclamation.get();
            existingReclamation.setClientNom(newReclamation.getClientNom());
            existingReclamation.setProduit(newReclamation.getProduit());
            existingReclamation.setMessage(newReclamation.getMessage());
            existingReclamation.setStatut(newReclamation.getStatut());

            // Send SMS when status changes
            if (!existingReclamation.getStatut().equals(newReclamation.getStatut())) {
                smsService.sendSms(existingReclamation.getPhoneNumber(),
                        "Your reclamation status has been updated to: " + newReclamation.getStatut());
            }

            return reclamationRepository.save(existingReclamation);
        }
        return null;
    }

    // Supprimer une réclamation
    public String deleteReclamation(Long id) {
        if (reclamationRepository.existsById(id)) {
            reclamationRepository.deleteById(id);
            return "Réclamation supprimée avec succès.";
        }
        return "Réclamation introuvable.";
    }

    // Afficher toutes les réclamations
    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAll();
    }

    public Reclamation addReclamationWithFile(Reclamation reclamation, MultipartFile file) {
        try {
            // Ensure the upload directory exists
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // Create the directory if it doesn't exist
            }

            // Save the file
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(uploadDir + File.separator + fileName);
            Files.write(path, file.getBytes());

            // Set the file name in the reclamation entity
            reclamation.setFileName(fileName);

            // Save to the database
            return reclamationRepository.save(reclamation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //search by client name / product

    public List<Reclamation> searchByClientNomOrProduit(String keyword) {
        return reclamationRepository.findByClientNomContainingIgnoreCaseOrProduitContainingIgnoreCase(keyword, keyword);
    }
    public List<Reclamation> filterByStatus(String status) {
        return reclamationRepository.findByStatutContainingIgnoreCase(status);
    }


}

