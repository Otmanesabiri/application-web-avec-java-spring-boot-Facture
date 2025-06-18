# 📋 API Module de Facturation - Spring Boot

> Application web RESTful développée avec Spring Boot pour la gestion des clients et des factures.

## 📖 Description

Cette application permet de gérer un système de facturation complet avec les fonctionnalités suivantes :
- Gestion CRUD complète des clients
- Création et consultation des factures
- Calcul automatique des totaux (HT, TVA, TTC)
- Recherche avancée des factures
- Export JSON des factures
- Documentation API interactive avec Swagger/OpenAPI

## 🚀 Technologies Utilisées

- **Framework** : Spring Boot 3.3.5
- **Langage** : Java 17
- **Base de données** : H2 
- **ORM** : JPA/Hibernate
- **Documentation API** : SpringDoc OpenAPI (Swagger)
- **Validation** : Spring Boot Starter Validation
- **Build** : Maven
- **Tests** : JUnit 5

## 📋 Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- IDE recommandé : IntelliJ IDEA ou Visual Studio Code

## ⚡ Installation et Démarrage

### 1. Cloner le projet
```bash
git clone https://github.com/Otmanesabiri/application-web-avec-java-spring-boot-Facture.git
cd application-web-avec-java-spring-boot-Facture
```

### 2. Compiler le projet
```bash
mvn clean compile
```

### 3. Lancer l'application
```bash
mvn spring-boot:run
```

L'application sera disponible sur : **http://localhost:8095**

## 📊 Accès aux Interfaces

| Interface | URL | Description |
|-----------|-----|-------------|
| **API Documentation** | http://localhost:8095/swagger-ui.html | Interface Swagger interactive |
| **Console H2** | http://localhost:8095/h2-console | Interface d'administration de la base de données |
| **API Docs JSON** | http://localhost:8095/api-docs | Spécification OpenAPI en JSON |

### 🔧 Paramètres de connexion H2
- **URL JDBC** : `jdbc:h2:mem:exercice`
- **Username** : `sa`
- **Password** : `password`
- **Driver** : `org.h2.Driver`

## 🎯 API Endpoints

### 👥 Gestion des Clients

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/clients` | Lister tous les clients |
| `GET` | `/api/clients/{id}` | Obtenir un client par ID |
| `POST` | `/api/clients` | Créer un nouveau client |
| `PUT` | `/api/clients/{id}` | Mettre à jour un client |
| `DELETE` | `/api/clients/{id}` | Supprimer un client |

### 🧾 Gestion des Factures

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/factures` | Lister toutes les factures |
| `GET` | `/api/factures/{id}` | Obtenir une facture par ID |
| `POST` | `/api/factures` | Créer une nouvelle facture |
| `GET` | `/api/factures/recherche/client/{clientId}` | Factures par client |
| `GET` | `/api/factures/recherche/date/{date}` | Factures par date |
| `GET` | `/api/factures/{id}/export` | Exporter une facture en JSON |

## 📋 Modèle de Données

### Client
```json
{
  "id": 1,
  "nom": "Entreprise ACME",
  "email": "contact@acme.com",
  "siret": "12345678901234",
  "dateCreation": "2025-06-18T10:30:00"
}
```

### Facture
```json
{
  "id": 1,
  "clientId": 1,
  "clientNom": "Entreprise ACME",
  "date": "2025-06-18",
  "lignes": [
    {
      "description": "Prestation de conseil",
      "quantite": 2.5,
      "prixUnitaireHT": 50.00,
      "tauxTVA": 20.00
    }
  ],
  "totalHT": 125.00,
  "totalTVA": 25.00,
  "totalTTC": 150.00
}
```

## ✅ Règles de Validation

### Client
- **Nom** : Obligatoire, 2-100 caractères
- **Email** : Obligatoire, format email valide, unique
- **SIRET** : Obligatoire, exactement 14 chiffres, unique

### Facture
- **ClientId** : Obligatoire, client doit exister
- **Lignes** : Au moins une ligne obligatoire
- **Description** : Non vide pour chaque ligne
- **Quantité** : Positive (> 0)
- **Prix unitaire HT** : Positif ou nul (≥ 0)
- **Taux TVA** : Valeurs autorisées : 0%, 5.5%, 10%, 20%

## 🔄 Fonctionnalités Métier

### Calcul Automatique des Totaux
- **Total HT** : Somme de (quantité × prix unitaire HT) pour toutes les lignes
- **Total TVA** : Somme de (montant HT × taux TVA / 100) pour toutes les lignes
- **Total TTC** : Total HT + Total TVA

### Contraintes d'Intégrité
- Suppression en cascade : Supprimer un client supprime toutes ses factures
- Unicité des emails et SIRET clients
- Validation des taux de TVA français

## 🧪 Tests

### Exécuter tous les tests
```bash
mvn test
```

### Tests inclus
- Tests unitaires du service de facturation
- Validation des règles métier
- Tests de création de factures avec différents scénarios

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/com/facture/exercice/
│   │   ├── config/          # Configuration Swagger
│   │   ├── controller/      # Contrôleurs REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # Entités JPA
│   │   ├── repository/     # Repositories JPA
│   │   └── service/        # Services métier
│   └── resources/
│       ├── application.properties
│       ├── static/
│       └── templates/
└── test/
    └── java/              # Tests unitaires
```

## 🎨 Exemples d'Utilisation

### Créer un client
```bash
curl -X POST http://localhost:8095/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Entreprise ACMA",
    "email": "contact@acma.com",
    "siret": "12345678901235"
  }'
```

### Créer une facture
```bash
curl -X POST http://localhost:8095/api/factures \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "date": "2025-06-18",
    "lignes": [
      {
        "description": "Prestation de conseil",
        "quantite": 2.5,
        "prixUnitaireHT": 50.00,
        "tauxTVA": 20
      }
    ]
  }'
```

## 🌟 Fonctionnalités Avancées

### 🔍 Recherche de Factures
- **Par client** : Retrouver toutes les factures d'un client
- **Par date** : Retrouver toutes les factures d'une date donnée

### 📤 Export JSON
- Export structuré des factures avec toutes les informations
- Format JSON lisible et réutilisable

### 📖 Documentation Interactive
- Interface Swagger complète avec exemples
- Test direct des endpoints depuis l'interface
- Schémas de données détaillés

## ⚙️ Configuration

### Base de Données
L'application utilise H2 en mémoire par défaut. Pour utiliser une base persistante, modifiez `application.properties` :

```properties
# Pour une base H2 persistante
spring.datasource.url=jdbc:h2:file:./data/exercice
spring.jpa.hibernate.ddl-auto=update
```

### Port de l'Application
```properties
server.port=8095
```

## 🔧 Développement

### Ajout de Nouvelles Fonctionnalités
1. Créer les entités dans `entity/`
2. Ajouter les repositories dans `repository/`
3. Implémenter la logique métier dans `service/`
4. Créer les DTOs dans `dto/`
5. Exposer via les contrôleurs dans `controller/`

### Bonnes Pratiques Implémentées
- ✅ Séparation des couches (Controller, Service, Repository)
- ✅ Validation des données avec Bean Validation
- ✅ Gestion des erreurs avec messages explicites
- ✅ Documentation API complète
- ✅ Tests unitaires
- ✅ Calculs automatiques des totaux

## 👨‍💻 Auteur

**Otmane Sabiri**
- Email : otmane.sabiri@example.com
- GitHub : [@Otmanesabiri](https://github.com/Otmanesabiri)

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commit vos changements
4. Push vers la branche
5. Ouvrir une Pull Request

## 📞 Support

Pour toute question ou problème :
- 📧 Email : otmane.sabiri@example.com
- 🐛 Issues : [GitHub Issues](https://github.com/Otmanesabiri/application-web-avec-java-spring-boot-Facture/issues)

---

⭐ **N'oubliez pas de donner une étoile au projet si vous l'avez trouvé utile !**