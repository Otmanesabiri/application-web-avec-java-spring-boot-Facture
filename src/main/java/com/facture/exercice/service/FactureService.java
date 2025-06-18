package com.facture.exercice.service;


import com.facture.exercice.dto.FactureDTO;
import com.facture.exercice.dto.LigneFactureDTO;
import com.facture.exercice.entity.Client;
import com.facture.exercice.entity.Facture;
import com.facture.exercice.entity.LigneFacture;
import com.facture.exercice.repository.ClientRepository;
import com.facture.exercice.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des factures
 */
@Service
public class FactureService {
    
    @Autowired
    private FactureRepository factureRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    // Taux de TVA autorisés
    private static final List<BigDecimal> TAUX_TVA_AUTORISES = Arrays.asList(
        BigDecimal.ZERO,           // 0%
        new BigDecimal("5.5"),     // 5.5%
        new BigDecimal("10"),      // 10%
        new BigDecimal("20")       // 20%
    );
    
    /**
     * Récupère toutes les factures
     */
    public List<FactureDTO> obtenirToutesLesFactures() {
        return factureRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère une facture par son ID
     */
    public Optional<FactureDTO> obtenirFactureParId(Long id) {
        return factureRepository.findById(id)
                .map(this::convertirEnDTO);
    }
    
    /**
     * Crée une nouvelle facture
     */
    public FactureDTO creerFacture(FactureDTO factureDTO) {
        // Vérification que le client existe
        Client client = clientRepository.findById(factureDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + factureDTO.getClientId()));
        
        // Validation des règles métier
        validerFacture(factureDTO);
        
        // Création de la facture
        Facture facture = new Facture(client, factureDTO.getDate());
        
        // Ajout des lignes de facture
        List<LigneFacture> lignes = factureDTO.getLignes().stream()
                .map(ligneDTO -> {
                    LigneFacture ligne = convertirLigneEnEntite(ligneDTO);
                    ligne.setFacture(facture);
                    return ligne;
                })
                .collect(Collectors.toList());
        
        facture.setLignes(lignes);
        
        // Calcul des totaux
        facture.calculerTotaux();
        
        // Sauvegarde
        Facture factureSauvegardee = factureRepository.save(facture);
        return convertirEnDTO(factureSauvegardee);
    }
    
    /**
     * Recherche les factures par client
     */
    public List<FactureDTO> rechercherFacturesParClient(Long clientId) {
        return factureRepository.findByClientId(clientId).stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Recherche les factures par date
     */
    public List<FactureDTO> rechercherFacturesParDate(LocalDate date) {
        return factureRepository.findByDate(date).stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Exporte une facture au format JSON
     */
    public String exporterFactureJSON(Long id) {
        Optional<Facture> factureOpt = factureRepository.findById(id);
        if (factureOpt.isEmpty()) {
            throw new RuntimeException("Facture non trouvée avec l'ID: " + id);
        }
        
        Facture facture = factureOpt.get();
        FactureDTO factureDTO = convertirEnDTO(facture);
        
        // Construction manuelle du JSON pour plus de contrôle
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"id\": ").append(factureDTO.getId()).append(",\n");
        json.append("  \"date\": \"").append(factureDTO.getDate()).append("\",\n");
        json.append("  \"client\": {\n");
        json.append("    \"id\": ").append(factureDTO.getClientId()).append(",\n");
        json.append("    \"nom\": \"").append(factureDTO.getClientNom()).append("\"\n");
        json.append("  },\n");
        json.append("  \"lignes\": [\n");
        
        for (int i = 0; i < factureDTO.getLignes().size(); i++) {
            LigneFactureDTO ligne = factureDTO.getLignes().get(i);
            json.append("    {\n");
            json.append("      \"description\": \"").append(ligne.getDescription()).append("\",\n");
            json.append("      \"quantite\": ").append(ligne.getQuantite()).append(",\n");
            json.append("      \"prixUnitaireHT\": ").append(ligne.getPrixUnitaireHT()).append(",\n");
            json.append("      \"tauxTVA\": ").append(ligne.getTauxTVA()).append("\n");
            json.append("    }");
            if (i < factureDTO.getLignes().size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("  ],\n");
        json.append("  \"totaux\": {\n");
        json.append("    \"totalHT\": ").append(factureDTO.getTotalHT()).append(",\n");
        json.append("    \"totalTVA\": ").append(factureDTO.getTotalTVA()).append(",\n");
        json.append("    \"totalTTC\": ").append(factureDTO.getTotalTTC()).append("\n");
        json.append("  }\n");
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Valide les règles métier d'une facture
     */
    private void validerFacture(FactureDTO factureDTO) {
        // Une facture doit avoir au moins une ligne
        if (factureDTO.getLignes() == null || factureDTO.getLignes().isEmpty()) {
            throw new RuntimeException("Une facture doit avoir au moins une ligne");
        }
        
        // Validation de chaque ligne
        for (LigneFactureDTO ligne : factureDTO.getLignes()) {
            validerLigneFacture(ligne);
        }
    }
    
    /**
     * Valide une ligne de facture
     */
    private void validerLigneFacture(LigneFactureDTO ligne) {
        // Vérification que tous les champs sont remplis
        if (ligne.getDescription() == null || ligne.getDescription().trim().isEmpty()) {
            throw new RuntimeException("La description de la ligne ne peut pas être vide");
        }
        
        if (ligne.getQuantite() == null || ligne.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("La quantité doit être positive");
        }
        
        if (ligne.getPrixUnitaireHT() == null || ligne.getPrixUnitaireHT().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Le prix unitaire HT doit être positif ou nul");
        }
        
        // Vérification du taux de TVA
        if (ligne.getTauxTVA() == null || !TAUX_TVA_AUTORISES.contains(ligne.getTauxTVA())) {
            throw new RuntimeException("Le taux de TVA doit être 0%, 5.5%, 10% ou 20%");
        }
    }
    
    /**
     * Convertit une entité Facture en FactureDTO
     */
    private FactureDTO convertirEnDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setClientId(facture.getClient().getId());
        dto.setClientNom(facture.getClient().getNom());
        dto.setDate(facture.getDate());
        dto.setTotalHT(facture.getTotalHT());
        dto.setTotalTVA(facture.getTotalTVA());
        dto.setTotalTTC(facture.getTotalTTC());
        
        List<LigneFactureDTO> lignesDTO = facture.getLignes().stream()
                .map(this::convertirLigneEnDTO)
                .collect(Collectors.toList());
        dto.setLignes(lignesDTO);
        
        return dto;
    }
    
    /**
     * Convertit une entité FactureLine en LigneFactureDTO
     */
    private LigneFactureDTO convertirLigneEnDTO(LigneFacture ligne) {
        return new LigneFactureDTO(
            ligne.getDescription(),
            ligne.getQuantite(),
            ligne.getPrixUnitaireHT(),
            ligne.getTauxTVA()
        );
    }
    
    /**
     * Convertit un LigneFactureDTO en entité LigneFacture
     */
    private LigneFacture convertirLigneEnEntite(LigneFactureDTO dto) {
        return new LigneFacture(
            dto.getDescription(),
            dto.getQuantite(),
            dto.getPrixUnitaireHT(),
            dto.getTauxTVA()
        );
    }
}