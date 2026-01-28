📱 1. Application : PLAYER CLIENT (Joueur)
L'application utilisée par le grand public pour jouer.

🔐 Authentification
S'inscrire (Joueur) : Création de compte avec Pseudo, Email, Mot de passe et Date de naissance (pour vérification d'âge).

Avro : RegisterPlayerRequest / RegisterPlayerResponse

Se connecter : Accès via Pseudo/Mot de passe.

Avro : LoginRequest / LoginResponse

🛒 Magasin (Store)
Consulter le catalogue : Afficher la liste de tous les jeux publiés disponibles.

Source : Lecture du topic game.

Voir la fiche d'un jeu : Afficher les détails (Description, Prix, Tags) et la liste des DLCs associés.

Source : Lecture des topics game et dlc.

Acheter un jeu ou un DLC : Envoyer une demande d'achat et recevoir la confirmation (ou refus si fonds insuffisants).

Avro : BuyGameRequest / BuyGameResponse.

Effet : Si succès, mise à jour locale de la bibliothèque via le flux ownership.

📚 Bibliothèque (Library)
Visualiser ses jeux (Client-Side Projection) : Afficher uniquement les jeux que le joueur possède.

Logique : Filtrer le flux game en croisant avec les IDs reçus dans le flux ownership (où playerId == MOI).

Filtrer/Trier : Trier par date d'achat ou par ordre alphabétique.

Lancer le jeu : (Action simulée) Bouton "JOUER" actif uniquement si le jeu est possédé.

📢 Social & Feedback
Noter un jeu : Laisser une évaluation (Note 1-5 étoiles + Commentaire). Possible uniquement si le jeu est possédé.

Avro : Evaluation.

Signaler un bug : Envoyer un rapport de bug technique aux développeurs.

Avro : BugReport.

Voir les avis : Consulter les évaluations laissées par les autres joueurs sur une fiche jeu.

Source : Lecture du topic evaluation.

🖥️ 2. Application : GAME EDITOR (Éditeur)
L'outil professionnel pour les studios de développement.

🔐 Authentification
S'inscrire (Studio) : Création de compte "Entreprise" ou "Indépendant".

Avro : RegisterEditorRequest / RegisterEditorResponse

Se connecter : Accès sécurisé à l'espace créateur.

Avro : LoginRequest / LoginResponse

🛠️ Gestion de Projet (Dashboard)
Voir mes créations : Lister les jeux créés par cet éditeur.

Source : Filtrage du topic game sur editorId.

Créer un brouillon de Jeu : Saisir les informations (Nom, Description, Prix initial).

Gérer les DLCs : Ajouter des extensions à un jeu existant.

Gérer les Patchs : Créer une note de version (ex: v1.0.2) pour une mise à jour.

🚀 Workflow de Publication
Publier un contenu : Envoyer une demande de publication pour un Jeu, un DLC ou un Patch.

Avro : PublishRequest (Utilise l'Union de types).

Recevoir la validation : Être notifié si la publication est acceptée ou refusée (ex: "Nom déjà pris").

Avro : PublishResponse.

UI : Afficher une erreur ou passer le statut du jeu à "Publié".

📈 Suivi
Lire les retours de bugs : Consulter la liste des BugReport envoyés par les joueurs concernant ses propres jeux.

⚙️ 3. Application : PLATFORM (Serveur / Admin)
Le cœur du système qui valide les règles métier.

🛡️ Modération & Validation
Valider les publications : Vérifier automatiquement qu'un jeu n'a pas le même nom qu'un autre.

Action : Si OK, écrire dans le topic game (Compact). Si KO, renvoyer une erreur.

Valider les achats : Vérifier les demandes d'achat.

Action : Si OK, écrire dans le topic ownership.

👮 Gestion des Inscriptions Éditeurs (Validation Manuelle)
File d'attente ("Inbox") :

Vue : Une liste des demandes d'inscription (RegisterEditorRequest) en attente de traitement.

Affichage par item : Carte affichant le Nom du Studio, l'Email de contact et la Description.

Actions de Modération :

Bouton "ACCEPTER" (Vert) :

Crée officiellement l'éditeur dans le système (Topic editor).

Envoie une RegisterEditorResponse avec isSuccess = true.

Bouton "REFUSER" (Rouge) :

Ouvre une petite boîte de dialogue pour saisir le motif du refus (Optionnel).

Envoie une RegisterEditorResponse avec isSuccess = false et le message d'erreur.

👁️ Monitoring
Flux en temps réel : Visualiser les logs des inscriptions, des connexions et des transactions financières (wallet virtuel si implémenté).

Gestion du catalogue : Possibilité de retirer un jeu de la vente (dépublication d'urgence).