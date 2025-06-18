package com.facture.exercice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la création et l'affichage des factures
 */
@Schema(description = "Objet représentant une facture avec ses lignes et totaux calculés")
public class FactureDTO {
    
    @Schema(description = "Identifiant unique de la facture", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @NotNull(message = "L'ID du client est obligatoire")
    @Schema(description = "Identifiant du client associé à la facture", example = "1", required = true)
    private Long clientId;
    
    @Schema(description = "Nom du client (calculé automatiquement)", example = "Entreprise ACME", accessMode = Schema.AccessMode.READ_ONLY)
    private String clientNom;
    
    @Schema(description = "Date de création de la facture", example = "2025-06-18", type = "string", format = "date")
    private LocalDate date;
    
    @NotEmpty(message = "Une facture doit avoir au moins une ligne")
    @Valid
    @Schema(description = "Liste des lignes de facturation", required = true)
    private List<LigneFactureDTO> lignes;
    
    @Schema(description = "Total hors taxes (calculé automatiquement)", example = "100.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalHT;
    
    @Schema(description = "Total de la TVA (calculé automatiquement)", example = "20.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalTVA;
    
    @Schema(description = "Total toutes taxes comprises (calculé automatiquement)", example = "120.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalTTC;
    
    // Constructeurs
    public FactureDTO() {}
    
    public FactureDTO(Long clientId, LocalDate date, List<LigneFactureDTO> lignes) {
        this.clientId = clientId;
        this.date = date;
        this.lignes = lignes;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public List<LigneFactureDTO> getLignes() { return lignes; }
    public void setLignes(List<LigneFactureDTO> lignes) { this.lignes = lignes; }
    
    public BigDecimal getTotalHT() { return totalHT; }
    public void setTotalHT(BigDecimal totalHT) { this.totalHT = totalHT; }
    
    public BigDecimal getTotalTVA() { return totalTVA; }
    public void setTotalTVA(BigDecimal totalTVA) { this.totalTVA = totalTVA; }
    
    public BigDecimal getTotalTTC() { return totalTTC; }
    public void setTotalTTC(BigDecimal totalTTC) { this.totalTTC = totalTTC; }
}