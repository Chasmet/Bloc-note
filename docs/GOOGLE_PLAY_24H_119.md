# Publication Google Play — Sync 30 Bloc-note

Statut : **candidat en développement**, ne pas pousser sur main avant configuration réelle du produit et tests Play Console.

## Offre
- Installation gratuite et **24 heures d'essai complet** comptées au premier lancement.
- Après l'essai : paiement **unique** via Google Play Billing 9.1.0, **sans abonnement**.
- Produit non consommable ID : `blocnote_premium_lifetime`, montant souhaité : **1,19 EUR** pour la France, à configurer côté Play Console. Le prix effectivement débité est celui affiché par Google Play.
- Les notes restent sur le téléphone même si l'essai se termine. Export JSON libre depuis le paywall.
- Licence restaurable depuis Google Play grâce à `queryPurchasesAsync`; achat en attente ne donne aucun accès.

## Avant toute vente (bloquants)
1. Créer l'application `com.chk.blocnote` dans Play Console (compte Organisation « Sync 30 Numérique »).
2. Configurer et activer un produit intégré **non consommable** `blocnote_premium_lifetime` à 1,19 € (France), puis tester avec les comptes de test de licence.
3. **Vérification serveur indispensable** avant commercialisation : transmettre le `purchaseToken` au backend HTTPS partagé, vérifier Google Play Developer API, suivre remboursement et annulation, gérer notifications RTDN. Le client seul n'est pas une preuve résistante aux fraudes.
4. Essai local actuel : durée 24h en SharedPreferences avec horloge monotone, **réinitialisable via réinstallation/effacement des données** et non synchronisé entre appareils. Pour une limitation par compte Google, implémenter la date du premier usage sur un backend sécurisé avec authentification minimale, politique RGPD.
5. **Signature** : utiliser une clé d'upload sécurisée et Play App Signing. Le workflow de la branche construit un AAB **non signé**, uniquement pour inspection. Ne jamais publier un bundle non signé.
6. Créer et valider fiche Play Store, confidentialité/RGPD, formulaire Data safety, classification âge, captures d'écran et test interne; vérifier sauvegarde et mise à jour sans perte.
7. Vérifier les profils Android 13 / 16, comportement offline, achat annulé, en attente, validé, restauré et remboursé. Ne pas publier en production tant que ces scénarios ne sont pas approuvés.

## Changements techniques
- compileSdk / targetSdk 36, minSdk 23 (Billing 9.1 impose Android 6+).
- AGP 8.13, Gradle 8.13, Java 17.
- Essai et paywall : `TrialAccess.java`, `PaywallActivity.java`.
- Achat Google Play : `PlayBillingController.java`.
- Conserver `applicationId com.chk.blocnote`, base SQLite `bloc_note_ultra.db` et identifiant des notes. Sauvegarde avant toute migration.
- Branche : `feature/play-24h-lifetime-119-20261111`. La version publique sur `main` reste sans monétisation jusqu'à validation.

## Informations importantes
L'essai gratuit est **géré par l'application** ; Google Play ne facturera pas automatiquement après 24h. L'utilisateur déclenche personnellement l'achat. Aucun prélèvement périodique.

## Protection de l'installation existante
Le cache de signature de debug V2 n'a pas été retrouvé en novembre 2026. Le candidat est construit en package DEBUG isolé `com.chk.blocnote.trialtest` et n'est **pas** une mise à jour de l'application installée. Ne pas le substituer à `com.chk.blocnote`. Pour la mise en ligne, le package release est toujours `com.chk.blocnote` et doit être signé avec la vraie clé de production.

## AAB de publication (option manuelle)
Le workflow `.github/workflows/play-signed-aab.yml` sait produire un AAB signé pour les tests internes uniquement.
Configurer **sans publier de secret** les 4 variables GitHub Actions Secrets :
- `BLOCNOTE_UPLOAD_JKS_BASE64` (contenu de la clé d'upload JKS encodée en base64)
- `BLOCNOTE_UPLOAD_STORE_PASSWORD`
- `BLOCNOTE_UPLOAD_ALIAS`
- `BLOCNOTE_UPLOAD_KEY_PASSWORD`

Lancer manuellement le workflow sur la branche de préparation ; sans secrets, il refuse de créer un AAB signé. Ne pas réutiliser une clé de debug. Ne pas diffuser le bundle tant que la validation serveur d'achat, la fiche RGPD et Play Console ne sont pas approuvées.

## Audit paiements 11/11/2026
- Nouveau contrôleur Android : achat `PURCHASED` sur le téléphone **ne suffit plus** ; vérification HTTPS CHK Paiements obligatoire, puis restauration et accusé de réception Play.
- URL de vérification utilisée : `https://<passerelle-publique-dediee>/api/v1/play/verify`; vérification pré-achat : `/api/v1/play/health` répond `{ok:true,billingReady:true}`.
- La configuration de l'URL passe uniquement par Gradle `-PchkPaymentsVerifyEndpoint` ; la CI de l'AAB signé exige le secret `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL`. Pas de secret Google Play dans l'APK.
- L'infrastructure CHK Paiements connue est **privée Tailscale et en mode prototype** : elle n'est pas accessible aux clients Google Play sans passerelle HTTPS publique dédiée. Ne pas utiliser l'URL MCP universelle ni exposer le port local 8791.
- Ne pas publier tant que CHK Paiements n'a pas un vérificateur Google réellement opérationnel, les droits Google Play Developer API et les tests achats/restauration/remboursements passés.
