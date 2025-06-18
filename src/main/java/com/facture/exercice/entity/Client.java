package com.facture.exercice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant un client
 */

@Entity
@Table(name = "clients")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String nom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Le SIRET est obligatoire")
    @Size(min = 14, max = 14, message = "Le SIRET doit contenir exactement 14 caractères")
    @Column(nullable = false, unique = true)
    private String siret;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    // Relation avec les factures
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Facture> factures;
    
    // Constructeurs
    public Client() {
        this.dateCreation = LocalDateTime.now();
    }
    
    public Client(String nom, String email, String siret) {
        this();
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
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public List<Facture> getFactures() { return factures; }
    public void setFactures(List<Facture> factures) { this.factures = factures; }
}