package com.facture.exercice.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour les opérations sur les clients
 */
@Schema(description = "Objet représentant un client avec ses informations de base")
public class ClientDTO {
    
    @Schema(description = "Identifiant unique du client", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Schema(description = "Nom ou raison sociale du client", example = "Entreprise ACME", required = true, minLength = 2, maxLength = 100)
    private String nom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Schema(description = "Adresse email du client", example = "contact@acme.com", required = true, format = "email")
    private String email;
    
    @NotBlank(message = "Le SIRET est obligatoire")
    @Size(min = 14, max = 14, message = "Le SIRET doit contenir exactement 14 caractères")
    @Schema(description = "Numéro SIRET du client (14 chiffres)", example = "12345678901234", required = true, minLength = 14, maxLength = 14, pattern = "^[0-9]{14}$")
    private String siret;
    
    @Schema(description = "Date de création du client au format ISO", example = "2025-06-18T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private String dateCreation;
    
    // Constructeurs
    public ClientDTO() {}
    
    public ClientDTO(String nom, String email, String siret) {
        this.nom = nom;
        this.email = email;
        this.siret = siret;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    
    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }
}