package com.facture.exercice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO pour les lignes de facture
 */
public class LigneFactureDTO {
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être positive")
    private BigDecimal quantite;
    
    @NotNull(message = "Le prix unitaire HT est obligatoire")
    @DecimalMin(value = "0.00", message = "Le prix unitaire HT doit être positif ou nul")
    private BigDecimal prixUnitaireHT;
    
    @NotNull(message = "Le taux de TVA est obligatoire")
    @DecimalMin(value = "0.00", message = "Le taux de TVA doit être positif ou nul")
    @DecimalMax(value = "100.00", message = "Le taux de TVA ne peut pas dépasser 100%")
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