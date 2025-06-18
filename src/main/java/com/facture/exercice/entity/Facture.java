package com.facture.exercice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une facture
 */
@Entity
@Table(name = "invoices")
public class Facture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La date est obligatoire")
    @Column(nullable = false)
    private LocalDate date;
    
    // Relation avec le client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    // Relation avec les lignes de facture
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneFacture> lignes = new ArrayList<>();
    
    // Montants calculés automatiquement
    @Column(name = "total_ht", precision = 10, scale = 2)
    private BigDecimal totalHT = BigDecimal.ZERO;
    
    @Column(name = "total_tva", precision = 10, scale = 2)
    private BigDecimal totalTVA = BigDecimal.ZERO;
    
    @Column(name = "total_ttc", precision = 10, scale = 2)
    private BigDecimal totalTTC = BigDecimal.ZERO;
    
    // Constructeurs
    public Facture() {
        this.date = LocalDate.now();
    }

    public Facture(Client client, LocalDate date) {
        this.client = client;
        this.date = date != null ? date : LocalDate.now();
    }
    
    /**
     * Calcule automatiquement les totaux de la facture
     */
    public void calculerTotaux() {
        this.totalHT = BigDecimal.ZERO;
        this.totalTVA = BigDecimal.ZERO;
        
        for (LigneFacture ligne : lignes) {
            BigDecimal montantHT = ligne.calculerMontantHT();
            BigDecimal montantTVA = ligne.calculerMontantTVA();
            
            this.totalHT = this.totalHT.add(montantHT);
            this.totalTVA = this.totalTVA.add(montantTVA);
        }
        
        this.totalTTC = this.totalHT.add(this.totalTVA);
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public List<LigneFacture> getLignes() { return lignes; }
    public void setLignes(List<LigneFacture> lignes) { this.lignes = lignes; }

    public BigDecimal getTotalHT() { return totalHT; }
    public void setTotalHT(BigDecimal totalHT) { this.totalHT = totalHT; }
    
    public BigDecimal getTotalTVA() { return totalTVA; }
    public void setTotalTVA(BigDecimal totalTVA) { this.totalTVA = totalTVA; }
    
    public BigDecimal getTotalTTC() { return totalTTC; }
    public void setTotalTTC(BigDecimal totalTTC) { this.totalTTC = totalTTC; }
}