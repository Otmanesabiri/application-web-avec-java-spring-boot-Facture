package com.facture.exercice.controller;

import com.facture.exercice.dto.ClientDTO;
import com.facture.exercice.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des clients
 */
@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API de gestion des clients")
public class ClientController {
    
    @Autowired
    private ClientService clientService;
    
    /**
     * Récupère la liste de tous les clients
     */
    @GetMapping
    @Operation(summary = "Lister tous les clients", 
               description = "Récupère la liste complète des clients")
    public ResponseEntity<List<ClientDTO>> obtenirTousLesClients() {
        List<ClientDTO> clients = clientService.obtenirTousLesClients();
        return ResponseEntity.ok(clients);
    }
    
    /**
     * Récupère un client par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un client par ID", 
               description = "Récupère les détails d'un client spécifique")
    public ResponseEntity<ClientDTO> obtenirClientParId(@PathVariable Long id) {
        Optional<ClientDTO> client = clientService.obtenirClientParId(id);
        return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crée un nouveau client
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau client", 
               description = "Crée un nouveau client avec les informations fournies")
    public ResponseEntity<?> creerClient(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            ClientDTO nouveauClient = clientService.creerClient(clientDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauClient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour un client existant
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client", 
               description = "Met à jour les informations d'un client existant")
    public ResponseEntity<?> mettreAJourClient(@PathVariable Long id, 
                                              @Valid @RequestBody ClientDTO clientDTO) {
        try {
            Optional<ClientDTO> clientMisAJour = clientService.mettreAJourClient(id, clientDTO);
            return clientMisAJour.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un client
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client", 
               description = "Supprime un client et toutes ses factures associées")
    public ResponseEntity<String> supprimerClient(@PathVariable Long id) {
        boolean supprime = clientService.supprimerClient(id);
        if (supprime) {
            return ResponseEntity.ok("Client supprimé avec succès");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}