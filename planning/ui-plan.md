# 📋 Plan de Construction UI - Steam V2 Better du Pauvre

---

## 🎯 Objectif
Construire l'interface utilisateur de 3 applications desktop en **Kotlin Multiplatform avec Compose** :
1. **Player** - Application grand public pour les joueurs
2. **Game Editor** - Outil professionnel pour les studios de développement
3. **Platform** - Interface d'administration et de modération

---

## 🎨 Spécifications Générales UI/UX

### Design System
- **Style** : Moderne, épuré, inspiré des plateformes gaming (Steam, Epic Games)
- **Thème** : Dark mode par défaut avec accents colorés
- **Palette de couleurs suggérée** :
  - Background principal : `#1a1a2e` (bleu très foncé)
  - Background secondaire : `#16213e` (bleu nuit)
  - Accent primaire : `#0f3460` (bleu profond)
  - Accent highlight : `#e94560` (rose/rouge vif)
  - Texte principal : `#ffffff`
  - Texte secondaire : `#a0a0a0`
  - Succès : `#4ade80` (vert)
  - Erreur : `#ef4444` (rouge)
  - Warning : `#f59e0b` (orange)

### Typographie
- **Police principale** : Inter ou Roboto (moderne, lisible)
- **Titres** : Bold, tailles 24-32px
- **Corps** : Regular, 14-16px
- **Labels/Captions** : 12px

### Composants Réutilisables à Créer
- `PrimaryButton` - Bouton principal avec effet hover
- `SecondaryButton` - Bouton secondaire outline
- `DangerButton` - Bouton rouge pour actions critiques
- `TextField` - Champ de saisie stylisé
- `PasswordField` - Champ mot de passe avec toggle visibilité
- `Card` - Carte conteneur avec ombre et bordure arrondie
- `GameCard` - Carte spécifique pour afficher un jeu (image, titre, prix)
- `NavigationRail` - Barre de navigation latérale
- `TopBar` - Barre supérieure avec titre et actions
- `Dialog` - Popup modal pour confirmations/formulaires
- `Snackbar` - Notifications temporaires (succès, erreur)
- `LoadingIndicator` - Spinner de chargement
- `EmptyState` - Écran vide avec illustration et message
- `StarRating` - Composant d'évaluation par étoiles (1-5)
- `Badge` - Petit indicateur (statut, compteur)
- `Tag` - Chip pour afficher les tags de jeux

### Dimensions Desktop
- **Fenêtre minimale** : 1024x768px
- **Fenêtre recommandée** : 1280x800px
- **Navigation latérale** : 250px de large (collapsible à 72px)
- **Marges standard** : 16px / 24px
- **Border radius** : 8px (cartes), 4px (boutons), 12px (modals)

---

## 🏗️ Architecture Commune

### Structure des ViewModels
Chaque écran doit avoir son propre ViewModel avec :
- **State** : Data class représentant l'état de l'UI
- **Events** : Sealed class pour les actions utilisateur
- **Points d'entrée Kafka** : Fonctions suspendues à implémenter plus tard

```
📁 viewmodel/
├── AuthViewModel.kt
├── [Feature]ViewModel.kt
└── ...
```

### Pattern de Navigation
Utiliser une navigation par état avec un `NavController` ou équivalent Compose :
- `Screen` sealed class définissant toutes les destinations
- Navigation stack pour le back

---

## 📱 Application 1 : PLAYER CLIENT

### Structure des Écrans

```
📁 player/
├── 🔐 auth/
│   ├── LoginScreen
│   └── RegisterScreen
├── 🏠 home/
│   └── HomeScreen (Dashboard)
├── 🛒 store/
│   ├── StoreScreen (Catalogue)
│   └── GameDetailScreen
├── 📚 library/
│   └── LibraryScreen
└── 👤 profile/
    └── ProfileScreen
```

### 🔐 Écrans d'Authentification

#### LoginScreen
**Layout** :
- Centré verticalement et horizontalement
- Logo de l'application en haut
- Formulaire de connexion dans une Card

**Composants** :
- Logo/Titre de l'application
- `TextField` : Pseudo
- `PasswordField` : Mot de passe
- `PrimaryButton` : "Se connecter"
- Lien : "Pas encore de compte ? S'inscrire"
- `Snackbar` pour erreurs de connexion

**Points d'entrée Kafka à prévoir** :
```kotlin
// À implémenter plus tard
suspend fun onLoginClicked(pseudo: String, password: String): LoginResponse
```

#### RegisterScreen
**Layout** :
- Similaire à LoginScreen
- Formulaire plus complet

**Composants** :
- Logo/Titre
- `TextField` : Pseudo
- `TextField` : Email
- `PasswordField` : Mot de passe
- `PasswordField` : Confirmation mot de passe
- `DatePicker` : Date de naissance (pour vérification d'âge)
- `Checkbox` : Acceptation CGU
- `PrimaryButton` : "Créer mon compte"
- Lien : "Déjà un compte ? Se connecter"

**Points d'entrée Kafka à prévoir** :
```kotlin
suspend fun onRegisterClicked(
    pseudo: String,
    email: String,
    password: String,
    birthDate: LocalDate
): RegisterPlayerResponse
```

---

### 🛒 StoreScreen (Catalogue)

**Layout** :
- `NavigationRail` à gauche
- Zone principale avec grille de jeux
- Barre de recherche et filtres en haut

**Composants** :
- `TopBar` : Titre "Magasin" + Barre de recherche
- Section filtres :
  - `Dropdown` : Trier par (Prix croissant, décroissant, Popularité, Date)
  - `ChipGroup` : Tags de filtrage (Action, RPG, Indie, etc.)
- `LazyVerticalGrid` : Grille de `GameCard`
  - Image du jeu (placeholder si pas d'image)
  - Titre du jeu
  - Prix (avec formatage €)
  - Tags (max 2-3 visibles)
- `LoadingIndicator` pendant le chargement
- `EmptyState` si aucun jeu

**Points d'entrée Kafka à prévoir** :
```kotlin
// Listener sur le topic "game"
fun observeGamesCatalog(): Flow<List<Game>>

// Listener sur le topic "dlc" 
fun observeDLCs(): Flow<List<DLC>>
```

---

### 🎮 GameDetailScreen

**Layout** :
- Header avec image/bannière du jeu
- Contenu scrollable avec détails
- Sidebar avec actions d'achat

**Composants** :
- **Header** :
  - Image bannière (ou placeholder gradient)
  - Titre du jeu (grand)
  - Développeur/Éditeur
  - Tags
- **Section Description** :
  - Texte description complète
  - Galerie screenshots (si disponible)
- **Section DLCs** :
  - Liste des DLCs disponibles avec prix
  - `Card` par DLC avec bouton "Acheter"
- **Section Avis** :
  - Note moyenne avec `StarRating`
  - Liste des évaluations (`Card` par avis)
    - Pseudo du joueur
    - Note (étoiles)
    - Commentaire
    - Date
- **Sidebar Actions** :
  - Prix affiché en grand
  - `PrimaryButton` : "Acheter" (si non possédé)
  - `PrimaryButton` : "JOUER" (si possédé, style différent)
  - `SecondaryButton` : "Ajouter à la wishlist"

**Actions utilisateur (boutons sans comportement pour l'instant)** :
- Bouton "Acheter" → Prévoir modal de confirmation
- Bouton "JOUER" → Action simulée (log ou toast)
- Bouton "Noter ce jeu" → Ouvre modal de notation

**Points d'entrée Kafka à prévoir** :
```kotlin
suspend fun onBuyGameClicked(gameId: String): BuyGameResponse
suspend fun onBuyDLCClicked(dlcId: String): BuyGameResponse
suspend fun onSubmitEvaluation(gameId: String, rating: Int, comment: String)

// Listener sur le topic "evaluation"
fun observeEvaluations(gameId: String): Flow<List<Evaluation>>
```

---

### 📚 LibraryScreen

**Layout** :
- `NavigationRail` à gauche
- Liste/Grille de jeux possédés
- Options de tri/filtrage

**Composants** :
- `TopBar` : Titre "Ma Bibliothèque" + Toggle vue (grille/liste)
- Section filtres :
  - `TextField` : Recherche dans la bibliothèque
  - `Dropdown` : Trier par (Alphabétique, Date d'achat, Dernière session)
- `LazyVerticalGrid` ou `LazyColumn` : Jeux possédés
  - `GameCard` avec overlay "JOUER"
  - Badge "Nouveau" si récemment acheté
- `EmptyState` : "Votre bibliothèque est vide - Visitez le magasin !"

**Points d'entrée Kafka à prévoir** :
```kotlin
// Listener sur le topic "ownership" filtré par playerId
fun observeMyOwnedGames(playerId: String): Flow<List<String>> // Liste des gameIds

// Logique de croisement avec le catalogue
fun getOwnedGamesDetails(): Flow<List<Game>>
```

---

### 📢 Fonctionnalités Social & Feedback

#### Modal : Noter un jeu
**Composants** :
- `Dialog` centré
- Titre : "Donnez votre avis"
- `StarRating` : Sélection 1-5 étoiles (cliquable)
- `TextField` multiline : Commentaire (optionnel)
- `PrimaryButton` : "Publier"
- `SecondaryButton` : "Annuler"

**Points d'entrée Kafka à prévoir** :
```kotlin
suspend fun submitEvaluation(evaluation: Evaluation)
```

#### Modal : Signaler un bug
**Composants** :
- `Dialog` centré
- Titre : "Signaler un bug"
- `Dropdown` : Sélection du jeu concerné
- `TextField` : Titre du bug
- `TextField` multiline : Description détaillée
- `Dropdown` : Sévérité (Mineur, Majeur, Critique)
- `PrimaryButton` : "Envoyer le rapport"
- `SecondaryButton` : "Annuler"

**Points d'entrée Kafka à prévoir** :
```kotlin
suspend fun submitBugReport(bugReport: BugReport)
```

---

### 🧭 Navigation Player Client

```
NavigationRail Items:
├── 🏠 Accueil (HomeScreen)
├── 🛒 Magasin (StoreScreen)
├── 📚 Bibliothèque (LibraryScreen)
├── 👤 Profil (ProfileScreen)
└── 🚪 Déconnexion
```

---

## 🖥️ Application 2 : GAME EDITOR

### Structure des Écrans

```
📁 game-editor/
├── 🔐 auth/
│   ├── LoginScreen
│   └── RegisterScreen
├── 🏠 dashboard/
│   └── DashboardScreen
├── 🎮 games/
│   ├── MyGamesScreen
│   ├── CreateGameScreen
│   └── EditGameScreen
├── 📦 dlc/
│   └── ManageDLCScreen
├── 🔄 patches/
│   └── ManagePatchesScreen
├── 🐛 bugs/
│   └── BugReportsScreen
└── 📤 publish/
    └── PublishScreen
```

### 🔐 Écrans d'Authentification Éditeur

#### LoginScreen (similaire Player)
- Même structure mais avec branding "Game Editor"
- Lien vers inscription Éditeur

#### RegisterScreen (Éditeur)
**Composants spécifiques** :
- `TextField` : Nom du studio
- `TextField` : Email professionnel
- `PasswordField` : Mot de passe
- `TextField` multiline : Description du studio
- `RadioGroup` : Type de compte
  - "Studio indépendant"
  - "Entreprise"
- `TextField` : Site web (optionnel)
- `PrimaryButton` : "Demander l'inscription"
- Info : "Votre demande sera examinée par notre équipe"

**Points d'entrée Kafka à prévoir** :
```kotlin
suspend fun onRegisterEditorClicked(
    studioName: String,
    email: String,
    password: String,
    description: String,
    studioType: StudioType
): RegisterEditorResponse
```

---

### 🏠 DashboardScreen

**Layout** :
- `NavigationRail` à gauche
- Grille de statistiques en haut
- Activité récente en dessous

**Composants** :
- `TopBar` : "Tableau de bord - [Nom du Studio]"
- **Section Stats** (Row de `Card`) :
  - Nombre de jeux publiés
  - Nombre total de ventes
  - Revenus totaux (si implémenté)
  - Bugs non résolus
- **Section Actions Rapides** :
  - `PrimaryButton` : "Créer un nouveau jeu"
  - `SecondaryButton` : "Gérer mes publications"
- **Section Activité Récente** :
  - Liste des dernières actions (publications, avis reçus, bugs signalés)

---

### 🎮 MyGamesScreen

**Layout** :
- Liste des jeux créés par l'éditeur
- Actions par jeu

**Composants** :
- `TopBar` : "Mes Jeux" + `PrimaryButton` "Nouveau jeu"
- `LazyColumn` : Liste de `Card` par jeu
  - Image miniature
  - Titre
  - Statut (`Badge`) : Brouillon / En attente / Publié / Refusé
  - Date de création
  - Actions :
    - `IconButton` : Éditer
    - `IconButton` : Gérer DLCs
    - `IconButton` : Publier (si brouillon)
    - `IconButton` : Voir les stats
- `EmptyState` : "Aucun jeu créé - Commencez maintenant !"

**Points d'entrée Kafka à prévoir** :
```kotlin
// Listener sur le topic "game" filtré par editorId
fun observeMyGames(editorId: String): Flow<List<Game>>
```

---

### ✏️ CreateGameScreen / EditGameScreen

**Layout** :
- Formulaire en plusieurs sections
- Sidebar avec aperçu en temps réel

**Composants** :
- `TopBar` : "Créer un jeu" / "Modifier [Nom du jeu]"
- **Section Informations de base** :
  - `TextField` : Nom du jeu
  - `TextField` multiline : Description courte (200 caractères)
  - `TextField` multiline : Description complète
  - `TextField` : Prix (avec validation numérique)
- **Section Médias** :
  - `ImagePicker` : Image de couverture
  - `ImagePicker` multiple : Screenshots (optionnel)
- **Section Catégorisation** :
  - `ChipGroup` éditable : Tags (sélection multiple)
  - `Dropdown` : Catégorie principale
  - `Dropdown` : Classification d'âge (PEGI)
- **Section Fichiers** (préparation future) :
  - Zone de drop pour fichiers du jeu (placeholder)
- **Actions** :
  - `SecondaryButton` : "Enregistrer le brouillon"
  - `PrimaryButton` : "Soumettre pour publication"

**Points d'entrée Kafka à prévoir** :
```kotlin
suspend fun saveGameDraft(game: Game)
suspend fun submitForPublication(publishRequest: PublishRequest): PublishResponse
```

---

### 📦 ManageDLCScreen

**Layout** :
- Sélection du jeu parent
- Liste des DLCs existants
- Formulaire d'ajout

**Composants** :
- `TopBar` : "Gestion des DLCs"
- `Dropdown` : Sélectionner le jeu
- **Liste DLCs existants** :
  - `Card` par DLC avec :
    - Nom
    - Prix
    - Statut
    - Actions (Éditer, Supprimer)
- **Formulaire Nouveau DLC** :
  - `TextField` : Nom du DLC
  - `TextField` multiline : Description
  - `TextField` : Prix
  - `PrimaryButton` : "Ajouter le DLC"

**Points d'entrée Kafka à prévoir** :
```kotlin
fun observeMyDLCs(editorId: String): Flow<List<DLC>>
suspend fun createDLC(dlc: DLC): PublishResponse
```

---

### 🔄 ManagePatchesScreen

**Layout** :
- Sélection du jeu
- Historique des versions
- Formulaire nouvelle version

**Composants** :
- `TopBar` : "Notes de version"
- `Dropdown` : Sélectionner le jeu
- **Historique des patches** :
  - `Timeline` vertical avec `Card` par version :
    - Numéro de version (ex: v1.0.2)
    - Date de publication
    - Notes de changement (résumé)
- **Nouveau Patch** :
  - `TextField` : Numéro de version
  - `TextField` multiline : Notes de changement (markdown supporté)
  - `PrimaryButton` : "Publier la mise à jour"

**Points d'entrée Kafka à prévoir** :
```kotlin
fun observePatches(gameId: String): Flow<List<Patch>>
suspend fun publishPatch(patch: Patch): PublishResponse
```

---

### 🐛 BugReportsScreen

**Layout** :
- Liste filtrable des bugs signalés
- Détail du bug sélectionné

**Composants** :
- `TopBar` : "Rapports de bugs"
- **Filtres** :
  - `Dropdown` : Filtrer par jeu
  - `ChipGroup` : Statut (Nouveau, En cours, Résolu)
  - `Dropdown` : Sévérité
- **Liste des bugs** (`LazyColumn`) :
  - `Card` par bug :
    - Titre
    - Jeu concerné
    - Sévérité (`Badge` coloré)
    - Date de signalement
    - Pseudo du joueur
- **Panel de détail** (à droite ou en modal) :
  - Description complète
  - Informations système (si fournies)
  - Actions :
    - `Dropdown` : Changer le statut
    - `TextField` : Note interne

**Points d'entrée Kafka à prévoir** :
```kotlin
// Listener sur le topic "bugReport" filtré par les jeux de l'éditeur
fun observeBugReports(editorId: String): Flow<List<BugReport>>
```

---

### 🧭 Navigation Game Editor

```
NavigationRail Items:
├── 🏠 Dashboard (DashboardScreen)
├── 🎮 Mes Jeux (MyGamesScreen)
├── 📦 DLCs (ManageDLCScreen)
├── 🔄 Patches (ManagePatchesScreen)
├── 🐛 Bugs (BugReportsScreen)
├── ⚙️ Paramètres
└── 🚪 Déconnexion
```

---

## ⚙️ Application 3 : PLATFORM (Admin)

### Structure des Écrans

```
📁 platform/
├── 🔐 auth/
│   └── AdminLoginScreen
├── 🏠 dashboard/
│   └── AdminDashboardScreen
├── 👮 moderation/
│   ├── EditorRequestsScreen
│   └── GameModerationScreen
├── 📊 monitoring/
│   └── LiveMonitoringScreen
└── ⚙️ settings/
    └── PlatformSettingsScreen
```

### 🔐 AdminLoginScreen

**Layout** :
- Design différencié (plus sobre, professionnel)
- Connexion admin uniquement

**Composants** :
- Logo "Platform Admin"
- `TextField` : Identifiant admin
- `PasswordField` : Mot de passe
- `PrimaryButton` : "Connexion sécurisée"
- Pas d'option d'inscription (comptes créés manuellement)

---

### 🏠 AdminDashboardScreen

**Layout** :
- Vue d'ensemble du système
- Alertes et actions prioritaires

**Composants** :
- `TopBar` : "Administration Platform"
- **Section Alertes** :
  - `Card` rouge si demandes en attente > seuil
  - `Card` orange si bugs critiques non traités
- **Section Stats Globales** (Grille de `Card`) :
  - Nombre total de joueurs
  - Nombre total d'éditeurs
  - Nombre de jeux publiés
  - Transactions aujourd'hui
- **Section Actions Rapides** :
  - `Badge` avec compteur : "X demandes d'éditeurs en attente"
  - `Badge` avec compteur : "X signalements à traiter"

---

### 👮 EditorRequestsScreen (Inbox)

**Layout** :
- Liste des demandes à gauche
- Détail de la demande sélectionnée à droite

**Composants** :
- `TopBar` : "Demandes d'inscription Éditeurs" + `Badge` compteur
- **Liste des demandes** (`LazyColumn`) :
  - `Card` par demande :
    - Nom du studio
    - Type (Indépendant/Entreprise)
    - Date de demande
    - Statut visuel (En attente)
- **Panel de détail** :
  - Nom du studio (grand)
  - Email de contact
  - Description complète
  - Type de compte
  - Site web (lien cliquable)
  - Date de la demande
  - **Actions** :
    - `PrimaryButton` vert : "✓ ACCEPTER"
    - `DangerButton` rouge : "✗ REFUSER"
- **Modal de refus** (si REFUSER cliqué) :
  - `TextField` multiline : "Motif du refus (optionnel)"
  - `DangerButton` : "Confirmer le refus"
  - `SecondaryButton` : "Annuler"

**Points d'entrée Kafka à prévoir** :
```kotlin
// Listener sur les demandes d'inscription
fun observePendingEditorRequests(): Flow<List<RegisterEditorRequest>>

suspend fun approveEditorRequest(requestId: String): RegisterEditorResponse
suspend fun rejectEditorRequest(requestId: String, reason: String?): RegisterEditorResponse
```

---

### 🎮 GameModerationScreen

**Layout** :
- Liste des jeux publiés
- Actions de modération

**Composants** :
- `TopBar` : "Modération du Catalogue"
- **Filtres** :
  - `TextField` : Recherche par nom
  - `Dropdown` : Filtrer par statut
  - `Dropdown` : Filtrer par éditeur
- **Liste des jeux** :
  - `Card` par jeu :
    - Image miniature
    - Titre
    - Éditeur
    - Date de publication
    - Statut (`Badge`)
    - Actions :
      - `IconButton` : Voir détails
      - `DangerButton` : "Dépublier"
- **Modal de dépublication** :
  - Titre : "Dépublier ce jeu ?"
  - Warning : "Cette action retirera le jeu du catalogue"
  - `TextField` : Motif de dépublication
  - `DangerButton` : "Confirmer la dépublication"
  - `SecondaryButton` : "Annuler"

**Points d'entrée Kafka à prévoir** :
```kotlin
fun observeAllGames(): Flow<List<Game>>
suspend fun unpublishGame(gameId: String, reason: String)
```

---

### 📊 LiveMonitoringScreen

**Layout** :
- Flux de logs en temps réel
- Graphiques de métriques

**Composants** :
- `TopBar` : "Monitoring en temps réel"
- **Section Logs** :
  - `LazyColumn` auto-scroll avec les derniers événements :
    - Timestamp
    - Type d'événement (Inscription, Connexion, Achat, Publication)
    - Détails (colorés selon le type)
    - Icône indicative
- **Section Filtres de logs** :
  - `ChipGroup` : Types d'événements à afficher
  - `Switch` : Auto-scroll activé/désactivé
  - `PrimaryButton` : "Exporter les logs"
- **Section Métriques** (optionnel, si temps) :
  - Graphique : Connexions par heure
  - Graphique : Transactions par jour

**Points d'entrée Kafka à prévoir** :
```kotlin
// Listeners sur plusieurs topics pour agrégation
fun observeSystemLogs(): Flow<SystemLogEvent>
fun observeTransactions(): Flow<Transaction>
fun observeConnections(): Flow<ConnectionEvent>
```

---

### 🧭 Navigation Platform Admin

```
NavigationRail Items:
├── 🏠 Dashboard (AdminDashboardScreen)
├── 👮 Éditeurs (EditorRequestsScreen)
├── 🎮 Catalogue (GameModerationScreen)
├── 📊 Monitoring (LiveMonitoringScreen)
├── ⚙️ Paramètres (PlatformSettingsScreen)
└── 🚪 Déconnexion
```

---

## 📁 Structure de Fichiers Recommandée

Pour chaque application, suivre cette structure :

```
📁 composeApp/src/jvmMain/kotlin/
├── 📁 ui/
│   ├── 📁 components/          # Composants réutilisables
│   │   ├── Buttons.kt
│   │   ├── Cards.kt
│   │   ├── TextFields.kt
│   │   ├── Navigation.kt
│   │   ├── Dialogs.kt
│   │   └── ...
│   ├── 📁 theme/               # Thème et styles
│   │   ├── Theme.kt
│   │   ├── Colors.kt
│   │   ├── Typography.kt
│   │   └── Shapes.kt
│   ├── 📁 screens/             # Écrans de l'application
│   │   ├── 📁 auth/
│   │   ├── 📁 home/
│   │   └── ...
│   └── 📁 navigation/          # Logique de navigation
│       └── NavGraph.kt
├── 📁 viewmodel/               # ViewModels
│   └── ...
├── 📁 model/                   # Data classes UI
│   └── ...
├── 📁 repository/              # Interfaces pour Kafka (à implémenter)
│   └── ...
└── Main.kt                     # Point d'entrée
```

---

## 🔌 Préparation Intégration Kafka

### Interfaces Repository à Définir

Créer des interfaces vides que l'implémentation Kafka remplira plus tard :

```kotlin
// repository/GameRepository.kt
interface GameRepository {
    fun observeGames(): Flow<List<Game>>
    fun observeGameById(id: String): Flow<Game?>
    suspend fun publishGame(game: Game): Result<PublishResponse>
}

// repository/PlayerRepository.kt
interface PlayerRepository {
    suspend fun login(pseudo: String, password: String): Result<LoginResponse>
    suspend fun register(request: RegisterPlayerRequest): Result<RegisterPlayerResponse>
    fun observeOwnership(playerId: String): Flow<List<String>>
}

// repository/EditorRepository.kt
interface EditorRepository {
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun register(request: RegisterEditorRequest): Result<RegisterEditorResponse>
    fun observePendingRequests(): Flow<List<RegisterEditorRequest>>
}

// etc.
```

### Implémentation Mock pour le Développement UI

Pour permettre le développement UI sans Kafka :

```kotlin
// repository/mock/MockGameRepository.kt
class MockGameRepository : GameRepository {
    override fun observeGames(): Flow<List<Game>> = flowOf(emptyList())
    override fun observeGameById(id: String): Flow<Game?> = flowOf(null)
    override suspend fun publishGame(game: Game): Result<PublishResponse> = 
        Result.success(PublishResponse(/* ... */))
}
```

---

## ✅ Checklist de Développement

### Phase 1 : Setup & Composants de Base
- [ ] Configurer le thème (Colors, Typography, Shapes)
- [ ] Créer les composants réutilisables de base
- [ ] Mettre en place la navigation

### Phase 2 : Player Client
- [ ] Écrans d'authentification
- [ ] Store (Catalogue)
- [ ] Fiche jeu détaillée
- [ ] Bibliothèque
- [ ] Modals (notation, bug report)

### Phase 3 : Game Editor
- [ ] Écrans d'authentification éditeur
- [ ] Dashboard
- [ ] Gestion des jeux (CRUD)
- [ ] Gestion DLCs
- [ ] Gestion Patches
- [ ] Vue des bug reports

### Phase 4 : Platform Admin
- [ ] Login admin
- [ ] Dashboard admin
- [ ] Modération éditeurs (Inbox)
- [ ] Modération catalogue
- [ ] Monitoring live

### Phase 5 : Intégration Kafka
- [ ] Implémenter les repositories avec Kafka
- [ ] Connecter les ViewModels aux vrais flux
- [ ] Tests d'intégration

---

## 📝 Notes Importantes

1. **Boutons sans comportement** : Tous les boutons doivent être cliquables visuellement (effet hover/press) mais peuvent simplement logger l'action ou afficher un toast "Fonctionnalité à venir".

2. **Pas de fausses données** : Les listes peuvent être vides avec des `EmptyState` appropriés. Ne pas générer de données fictives.

3. **États de chargement** : Prévoir un état `isLoading` dans chaque ViewModel pour afficher des skeletons/spinners.

4. **Gestion d'erreurs** : Prévoir un état `error` pour afficher des messages d'erreur via Snackbar.

5. **Responsive** : Même si c'est du desktop, prévoir que la fenêtre puisse être redimensionnée.

---

*Document généré pour le projet Steam V2 Better du Pauvre - Janvier 2026*
