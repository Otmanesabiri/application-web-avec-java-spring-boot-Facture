package com.facture.exercice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la création et l'affichage des factures
 */
public class FactureDTO {
    
    private Long id;
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;
    
    private String clientNom;
    
    private LocalDate date;
    
    @NotEmpty(message = "Une facture doit avoir au moins une ligne")
    @Valid
    private List<LigneFactureDTO> lignes;
    
    private BigDecimal totalHT;
    private BigDecimal totalTVA;
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