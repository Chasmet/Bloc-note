> **ÉTAT ACTUALISÉ 14/11/2026 — PREUVE Play Console : 8/11 tâches terminées.** Les blocs ci-dessous datant du 13/11 sont des historiques, pas l'état courant. IARC, public cible 13+, Ads, Sign-in details, Santé, Finances, Gouvernement et politique de confidentialité ont été validés. Restent : Data safety, Store settings (coordonnées), Store listing. Pour le suivi technique/CI et les SHA-256 de l'AAB signé QA 2.5.6 (34006), voir `play-store/checklist-publication.md`. L'AAB n'a pas été envoyé sur Play. **Backend Windows CHK Paiements non déployé ; vente interdite jusqu'aux tests réels.**

# Mes Notes & Dossiers — déclarations Play Console (base vérifiée)

Éditeur : **Sync 30 Numérique**. Identifiant Play : `4976373584470422373`. Package **public** retenu : `com.sync30.mesnotesdossiers`. APK personnelle distincte `com.chk.blocnote` (ne pas modifier).

> État de rédaction 13/11/2026. Ce document est une **fiche de préparation**, pas la preuve de validation des formulaires. Les déclarations officielles doivent correspondre à la version réellement publiée et être relues dans Play Console avant l'envoi.

## Faits techniques vérifiés sur la branche publique

- Manifeste **source** : `android.permission.INTERNET` et `com.android.vending.BILLING`. Inspection du manifeste **fusionné dans l'AAB signé du 13/11/2026** : présence supplémentaire de `android.permission.ACCESS_NETWORK_STATE` (dépendance). `AD_ID`, localisation, caméra et microphone non détectés. Ne pas se fonder uniquement sur le manifeste source pour la déclaration Play.
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


## Accès examinateur — développement ajouté le 13/11/2026

- [x] Code Android public (branche Play seulement) : bouton **« Accès examinateur Google Play »** dans l'écran « Mon accès : essai 24h / achat unique » accessible depuis le menu. Cette fonction n'est pas limitée à la fin de l'essai.
- [x] `ReviewerAccess` compare uniquement le **SHA-256 du code** (long code aléatoire privé). Il autorise temporairement l'éditeur et la création pendant 72 heures ; il **ne crée pas une licence achetée**, n'appelle pas Billing, ne modifie pas `TrialAccess.updatePurchase` et n'affecte pas l'application personnelle.
- [x] Le vrai code secret est stocké **uniquement dans la note CHK privée épinglée** : « CONFIDENTIEL — Code examinateur Google Play Mes Notes & Dossiers ». **Ne jamais copier le code, ses valeurs ou un lien vers cette note dans GitHub public.**
- [ ] Vérifier la compilation et tester réellement sur Android : code incorrect refusé, code valide accepté, accès à la création/édition sans achat, expiration à 72 h, aucune élévation de licence Billing.
- [ ] Enregistrer dans le formulaire **App access** privé de Google Play : restriction = oui, chemin Menu → Mon accès : essai 24h / achat unique → Accès examinateur Google Play, et coller le code depuis CHK privé. L'accès n'est pas un abonnement ni une vente.
- [ ] Recharger Play Console et confirmer les instructions sauvegardées. Le simple développement Android **ne valide pas** la déclaration officielle.


## Audit indépendant du bundle signé — 13 novembre 2026

- Artefact release exact issu du run [43485466135](https://github.com/Chasmet/Bloc-note/actions/runs/43485466135), artifact ID `14067930210` ; fichier extrait `app-release.aab`.
- Vérification locale `jarsigner -verify -verbose` : **exit code 0, `jar verified.`**.
- `keytool -printcert -jarfile` : SHA-256 signataire `95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB` ; correspond au certificat JKS upload CHK.
- `base/manifest/AndroidManifest.xml` dans l'AAB : package `com.sync30.mesnotesdossiers`, `versionCode=24014`, `versionName=2.4.14`, libellé `Mes Notes & Dossiers`.
- SHA-256 du **fichier AAB** : `bdbfecf0d767a816d3ec64b39e8e16fff9dbb9d37ff34053eecbb56fc62fec3b`.
- Permissions détectées dans le manifeste fusionné : `INTERNET`, `BILLING`, `ACCESS_NETWORK_STATE`. `AD_ID` non détectée. Pour les formulaires Google Play, contrôler la version exacte à soumettre et ses dépendances.
- Le bundle est **signé pour les tests internes**, mais n'a pas été téléversé sur une piste Play Console ni testé en conditions de vente réelles ; il ne prouve pas l'état de CHK Paiements.
