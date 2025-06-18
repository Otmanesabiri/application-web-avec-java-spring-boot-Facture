package com.facture.exercice.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configuration de la documentation OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Module de Facturation")
                        .description("API REST pour la gestion des clients et des factures\n\n" +
                                "Cette API permet de :\n" +
                                "- Gérer les clients (CRUD)\n" +
                                "- Créer et consulter les factures\n" +
                                "- Calculer automatiquement les totaux\n" +
                                "- Exporter les factures au format JSON\n" +
                                "- Rechercher les factures par client ou date\n\n" +
                                "**Formats de données :**\n" +
                                "- Dates : Format ISO (YYYY-MM-DD)\n" +
                                "- Montants : BigDecimal avec 2 décimales\n" +
                                "- SIRET : 14 chiffres exactement\n\n" +
                                "**Codes d'erreur courants :**\n" +
                                "- 400 : Données invalides\n" +
                                "- 404 : Ressource non trouvée\n" +
                                "- 422 : Erreur de validation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Otmane Sabiri")
                                .email("otmane.sabiri@example.com")
                                .url("https://github.com/Otmanesabiri"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8095")
                                .description("Serveur de développement local"),
                        new Server()
                                .url("https://facture-api.example.com")
                                .description("Serveur de production (exemple)")
                ))
                .components(new Components()
                        .addResponses("ValidationError", new ApiResponse()
                                .description("Erreur de validation des données")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .type("object")
                                                        .addProperty("message", new Schema<>().type("string"))
                                                        .addProperty("errors", new Schema<>().type("array"))))))
                        .addResponses("NotFound", new ApiResponse()
                                .description("Ressource non trouvée")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .type("string")
                                                        .example("Ressource non trouvée")))))
                        .addResponses("ServerError", new ApiResponse()
                                .description("Erreur interne du serveur")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .type("string")
                                                        .example("Une erreur interne s'est produite")))))
                        .addExamples("ClientExample", new Example()
                                .summary("Exemple de client")
                                .description("Client type avec toutes les informations requises")
                                .value("{\n" +
                                        "  \"nom\": \"Entreprise ACME\",\n" +
                                        "  \"email\": \"contact@acme.com\",\n" +
                                        "  \"siret\": \"12345678901234\"\n" +
                                        "}"))
                        .addExamples("FactureExample", new Example()
                                .summary("Exemple de facture")
                                .description("Facture complète avec lignes de facturation")
                                .value("{\n" +
                                        "  \"clientId\": 1,\n" +
                                        "  \"date\": \"2025-06-18\",\n" +
                                        "  \"lignes\": [\n" +
                                        "    {\n" +
                                        "      \"description\": \"Prestation de conseil\",\n" +
                                        "      \"quantite\": 2.5,\n" +
                                        "      \"prixUnitaireHT\": 50.00,\n" +
                                        "      \"tauxTVA\": 20.00\n" +
                                        "    }\n" +
                                        "  ]\n" +
                                        "}")));
    }
}