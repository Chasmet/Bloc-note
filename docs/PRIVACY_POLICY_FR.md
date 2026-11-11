# Politique de confidentialité — Sync 30 Bloc-note
Dernière mise à jour : 11 novembre 2026 — **version de travail à publier après validation**.

**Responsable de l'application :** Sync 30 Numérique, France.
**Contact de support :** skypieachannel@gmail.com.
**Site :** https://sync30.onrender.com/numerique/

### Données saisies dans l'application
Les notes, dossiers, étiquettes et choix de personnalisation sont stockés dans une base de données locale SQLite sur l'appareil. Dans la version analysée, l'éditeur n'exploite pas de service de synchronisation propriétaire des notes vers ses serveurs. L'utilisateur peut créer un export JSON de ses données et choisir où le stocker.

### Sauvegardes Android
L'application autorise actuellement la sauvegarde de données par le système Android (`android:allowBackup=true`). Selon les réglages du téléphone et du compte Google, certaines données d'application peuvent être prises en compte par les mécanismes de sauvegarde et restauration de Google. Il serait donc inexact d'affirmer que les données ne sortent jamais du téléphone.

### Achat intégré Google Play
L'option premium utilise Google Play Billing. Google gère la confirmation de paiement et le compte de paiement. L'application consulte les achats autorisés afin de restaurer l'accès et traite des jetons d'achat ; elle ne lit pas les numéros de carte bancaire. L'intégration côté serveur pour valider les achats n'est pas encore mise en place : cette politique devra être actualisée avant sa mise en production.

### Reconnaissance vocale Android
Si l'utilisateur active la dictée, le service de reconnaissance vocale installé sur l'appareil peut traiter les paroles selon ses propres règles et paramètres. Ce service n'est pas une fonctionnalité de collecte de l'éditeur.

### Connexions réseau
L'application peut utiliser Internet pour consulter les nouvelles versions de test et pour communiquer avec les services de facturation de Google Play. La version analysée ne comprend pas de bibliothèque d'analyse publicitaire ou de suivi de comportement propriétaire.

### Conservation et contrôle
Les notes locales restent présentes jusqu'à leur suppression, leur export, leur sauvegarde Android ou la désinstallation, selon les options du téléphone. Le code PIN est une barrière d'interface et ne chiffre pas actuellement la base de données locale. L'utilisateur est invité à protéger son appareil et ses sauvegardes.

### Mise à jour de cette notice
Cette politique doit correspondre au comportement du binaire réellement envoyé sur Google Play et au formulaire « Sécurité des données » de Play Console. Contacter l'éditeur à l'adresse ci-dessus pour toute question.
