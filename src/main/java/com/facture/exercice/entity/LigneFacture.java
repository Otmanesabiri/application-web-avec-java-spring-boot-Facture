package com.facture.exercice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entité représentant une ligne de facture
 */
@Entity
@Table(name = "invoice_lines")
public class LigneFacture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "La description est obligatoire")
    @Column(nullable = false)
    private String description;
    
    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantite;
    
    @NotNull(message = "Le prix unitaire HT est obligatoire")
    @DecimalMin(value = "0.00", message = "Le prix unitaire HT doit être positif ou nul")
    @Column(name = "prix_unitaire_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaireHT;
    
    @NotNull(message = "Le taux de TVA est obligatoire")
    @DecimalMin(value = "0.00", message = "Le taux de TVA doit être positif ou nul")
    @DecimalMax(value = "100.00", message = "Le taux de TVA ne peut pas dépasser 100%")
    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTVA;
    
    // Relation avec la facture
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Facture invoice;
    
    // Constructeurs
    public LigneFacture() {}
    
    public LigneFacture(String description, BigDecimal quantite, 
                       BigDecimal prixUnitaireHT, BigDecimal tauxTVA) {
        this.description = description;
        this.quantite = quantite;
        this.prixUnitaireHT = prixUnitaireHT;
        this.tauxTVA = tauxTVA;
    }
    
    /**
     * Calcule le montant HT de la ligne (quantité × prix unitaire HT)
     */
    public BigDecimal calculerMontantHT() {
        return quantite.multiply(prixUnitaireHT).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcule le montant de TVA de la ligne
     */
    public BigDecimal calculerMontantTVA() {
        BigDecimal montantHT = calculerMontantHT();
        return montantHT.multiply(tauxTVA.divide(BigDecimal.valueOf(100)))
                       .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcule le montant TTC de la ligne
     */
    public BigDecimal calculerMontantTTC() {
        return calculerMontantHT().add(calculerMontantTVA());
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    
    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public void setPrixUnitaireHT(BigDecimal prixUnitaireHT) { this.prixUnitaireHT = prixUnitaireHT; }
    
    public BigDecimal getTauxTVA() { return tauxTVA; }
    public void setTauxTVA(BigDecimal tauxTVA) { this.tauxTVA = tauxTVA; }
    
    public Facture getFacture() { return invoice; }
    public void setFacture(Facture invoice) { this.invoice = invoice; }

}
