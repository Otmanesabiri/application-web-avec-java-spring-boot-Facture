package com.facture.exercice.controller;

import com.facture.exercice.dto.ClientDTO;
import com.facture.exercice.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Clients", description = "API de gestion des clients - Permet de créer, lire, modifier et supprimer des clients")
public class ClientController {
    
    @Autowired
    private ClientService clientService;
    
    /**
     * Récupère la liste de tous les clients
     */
    @GetMapping
    @Operation(
        summary = "Lister tous les clients", 
        description = "Récupère la liste complète des clients enregistrés dans le système",
        tags = {"Clients"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Liste des clients récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erreur interne du serveur",
            content = @Content
        )
    })
    public ResponseEntity<List<ClientDTO>> obtenirTousLesClients() {
        List<ClientDTO> clients = clientService.obtenirTousLesClients();
        return ResponseEntity.ok(clients);
    }
    
    /**
     * Récupère un client par son ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtenir un client par ID", 
        description = "Récupère les détails d'un client spécifique en utilisant son identifiant unique",
        tags = {"Clients"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Client trouvé et retourné avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientDTO.class)
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
    public ResponseEntity<ClientDTO> obtenirClientParId(
        @Parameter(
            description = "ID unique du client à récupérer", 
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @PathVariable Long id) {
        Optional<ClientDTO> client = clientService.obtenirClientParId(id);
        return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crée un nouveau client
     */
    @PostMapping
    @Operation(
        summary = "Créer un nouveau client", 
        description = "Crée un nouveau client avec les informations fournies. Tous les champs obligatoires doivent être renseignés.",
        tags = {"Clients"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Client créé avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Données de client invalides ou client déjà existant",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string", example = "Erreur: Email déjà utilisé")
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Erreur de validation des données",
            content = @Content
        )
    })
    public ResponseEntity<?> creerClient(
        @Parameter(
            description = "Informations du client à créer", 
            required = true,
            schema = @Schema(implementation = ClientDTO.class)
        )
        @Valid @RequestBody ClientDTO clientDTO) {
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
    @Operation(
        summary = "Mettre à jour un client", 
        description = "Met à jour les informations d'un client existant. Seuls les champs fournis seront modifiés.",
        tags = {"Clients"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Client mis à jour avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Client non trouvé avec l'ID spécifié",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Données de client invalides",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string", example = "Erreur: Email déjà utilisé par un autre client")
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Erreur de validation des données",
            content = @Content
        )
    })
    public ResponseEntity<?> mettreAJourClient(
        @Parameter(
            description = "ID unique du client à modifier", 
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @PathVariable Long id, 
        @Parameter(
            description = "Nouvelles informations du client", 
            required = true,
            schema = @Schema(implementation = ClientDTO.class)
        )
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
    @Operation(
        summary = "Supprimer un client", 
        description = "Supprime définitivement un client et toutes ses factures associées. Cette action est irréversible.",
        tags = {"Clients"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Client supprimé avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string", example = "Client supprimé avec succès")
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Client non trouvé avec l'ID spécifié",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Impossible de supprimer le client (contraintes de données)",
            content = @Content
        )
    })
    public ResponseEntity<String> supprimerClient(
        @Parameter(
            description = "ID unique du client à supprimer", 
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @PathVariable Long id) {
        boolean supprime = clientService.supprimerClient(id);
        if (supprime) {
            return ResponseEntity.ok("Client supprimé avec succès");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}