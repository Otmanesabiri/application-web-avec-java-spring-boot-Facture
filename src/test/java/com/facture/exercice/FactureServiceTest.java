package com.facture.exercice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.facture.exercice.dto.FactureDTO;
import com.facture.exercice.dto.LigneFactureDTO;
import com.facture.exercice.entity.Client;
import com.facture.exercice.entity.Facture;
import com.facture.exercice.service.FactureService;
import com.facture.exercice.repository.ClientRepository;
import com.facture.exercice.repository.FactureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour InvoiceService
 */
@SpringBootTest
public class FactureServiceTest {
    
    @Autowired
    private FactureService factureService;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private FactureRepository factureRepository;

    private Client clientTest;

    @BeforeEach
    void setUp() {
        clientTest = new Client("Test Client", "test@test.com", "12345678901234");
        clientTest.setId(1L);
    }
    
    @Test
    void testCreerFacture_AvecDonneesValides_DoitReussir() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientTest));

        LigneFactureDTO ligne = new LigneFactureDTO(
            "Test service",
            new BigDecimal("2"),
            new BigDecimal("100"),
            new BigDecimal("20")
        );

        FactureDTO factureDTO = new FactureDTO(
            1L,
            LocalDate.now(),
            Arrays.asList(ligne)
        );

        // Mock factureRepository.save to return a Facture with an ID
        Facture mockFacture = new Facture();
        mockFacture.setId(1L);
        mockFacture.setClient(clientTest); // <-- Add this line
        // set other necessary fields if needed
        when(factureRepository.save(any(Facture.class))).thenReturn(mockFacture);
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            factureService.creerFacture(factureDTO);
        });
        
        verify(clientRepository).findById(1L);
    }
    
    @Test
    void testCreerFacture_AvecClientInexistant_DoitEchouer() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        LigneFactureDTO ligne = new LigneFactureDTO(
            "Test service",
            new BigDecimal("1"),
            new BigDecimal("100"),
            new BigDecimal("20")
        );

        FactureDTO factureDTO = new FactureDTO(
            999L,
            LocalDate.now(),
            Arrays.asList(ligne)
        );
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            factureService.creerFacture(factureDTO);
        });
        
        assertTrue(exception.getMessage().contains("Client non trouvé"));
    }
    
    @Test
    void testCreerFacture_SansLignes_DoitEchouer() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientTest));

        FactureDTO factureDTO = new FactureDTO(1L, LocalDate.now(), Arrays.asList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            factureService.creerFacture(factureDTO);
        });
        
        assertTrue(exception.getMessage().contains("au moins une ligne"));
    }
}