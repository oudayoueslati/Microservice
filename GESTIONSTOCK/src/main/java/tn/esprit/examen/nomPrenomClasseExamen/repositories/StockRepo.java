package tn.esprit.examen.nomPrenomClasseExamen.repositories;


import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepo extends JpaRepository<Stock, Integer> {

    List<Stock> findByStockNameContainingIgnoreCase(String s);
}
