package com.facture.exercice.controller;


import com.facture.exercice.dto.FactureDTO;
import com.facture.exercice.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des factures
 */
@RestController
@RequestMapping("/api/factures")
@Tag(name = "Factures", description = "API de gestion des factures")
public class FactureController {
    
    @Autowired
    private FactureService FactureService;
    
    /**
     * Récupère la liste de toutes les factures
     */
    @GetMapping
    @Operation(summary = "Lister toutes les factures", 
               description = "Récupère la liste complète des factures")
    public ResponseEntity<List<FactureDTO>> obtenirToutesLesFactures() {
        List<FactureDTO> factures = FactureService.obtenirToutesLesFactures();
        return ResponseEntity.ok(factures);
    }
    
    /**
     * Récupère une facture par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une facture par ID", 
               description = "Récupère les détails d'une facture spécifique")
    public ResponseEntity<FactureDTO> obtenirFactureParId(@PathVariable Long id) {
        Optional<FactureDTO> facture = FactureService.obtenirFactureParId(id);
        return facture.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crée une nouvelle facture
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle facture", 
               description = "Crée une nouvelle facture avec calcul automatique des totaux")
    public ResponseEntity<?> creerFacture(@Valid @RequestBody FactureDTO factureDTO) {
        try {
            FactureDTO nouvelleFacture = FactureService.creerFacture(factureDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleFacture);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Recherche des factures par client (BONUS)
     */
    @GetMapping("/recherche/client/{clientId}")
    @Operation(summary = "Rechercher les factures par client", 
               description = "Récupère toutes les factures d'un client spécifique")
    public ResponseEntity<List<FactureDTO>> rechercherFacturesParClient(@PathVariable Long clientId) {
        List<FactureDTO> factures = FactureService.rechercherFacturesParClient(clientId);
        return ResponseEntity.ok(factures);
    }
    
    /**
     * Recherche des factures par date (BONUS)
     */
    @GetMapping("/recherche/date/{date}")
    @Operation(summary = "Rechercher les factures par date", 
               description = "Récupère toutes les factures d'une date spécifique")
    public ResponseEntity<List<FactureDTO>> rechercherFacturesParDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FactureDTO> factures = FactureService.rechercherFacturesParDate(date);
        return ResponseEntity.ok(factures);
    }
    
    /**
     * Exporte une facture au format JSON
     */
    @GetMapping("/{id}/export")
    @Operation(summary = "Exporter une facture en JSON", 
               description = "Génère un export JSON structuré de la facture")
    public ResponseEntity<?> exporterFactureJSON(@PathVariable Long id) {
        try {
            String jsonExport = FactureService.exporterFactureJSON(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonExport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}