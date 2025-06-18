package com.facture.exercice.service;


import com.facture.exercice.dto.ClientDTO;
import com.facture.exercice.entity.Client;
import com.facture.exercice.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des clients
 */
@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    /**
     * Récupère tous les clients
     */
    public List<ClientDTO> obtenirTousLesClients() {
        return clientRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un client par son ID
     */
    public Optional<ClientDTO> obtenirClientParId(Long id) {
        return clientRepository.findById(id)
                .map(this::convertirEnDTO);
    }
    
    /**
     * Crée un nouveau client
     */
    public ClientDTO creerClient(ClientDTO clientDTO) {
        // Vérification de l'unicité de l'email
        if (clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new RuntimeException("Un client avec cet email existe déjà");
        }
        
        // Vérification de l'unicité du SIRET
        if (clientRepository.existsBySiret(clientDTO.getSiret())) {
            throw new RuntimeException("Un client avec ce SIRET existe déjà");
        }
        
        Client client = convertirEnEntite(clientDTO);
        Client clientSauvegarde = clientRepository.save(client);
        return convertirEnDTO(clientSauvegarde);
    }
    
    /**
     * Met à jour un client existant
     */
    public Optional<ClientDTO> mettreAJourClient(Long id, ClientDTO clientDTO) {
        return clientRepository.findById(id).map(client -> {
            // Vérification de l'unicité de l'email (sauf pour le client actuel)
            if (!client.getEmail().equals(clientDTO.getEmail()) && 
                clientRepository.existsByEmail(clientDTO.getEmail())) {
                throw new RuntimeException("Un client avec cet email existe déjà");
            }
            
            // Vérification de l'unicité du SIRET (sauf pour le client actuel)
            if (!client.getSiret().equals(clientDTO.getSiret()) && 
                clientRepository.existsBySiret(clientDTO.getSiret())) {
                throw new RuntimeException("Un client avec ce SIRET existe déjà");
            }
            
            client.setNom(clientDTO.getNom());
            client.setEmail(clientDTO.getEmail());
            client.setSiret(clientDTO.getSiret());
            
            Client clientMisAJour = clientRepository.save(client);
            return convertirEnDTO(clientMisAJour);
        });
    }
    
    /**
     * Supprime un client
     */
    public boolean supprimerClient(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Convertit une entité Client en ClientDTO
     */
    private ClientDTO convertirEnDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setEmail(client.getEmail());
        dto.setSiret(client.getSiret());
        dto.setDateCreation(client.getDateCreation().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }
    
    /**
     * Convertit un ClientDTO en entité Client
     */
    private Client convertirEnEntite(ClientDTO dto) {
        Client client = new Client();
        client.setNom(dto.getNom());
        client.setEmail(dto.getEmail());
        client.setSiret(dto.getSiret());
        return client;
    }
}