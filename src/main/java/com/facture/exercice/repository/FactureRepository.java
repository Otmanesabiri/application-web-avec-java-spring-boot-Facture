package com.facture.exercice.repository;

import com.facture.exercice.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour les opérations CRUD sur les factures
 */
@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    
    /**
     * Recherche les factures d'un client spécifique
     */
    List<Facture> findByClientId(Long clientId);
    
    /**
     * Recherche les factures par date
     */
    List<Facture> findByDate(LocalDate date);
    
    /**
     * Recherche les factures entre deux dates
     */
    List<Facture> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);
    
    /**
     * Recherche les factures d'un client entre deux dates
     */
    @Query("SELECT i FROM Facture i WHERE i.client.id = :clientId AND i.date BETWEEN :dateDebut AND :dateFin")
    List<Facture> findByClientIdAndDateBetween(
        @Param("clientId") Long clientId, 
        @Param("dateDebut") LocalDate dateDebut, 
        @Param("dateFin") LocalDate dateFin
    );
}