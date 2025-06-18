package com.facture.exercice.controller;


import com.facture.exercice.dto.FactureDTO;
import com.facture.exercice.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Factures", description = "API de gestion des factures - Permet de créer, consulter, rechercher et exporter des factures")
public class FactureController {
    
    @Autowired
    private FactureService FactureService;
    
    /**
     * Récupère la liste de toutes les factures
     */
    @GetMapping
    @Operation(
        summary = "Lister toutes les factures", 
        description = "Récupère la liste complète des factures avec leurs détails (lignes, totaux, etc.)",
        tags = {"Factures"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Liste des factures récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FactureDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erreur interne du serveur",
            content = @Content
        )
    })
    public ResponseEntity<List<FactureDTO>> obtenirToutesLesFactures() {
        List<FactureDTO> factures = FactureService.obtenirToutesLesFactures();
        return ResponseEntity.ok(factures);
    }
    
    /**
     * Récupère une facture par son ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtenir une facture par ID", 
        description = "Récupère les détails complets d'une facture spécifique avec toutes ses lignes et calculs",
        tags = {"Factures"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Facture trouvée et retournée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FactureDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Facture non trouvée avec l'ID spécifié",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de facture invalide",
            content = @Content
        )
    })
    public ResponseEntity<FactureDTO> obtenirFactureParId(
        @Parameter(
            description = "ID unique de la facture à récupérer", 
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @PathVariable Long id) {
        Optional<FactureDTO> facture = FactureService.obtenirFactureParId(id);
        return facture.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crée une nouvelle facture
     */
    @PostMapping
    @Operation(
        summary = "Créer une nouvelle facture", 
        description = "Crée une nouvelle facture avec calcul automatique des totaux (sous-total, TVA, total TTC). Le client doit exister.",
        tags = {"Factures"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Facture créée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FactureDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Données de facture invalides ou client inexistant",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string", example = "Erreur: Client avec l'ID 123 non trouvé")
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Erreur de validation des données",
            content = @Content
        )
    })
    public ResponseEntity<?> creerFacture(
        @Parameter(
            description = "Informations de la facture à créer", 
            required = true,
            schema = @Schema(implementation = FactureDTO.class)
        )
        @Valid @RequestBody FactureDTO factureDTO) {
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
    @Operation(
        summary = "Rechercher les factures par client", 
        description = "Récupère toutes les factures associées à un client spécifique, triées par date de création",
        tags = {"Factures", "Recherche"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Factures du client récupérées avec succès (liste vide si aucune facture)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FactureDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Client non trouvé avec l'ID spécifié",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de client invalide",
            content = @Content
        )
    })
    public ResponseEntity<List<FactureDTO>> rechercherFacturesParClient(
        @Parameter(
            description = "ID unique du client dont on veut récupérer les factures", 
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @PathVariable Long clientId) {
        List<FactureDTO> factures = FactureService.rechercherFacturesParClient(clientId);
        return ResponseEntity.ok(factures);
    }
    
    /**
     * Recherche des factures par date (BONUS)
     */
    @GetMapping("/recherche/date/{date}")
    @Operation(
        summary = "Rechercher les factures par date", 
        description = "Récupère toutes les factures créées à une date spécifique au format ISO (YYYY-MM-DD)",
        tags = {"Factures", "Recherche"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Factures de la date récupérées avec succès (liste vide si aucune facture)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FactureDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Format de date invalide (doit être YYYY-MM-DD)",
            content = @Content
        )
    })
    public ResponseEntity<List<FactureDTO>> rechercherFacturesParDate(
        @Parameter(
            description = "Date de création des factures à rechercher (format: YYYY-MM-DD)", 
            required = true,
            example = "2025-06-18",
            schema = @Schema(type = "string", format = "date", pattern = "^\\d{4}-\\d{2}-\\d{2}$")
        )
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FactureDTO> factures = FactureService.rechercherFacturesParDate(date);
        return ResponseEntity.ok(factures);
    }
    
    /**
     * Exporte une facture au format JSON
     */
    @GetMapping("/{id}/export")
    @Operation(
        summary = "Exporter une facture en JSON", 
        description = "Génère un export JSON structuré de la facture avec toutes ses informations (client, lignes, totaux)",
        tags = {"Factures", "Export"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Export JSON de la facture généré avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string", example = "{\"facture\": {\"id\": 1, \"numero\": \"F-2025-001\", ...}}")
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Facture non trouvée avec l'ID spécifié",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string", example = "Erreur: Facture avec l'ID 123 non trouvée")
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de facture invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erreur lors de la génération de l'export",
            content = @Content
        )
    })
    public ResponseEntity<?> exporterFactureJSON(
        @Parameter(
            description = "ID unique de la facture à exporter", 
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @PathVariable Long id) {
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