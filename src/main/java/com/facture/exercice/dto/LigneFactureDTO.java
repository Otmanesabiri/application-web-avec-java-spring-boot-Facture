package com.facture.exercice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO pour les lignes de facture
 */
@Schema(description = "Ligne de facturation avec description, quantité, prix et TVA")
public class LigneFactureDTO {
    
    @NotBlank(message = "La description est obligatoire")
    @Schema(description = "Description du produit ou service", example = "Prestation de conseil", required = true)
    private String description;
    
    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être positive")
    @Schema(description = "Quantité du produit ou nombre d'heures", example = "2.5", required = true, minimum = "0.01")
    private BigDecimal quantite;
    
    @NotNull(message = "Le prix unitaire HT est obligatoire")
    @DecimalMin(value = "0.00", message = "Le prix unitaire HT doit être positif ou nul")
    @Schema(description = "Prix unitaire hors taxes", example = "50.00", required = true, minimum = "0.00")
    private BigDecimal prixUnitaireHT;
    
    @NotNull(message = "Le taux de TVA est obligatoire")
    @DecimalMin(value = "0.00", message = "Le taux de TVA doit être positif ou nul")
    @DecimalMax(value = "100.00", message = "Le taux de TVA ne peut pas dépasser 100%")
    @Schema(description = "Taux de TVA en pourcentage", example = "20.00", required = true, minimum = "0.00", maximum = "100.00")
    private BigDecimal tauxTVA;
    
    // Constructeurs
    public LigneFactureDTO() {}

    public LigneFactureDTO(String description, BigDecimal quantite,
                         BigDecimal prixUnitaireHT, BigDecimal tauxTVA) {
        this.description = description;
        this.quantite = quantite;
        this.prixUnitaireHT = prixUnitaireHT;
        this.tauxTVA = tauxTVA;
    }
    
    // Getters et Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    
    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public void setPrixUnitaireHT(BigDecimal prixUnitaireHT) { this.prixUnitaireHT = prixUnitaireHT; }
    
    public BigDecimal getTauxTVA() { return tauxTVA; }
    public void setTauxTVA(BigDecimal tauxTVA) { this.tauxTVA = tauxTVA; }
}