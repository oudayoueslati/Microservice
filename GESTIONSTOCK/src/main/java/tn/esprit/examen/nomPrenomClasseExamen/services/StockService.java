package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.data.domain.Sort;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Stock;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.StockRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private MailService mailService;
    @Autowired
    private SmsService smsService;


    public Stock addStock(Stock stock) {
        Stock savedStock = stockRepo.save(stock);

        String subject = "🆕 Nouveau stock ajouté : " + savedStock.getStockName();
        String body = "Un nouveau stock vient d'être ajouté :\n\n" +
                "📦 Nom : " + savedStock.getStockName() + "\n" +
                "🔢 Quantité : " + savedStock.getStockQty() + "\n" +
                "🏷️ Type : " + savedStock.getStockType() + "\n\n" +
                "Merci.";

        // Envoyer le mail
        // mailService.sendStockNotification("trabelsihalim4@gmail.com", subject, body);
        // String sms = "📦 Nouveau stock ajouté : " + savedStock.getStockName() + " (Qte: " + savedStock.getStockQty() + ")";
        // smsService.sendSms("+21623340490", sms);

        return savedStock;
    }

    public Stock updateStock(int id, Stock newStock) {
        Optional<Stock> existingStockOpt = stockRepo.findById(id);
        if (existingStockOpt.isPresent()) {
            Stock existing = existingStockOpt.get();
            existing.setStockName(newStock.getStockName());
            existing.setStockPrice(newStock.getStockPrice());
            existing.setStockQty(newStock.getStockQty());
            existing.setStockType(newStock.getStockType());
            return stockRepo.save(existing);
        } else {
            throw new EntityNotFoundException("Stock avec ID " + id + " non trouvé.");
        }
    }

    public String deleteStock(int id) {
        if (stockRepo.findById(id).isPresent()) {
            stockRepo.deleteById(id);
            return "Stock supprimé.";
        }
        return "Stock introuvable.";
    }
    public List<Stock> searchStocks(String name) {
        return stockRepo.findByStockNameContainingIgnoreCase(
                name != null ? name : ""
        );
    }

    public List<Stock> sortStocksByPriceDesc() {
        return stockRepo.findAll(Sort.by(Sort.Order.desc("stockPrice")));
    }
    public List<Stock> sortStocksByPrice(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return stockRepo.findAll(Sort.by(Sort.Order.desc("stockPrice")));
        } else {
            return stockRepo.findAll(Sort.by(Sort.Order.asc("stockPrice")));
        }
    }

    public long countStocks() {
        return stockRepo.count();
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }

    public Optional<Stock> getStockById(int id) {
        return stockRepo.findById(id);
    }
}
