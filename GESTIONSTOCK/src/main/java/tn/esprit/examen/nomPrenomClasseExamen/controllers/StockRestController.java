package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.http.MediaType;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ChatRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ChatResponseDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Stock;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.StockRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.services.CohereService;
import tn.esprit.examen.nomPrenomClasseExamen.services.PdfService;
import tn.esprit.examen.nomPrenomClasseExamen.services.QrCodeService;
import tn.esprit.examen.nomPrenomClasseExamen.services.StockService;



import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/stocks")
public class StockRestController {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private PdfService pdfService;


    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private CohereService cohereService;

    // 🔹 Ajouter un stock
    @PostMapping("/addStock")
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        return new ResponseEntity<>(stockService.addStock(stock), HttpStatus.CREATED);
    }

    // 🔹 Mettre à jour un stock
    @PutMapping("/updateStock/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable int id, @RequestBody Stock stock) {
        return new ResponseEntity<>(stockService.updateStock(id, stock), HttpStatus.OK);
    }

    // 🔹 Supprimer un stock
    @DeleteMapping("/deleteStock/{id}")
    public ResponseEntity<String> deleteStock(@PathVariable int id) {
        return new ResponseEntity<>(stockService.deleteStock(id), HttpStatus.OK);
    }

    // 🔹 Récupérer tous les stocks
    @GetMapping("/getAllStocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return new ResponseEntity<>(stockService.getAllStocks(), HttpStatus.OK);
    }

    // 🔹 Récupérer un stock par ID
    @GetMapping("/getStockById/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable int id) {
        Optional<Stock> stock = stockService.getStockById(id);
        return stock.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    // 🔹 Compter les stocks
    @GetMapping("/count")
    public ResponseEntity<Long> countStocks() {
        return new ResponseEntity<>(stockService.countStocks(), HttpStatus.OK);
    }
    // 🔹 Rechercher des stocks par nom
    @GetMapping("/search/{name}")
    public ResponseEntity<List<Stock>> searchStocks(@PathVariable String name) {
        List<Stock> stocks = stockService.searchStocks(name);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }
    // 🔹 Trier les stocks par prix (ordre décroissant par défaut)
    @GetMapping("/sortByPrice")
    public ResponseEntity<List<Stock>> sortStocksByPrice() {
        List<Stock> stocks = stockService.sortStocksByPriceDesc();
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }
    // 🔹 Trier les stocks par prix
    @GetMapping("/sortByPriceByOrder/{order}")
    public ResponseEntity<List<Stock>> sortStocksByPrice(@PathVariable String order) {
        List<Stock> stocks = stockService.sortStocksByPrice(order);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }
    // 🔹 Generer un qr code pour chaque stock enregistre dans la BDD
    @GetMapping(value = "/qr/{id}")
    public ResponseEntity<byte[]> generateQrCode(@PathVariable int id) {

        System.out.println("🎯 Called generateQrCode with id: " + id); // <---- ici


        Optional<Stock> stockOptional = stockRepo.findById(id);
        if (stockOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stock stock = stockOptional.get();
        String text = "Stock ID: " + stock.getIdStock() + ", Name: " + stock.getStockName() + ", Quantity: " + stock.getStockQty();

        try {
            byte[] image = qrCodeService.generateQrCode(text, 250, 250);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 🔹 Generer un PDF code pour chaque stock enregistre dans la BDD
    @GetMapping(value = "/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateStockPdf(@PathVariable int id) {
        Optional<Stock> stockOptional = stockRepo.findById(id);
        if (stockOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stock stock = stockOptional.get();
        try {
            byte[] pdf = pdfService.generateStockPdf(stock);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=stock_" + id + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    // 🔹 Chatbot
    @PostMapping("/chatbot/ask")
    public ResponseEntity<ChatResponseDTO> askChatbot(@RequestBody ChatRequestDTO chatRequest) {
        String response = cohereService.getChatbotResponse(chatRequest.getText());
        return ResponseEntity.ok(new ChatResponseDTO(response));
    }


}
