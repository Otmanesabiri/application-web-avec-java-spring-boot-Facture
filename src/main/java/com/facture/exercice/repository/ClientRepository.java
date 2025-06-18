package com.facture.exercice.repository;


import com.facture.exercice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository pour les opérations CRUD sur les clients
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    /**
     * Recherche un client par son email
     */
    Optional<Client> findByEmail(String email);
    
    /**
     * Recherche un client par son SIRET
     */
    Optional<Client> findBySiret(String siret);
    
    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Vérifie si un SIRET existe déjà
     */
    boolean existsBySiret(String siret);
}