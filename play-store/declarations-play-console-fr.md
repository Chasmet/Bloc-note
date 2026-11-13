# Mes Notes & Dossiers — déclarations Play Console (base vérifiée)

Éditeur : **Sync 30 Numérique**. Identifiant Play : `4976373584470422373`. Package **public** retenu : `com.sync30.mesnotesdossiers`. APK personnelle distincte `com.chk.blocnote` (ne pas modifier).

> État de rédaction 13/11/2026. Ce document est une **fiche de préparation**, pas la preuve de validation des formulaires. Les déclarations officielles doivent correspondre à la version réellement publiée et être relues dans Play Console avant l'envoi.

## Faits techniques vérifiés sur la branche publique

- Permissions Manifest : `android.permission.INTERNET` et `com.android.vending.BILLING` uniquement. Pas de permission de localisation, caméra ou microphone dans ce manifeste.
- `android:allowBackup="true"` : les sauvegardes automatiques Android peuvent dépendre des réglages du système/du compte. Ne pas affirmer « aucune sauvegarde cloud possible ».
- Stockage de notes et dossiers : base SQLite locale (`NoteDatabase`) ; export/import JSON avec actions volontaires de l'utilisateur.
- Paiements : bibliothèque `com.android.billingclient:billing:9.1.0`, achat intégré non consommable `blocnote_premium_lifetime`. Le code transmet en HTTPS au vérificateur CHK Paiements : `packageName`, `productId`, `purchaseToken`. Aucune URL Render n'est imposée à cette vérification.
- Aucune bibliothèque publicitaire relevée dans `app/build.gradle`, aucune publicité déclarée dans Play Console.
- Le système de dictée Android est sollicité par l'appareil ; inspecter les éventuels services externes et leurs conditions avant de décrire la collecte.
- Sans achat, la licence n'est pas débloquée : `TrialAccess` gère les 24 heures locales ; `PlayBillingController` exige la vérification serveur pour une licence achetée.

## Déclarations déjà confirmées dans Play Console

- [x] Politique de confidentialité : URL enregistrée, tableau de bord indique la tâche comme terminée.
- [x] Publicités : **Non**.
- [x] Application gouvernementale : **Non**.
- [x] Fiche française : titre, description courte (68 caractères) et description complète (1 704 caractères) sauvegardés et relus après rechargement.

## Déclarations NON encore confirmées

- [ ] **Financial features** : une application de notes vendant un achat intégré n'est pas en elle-même un service bancaire, prêt, portefeuille, trading ou transfert d'argent. Sélectionner l'absence de fonctionnalités financières *uniquement* si la version finale confirme cette absence ; compléter le formulaire et vérifier le résultat enregistré.
- [ ] **Health features** : aucune fonction santé identifiée dans le code inspecté. Le formulaire a permis de sélectionner l'option « aucune fonction santé » mais l'étape suivante est restée bloquée sur `Loading…` ; **NON enregistré**.
- [ ] **Sign-in details/App access** : la réponse vraie est **Yes, certaines parties sont restreintes**, car achat unique / palier d'accès. La Console dit explicitement que les examinateurs ne peuvent pas payer ni utiliser une période d'essai pour tester. Il faut fournir un véritable moyen d'accès temporaire aux fonctions premium, testé dans l'application, puis consigner les instructions dans le formulaire privé. Ne pas déclarer « No restriction » et ne pas publier un code d'accès dans ce dépôt.
- [ ] **Data safety** : préparer les réponses après audit du code de version finale, du backend CHK Paiements et des services Google Play. Le `purchaseToken` et les identifiants produit/package sont envoyés au backend ; les contenus des notes sont locaux hors partage/export et sauvegardes système éventuelles. Ne pas cocher aveuglément « aucune donnée collectée ».
- [ ] **Target audience** : choisir l'audience réelle et revoir les obligations enfants/familles avant validation ; aucune tranche d'âge n'est décidée arbitrairement ici.
- [ ] **Content rating** : remplir l'IARC selon les fonctionnalités réelles de prise de notes, pas selon le contenu potentiel saisi par un utilisateur.
- [ ] **AD_ID** : aucune permission publicitaire relevée dans le manifeste, mais vérifier le manifeste **fusionné** et les dépendances avant validation.
- [ ] **Store settings** : catégorie Productivité, e-mail professionnel et site `https://sync30.onrender.com/numerique/` encore non enregistrés.
- [ ] **Store assets** : logo 512×512, bannière 1024×500 et cinq captures (kit GitHub et copies CHK) prêts, mais import dans Play Console encore non confirmé.
- [ ] **Statut trader / compte développeur Organisation** : titulaire à certifier après contrôle de l'identité.
- [ ] **Signature / release** : clé d'upload JKS CRÉÉE, certificat et signature d'un JAR de test VÉRIFIÉS (13/11/2026). Deux copies de JKS 4012 octets ont un SHA-256 identique. GitHub Secrets et AAB release signé TOUJOURS NON CONFIRMÉS. Clé privée conservée dans l'espace CHK Android du titulaire, non insérée dans le dépôt. Certificat public et instructions : `play-store/UPLOAD_KEY_SYNC30_MESNOTES_20261113.md`. Le workflow `play-signed-aab.yml` a échoué à `Restaurer cle d'upload uniquement depuis GitHub Secrets` (run 43223516271 puis échecs ultérieurs). Secrets attendus : `BLOCNOTE_UPLOAD_JKS_BASE64`, `BLOCNOTE_UPLOAD_STORE_PASSWORD`, `BLOCNOTE_UPLOAD_ALIAS`, `BLOCNOTE_UPLOAD_KEY_PASSWORD`, `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL` ; leurs **valeurs** ne doivent jamais figurer ici.
- [ ] **Paiement/test interne** : vérifier CHK Paiements HTTPS actif, Google Play API, Billing `PURCHASED/PENDING`, annulation, restauration, remboursement/révocation, temps d'essai et usage hors ligne ; pas de ventes réelles avant.

## Règles d'intégrité

1. L'application **ne doit pas dépendre de Render**. Render héberge seulement le site Sync 30 et la politique de confidentialité.
2. Ne pas toucher à la version personnelle déjà installée, à Rush Studio ni à d'autres projets.
3. Ne pas confondre compilation de code, AAB signé, dépôt test interne, soumission à Google et publication effective.
4. Toute déclaration « FAIT » exige relecture de son état persistant dans Play Console.
