# Setup 
Pour lancer tous programe lancer la commande 
``` sudo docker compose up -d```
Tous les conteneurs doivent être lancer avant lexecution des programes.
# Struture 
Le projet est strurer avec 3 sous projet : 
- game-editor 
- platform 
- player  
C'est trois projet son donc les 3 projet qui doivent comuniquer ensemble grâceà kafka. 
De plus vous pouvez voir le dossier : 
- schema-lib la bibliotheque 
## La bibliotheque 
schema-lib est le dossier stockant tous les shcema avro. C'est shcema permet de sassurer des types transmi. 
L'ensemble des schema doivent être mis dans le dossier : 
- src/main/avro/
**IL NE DOIVENT SURTOUT PAS ETRE VIDE** sinon cella fait crash le programme. 
Pour crée une classe utilisable dans tous les programe fait cette comande dans le terminal de inteliji : 
``` ./gradlew :schema-lib:generateAvro ```
# Kafka 
Pour kafka nous avont 3 grosse tecno : 
- les consomeur 
- les produceur 
- les topics (lieux on sont envoyer tous les messages)
## Global 
Vous pouvez trouver dans ce fichier deux exemple de code kotlin : 
- un exemple de consomeur (celui qui consome les messages kafka)
- un exemple de produceur (celui qui produit les messages kafka)
Il faudra utiliser ce type de code a chaque fois pour envoyer les diférent messages. 
## Gestion des stream 
On utilise KStream pour gérer les stream kafka. Cella nous permet de faciliter le traitement des messages. Dans notre exemple il selectionne toute les personnes majeur et les envoie dans le bons topic. 

