## Data safety enregistré — 14/11/2026

- Capture Preview : « Change saved. Send for review in Publishing overview. » Déclaration enregistrée, revue non envoyée.
- Production → Releases : brouillon existant « Untitled release », piste inactive, 0 pays. AAB signé 50017 déjà sauvegardé. La vérification publique des achats renvoie toujours HTTP 503.

## AAB commercial signé — 14/11/2026

- [Build réussi 43584944637](https://github.com/Chasmet/Bloc-note/actions/runs/43584944637), version 2.6.17 / 50017, package `com.sync30.mesnotesdossiers`. Signature, manifeste et structure vérifiés. SHA-256 AAB `c2549200a3ede70fcb185e65049133960cd94393ec835cac5551ddd9513d60ff`.
- L’URL HTTPS actuellement intégrée a renvoyé 503 au contrôle public. Play Console affiche 10/11, Data safety ouvert et envoi verrouillé. AAB récupéré, production non soumise.

## Vérification AAB commercial — 14/11/2026

Le moteur Windows actif écoute sur 8795. [Correctif de passerelle](https://github.com/Chasmet/CHK-serveur-Universal-Control/commit/6806a636d111bfcd89c40fd79925acd71229ea47) copié et relu sur le PC. [CI Windows réussie](https://github.com/Chasmet/CHK-serveur-Universal-Control/actions/runs/43584151724). Le nouveau contrôle du port HTTPS public 10000 renvoie 503. Activation publique non confirmée, dernier run commercial en échec, aucun AAB commercial généré. Reprendre après confirmation de `billingReady=true` à l’URL publique et configuration de `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL`.

## État vérifié après sauvegarde des coordonnées — 14/11/2026

Play Console : coordonnées et fiche validées après rechargement, 10/11. Data safety seul ouvert : historique d’achat sélectionné, ligne Start absente à l’étape 4, sauvegarde sur Submitting. Production non soumise ; paiement Windows et AAB commercial en attente.

## REPRISE DU 14 NOVEMBRE 2026 — CONTRÔLES ET CORRECTIFS

- **Play Console** : compte Sync 30 Numérique vérifié comme **Organization account**. Le statut commerçant affiche **Action required**. Le package `com.sync30.mesnotesdossiers` est affiché en **Draft**. Tableau de bord **8/11**, avec contacts, fiche et Data safety encore ouverts. Catégorie **Productivity** confirmée. Une nouvelle navigation dans Store settings retrouve les champs de contact vides. L’ancien onglet contient les valeurs dans une modale désactivée, ce qui ne prouve pas leur sauvegarde. Les clics ne sont pas confirmés par le WebView CHK en arrière-plan.
- **Production** : **Inactive**, brouillon présent, 0 pays/régions, aucun envoi en examen, aucune publication. Aucune piste de tests internes créée. Ne pas importer l'AAB QA 34006 avec les achats désactivés.
- **Workflow commercial** : [9dbc92f](https://github.com/Chasmet/Bloc-note/commit/9dbc92ffd1c0c8c2eabe95f64e2cbc84232d4615). Base `50000 + GITHUB_RUN_NUMBER`, version `2.6.N`. Comparer le numéro calculé au plus grand versionCode déjà importé. Validation du certificat, de l'AAB, des permissions, du package, du SDK et de la déclaration Billing avant artefact. Le [run 43579311444](https://github.com/Chasmet/Bloc-note/actions/runs/43579311444) vérifie la clé et confirme **secret `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL` absent**. Compilation commerciale sautée, aucun artefact. [Compilation Android de contrôle](https://github.com/Chasmet/Bloc-note/actions/runs/43579311437) réussie.
- **Paiements Windows** : les huit fichiers de la branche serveur au commit `ca2b78a` ont été copiés et relus à l’identique dans `CHK-Paiements-GooglePlay/release-ca2b78a` du dossier Windows CHK-Workspace. 16 tests locaux des paiements, de la passerelle et du sidecar réussis. Le connecteur Windows permet le transfert mais ne permet pas de démarrer le service. Compte de service Google, activation du sidecar existant et HTTPS public restent à vérifier. Le contrôle HTTPS externe n’a pas obtenu `billingReady=true`. Le dossier préparé ne remplace pas l’instance ni ses données.
- **Confidentialité** : la [politique publique](https://sync30.onrender.com/politique-confidentialite-blocnote.html) précise la conservation de l’historique et la procédure de demande d’effacement. Déploiement statique Render `dep-dbs2dduk1f9s7383de7g` **live**, texte relu sur l’URL publique. Dans Data safety, l’étape 2 a été renseignée avec collecte, HTTPS, absence de compte et URL de demande d’effacement. Google Play reste sur « Checking URLs… ». L’enregistrement du brouillon n’est pas confirmé. Les types et usages des données restent à finaliser.
- **À reprendre** : ouvrir CHK Agent Browser au premier plan pour terminer la fiche, les contacts et Data safety, puis vérifier le statut commerçant et le produit. Sur Windows, exécuter la mise en service des correctifs dans le service CHK Paiements existant, avec ses données et le compte de service Google. Vérifier `billingReady=true` en HTTPS, renseigner le secret et relancer le workflow. Offre gratuite à télécharger, essai 24 h, achat non consommable de 1,19 €, anciennes notes lisibles et exportables. Dépôt direct en production après contrôle du bundle.

### Reprise après ouverture du navigateur CHK

- Data safety a passé le contrôle d’URL. « Purchase history » a été coché et la page est passée à l’étape 4/5. La section « Data usage and handling » est restée sans intitulé ni bouton Start et Next était désactivé. Aucune sauvegarde finale confirmée.
- Dans la fiche, les textes FR, les deux visuels principaux et quatre captures étaient toujours présents. Le clic Save dans Review a désactivé le bouton, mais la tâche reste ouverte sur le tableau de bord rechargé. Les coordonnées restent également à enregistrer.
- Nouvelle lecture du tableau de bord : 8/11. Le canal Local 3 a déclaré le WebView en arrière-plan malgré l’ouverture signalée sur le téléphone. Le contrôle HTTPS du backend a renvoyé HTTP 503 depuis l’environnement de vérification.

---

## TENTATIVE DE SOUMISSION DIRECTE — 14/11/2026

- [x] Demande explicite : **soumettre maintenant en production**, sans piste interne, **sans supprimer l'essai gratuit 24 h** ; distribution gratuite avec achat intégré unique à 1,19 €.
- [x] Play Console accédée en direct via CHK Browser Local 3 : piste **Production = Inactive**. Bouton `Create new release` actif sur la page `/tracks/production`, navigation réalisée vers `/tracks/4697558183771309621/create`. La page de création reste à « Loading Google Play Console » : aucun formulaire d'AAB exploitable, aucun envoi de release.
- [ ] Tableau de bord Play Console **8/11** : `Data safety`, `Select an app category and provide contact details`, `Set up your store listing` non validés. Section de publication : étapes `Select countries and regions`, `Create and roll out a release`, `Preview and confirm`, `Send release to Google for review`, `Publish` encore verrouillées.
- [ ] Fiche visuelle de production : Review ouverte (titre « Mes Notes & Dossiers », descriptions FR présentes, icône et bannière 1/1, 4 captures). `Save` envoyé, mais devient désactivé sans message de réussite ni persistance confirmée. Contacts Store settings (email/site) demeurent affichés dans une modale avec `Save` désactivé ; résultat métier NON CONFIRMÉ.
- [ ] Le seul AAB signé vérifié reste **QA 2.5.6 / versionCode 34006**, avec paiements désactivés. NE PAS le déposer pour la production. Le workflow commercial exige une passerelle HTTPS CHK Paiements confirmée et une nouvelle signature AAB.
- [ ] **Résultat réel : PAS SOUMIS, PAS PUBLIÉ**. Reprendre dès que la Console permet de sauver les champs, terminer Data safety, activer produit Play Billing et backend public, puis générer et déposer l'AAB commercial, sans piste de test interne.

---

## DÉCISION ÉDITEUR — 14 NOVEMBRE 2026 : PRODUCTION DIRECTE, ESSAI 24 H

- **Modèle commercial inchangé** : téléchargement Play Store déclaré **gratuit (Free)** ; l'application propose un **essai gratuit de 24 heures**, puis un **achat intégré non consommable unique à 1,19 €** pour restaurer et conserver les fonctions premium ; **pas d'abonnement**. Les notes existantes restent consultables après l'essai, sans forcer l'achat.
- **Choix explicite du propriétaire** : **ne pas publier sur la piste de tests internes** ; après préparation et conformité, soumettre **directement en production** à Google Play, sous réserve que la Console autorise cette piste pour le compte Organisation. Pas de période de test interne exigée par l'éditeur. Les vérifications techniques automatiques (CI, signature et sécurité) demeurent.
- **NE PAS utiliser en production** l'AAB QA signé (2.5.6, versionCode 34006) : ce bundle possède les achats désactivés ; il ne répond pas au modèle 24 h + 1,19 €. Il faut un **nouvel AAB commercial release signé** (versionCode supérieur) avec Billing Google Play et vérificateur CHK Paiements HTTPS fonctionnels.
- **Prérequis avant envoi** : terminer les **3/11 tâches Console** (Data safety, coordonnées/catégorie, fiche), vérifier l'identité du compte, le statut commerçant, l'achat intégré `blocnote_premium_lifetime` à 1,19 €, configurer et valider la passerelle existante, puis générer/contrôler l'AAB de production. La signature/CI d'un bundle QA ne signifie pas que la production est prête.
- Ne jamais transformer la fiche en **application payante à télécharger**, ce qui supprimerait la logique d'essai 24 h. Ne pas toucher au package personnel `com.chk.blocnote`.

---

## REPRISE VÉRIFIÉE — 14 NOVEMBRE 2026 (GOOGLE PLAY & PAIEMENTS)

- [x] **Google Play Console : 8/11 tâches validées**, relu sur tableau de bord. Les 3 tâches encore ouvertes : Data safety, « Select an app category and provide contact details », « Set up your store listing ». La piste interne est bloquée tant que les tâches initiales ne sont pas terminées.
- [ ] **Contacts fiche Play** : formulaire Store settings rempli avec l'e-mail professionnel et le site Sync 30 ; clic Save lancé, puis dialogue bloqué avec boutons désactivés. **Aucune preuve de persistance**, ne pas considérer la tâche terminée.
- [ ] **Data safety** : les réponses de base ont été saisies à nouveau puis « Save draft » est resté sur « Submitting... » dans CHK en arrière-plan. Aucune confirmation d'enregistrement ni soumission. Les données d'achat transmises au serveur et hachées ensuite sont des données collectées et non éphémères ; voir la politique Play et vérifier toutes les finalités et SDKs avant validation.
- [x] **AAB QA signé récupéré et vérifié** depuis GitHub Actions [43485466361](https://github.com/Chasmet/Bloc-note/actions/runs/43485466361), artifact `14067965145` (ZIP SHA-256 `4004ce8d5e803f9f76a7f570e6cdf18a3a1273fd62720131109c9b451b5457d5`). AAB extrait SHA-256 `c70a34e36b0d481d5a18ce9733c072c9322f56a15079d62ccdf32b0944feedd8`. Vérification indépendante `jarsigner -verify` → `jar verified.`, certificat upload SHA256 `95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB`. Manifest : package `com.sync30.mesnotesdossiers`, versionCode **34006**, versionName **2.5.6**. Copie ZIP et AAB extraite dans stockage privé CHK Android, `Sync30/GooglePlay/MesNotesDossiers/`. **Achats désactivés**, ne pas publier en production.
- [x] **Backend CHK Paiements / branche `feature/paiements-sync30-public-20261112`** : script Funnel PowerShell réparé (port public 10000 uniquement), workflow CI Windows désormais actif sur cette branche. Corrigé aussi `PaymentsHub.recordVerifiedGooglePlayPurchase` qui refusait le package public même après validation Google. Tests régression achat, enregistrement SHA-256, annulation/remboursement ajoutés. GitHub Actions SUCCESS [43571399030](https://github.com/Chasmet/CHK-serveur-Universal-Control/actions/runs/43571399030) : parser PowerShell + tests serveur.
- [ ] **Backend Windows NON déployé / Google Play Developer API/OAuth non validée / Funnel HTTPS public NON activé**. Aucun paiement réel ni test installé via piste Play. Ne pas activer vente.
- [x] **Politique de confidentialité** vérifiée en lecture sur Render : décrit jeton d'achat, SHA-256, Google Play API, sauvegardes Android et notes locales. À compléter avant publication : durée/conservation de l'historique de paiement et procédure de suppression appropriée.
- [ ] **Statut trader / Organisation, dossier complet et dernières captures** à vérifier puis envoi pour examen uniquement après l'ensemble des tests.

---

## REPRISE PLAY CONSOLE — PUBLIC CIBLE VALIDÉ, 13/11/2026

- [x] **Classification du contenu IARC** : statut **Completed**, soumis le 13 novembre 2026 à 22 h 51, vérifié directement dans Play Console.
- [x] **Advertising ID** : réponse **No** enregistrée et relue ; manifeste AAB signé sans `AD_ID`.
- [x] **Target audience and content** : tranches cochées et **enregistrées** : 13–15, 16–17 et 18 ans et plus. IMPORTANT : l'option **5 ans et moins avait été cochée** ; elle a été **décochée** et les trois tranches 13+ vérifiées. Résumé Google « Change saved », sans soumission de publication.
- [ ] **Data safety** : formulaire à l'étape **Preview 5/5**, déclarations préremplies incluant Financial info → Purchase history collecté, pas de partage déclaré, HTTPS transit, aucune suppression via demande. **Non soumis.** Contrôler vérification CHK Paiements, finalité, durée de conservation et transferts avant validation : la branche backend `Chasmet/CHK-serveur-Universal-Control` / `feature/paiements-sync30-public-20261112` a une CI verte, mais n'est pas déployée ni testée en production ; le serveur Windows et l'API Google Play ne sont pas validés.
- [ ] **Store settings** : catégorie Productivité vérifiée mais contacts professionnels e-mail/site non enregistrés ; statut trader à compléter.
- [ ] **Publication** : AAB commercial, Google Play Billing réel, piste interne et revue Google **non réalisés**. Ne pas publier avant test du vérificateur de licences.
- [x] Note de reprise CHK « ÉTAT DU PROJET » mise à jour. Navigateur CHK inaccessible pour commandes finales après bascule en arrière-plan, ne pas confondre « page affichée » et action effectivement enregistrée.

---

## REPRISE CONSOLE — 13 NOVEMBRE 2026, SOIR (POST-IMPORT)

Ce bloc est le **dernier contrôle effectif** et prévaut sur les anciennes cases historiques du fichier.

- [x] **Fiche Play enregistrée et relue sur Android via CHK Browser Local 3** : l'interface affiche **App icon 1/1**, **Feature graphic 1/1**, **Phone screenshots 4/8**. Le message « Your changes have been saved » est présent. La cinquième capture, préparée en local, n'est pas encore importée ou vérifiée dans Play.
- [x] **Titre et textes FR visibles à l'étape Review** : « Mes Notes & Dossiers », description courte « Prenez des notes et organisez vos dossiers et vos favoris simplement », description longue enregistrée.
- [x] **Catégorie Productivité** : enregistrée et retrouvée lors du contrôle CHK du 13/11 vers 20h55 (voir note maître CHK).
- [ ] **Déclaration IA des visuels** : l'option « Label assets as created or edited using AI » est sélectionnée dans Review. Dans la fenêtre « Label AI-generated assets », la bannière apparaît sélectionnée sur la capture du propriétaire. **Validation « Label assets and submit » NON confirmée** par CHK ; les captures d'écran réelles ne doivent pas être étiquetées IA à tort. Vérifier le logo puis finaliser la déclaration.
- [ ] **Accès Play Console** : nouveau clic de validation et création d'onglet non confirmés par CHK en arrière-plan, ce qui ne constitue PAS une sauvegarde ni une soumission.
- [ ] **Contacts Store settings, déclarations App access / accès examinateur, santé, finance, âge, IARC, Data safety, trader** : toujours à relire et enregistrer séparément.
- [ ] **Paiements / production** : CHK Paiements HTTPS, cinquième secret et achat/restauration/remboursement Google Play non validés ; AAB commercial non déposé ; aucune publication. **Ne pas activer de ventes.**
- [ ] **APK personnelle** `com.chk.blocnote` inchangée ; branche publique seule à modifier.

---

## KIT VISUELS — 7/7 PRÉPARÉS DANS CHK (13/11/2026)

Fichiers **copiés et vérifiés par SHA-256** dans le dossier privé CHK Android
`Categories/Images/application bloc note/VISUELS GOOGLE PLAY - OFFICIELS/`.
Les fichiers d'origine GitHub et CHK n'ont pas été modifiés.

| Zone à remplir dans Play Console | Fichier CHK clairement identifié |
| --- | --- |
| App icon, 512 × 512 | `00_LOGO_GOOGLE_PLAY_512x512.png` |
| Feature graphic, 1024 × 500 | `01_BANNIERE_GOOGLE_PLAY_1024x500.jpg` |
| Phone screenshot 1 — Accueil | `02_CAPTURE_01_ACCUEIL_NOTES_1080x1920.png` |
| Phone screenshot 2 — Favoris | `03_CAPTURE_02_FAVORIS_1080x1920.png` |
| Phone screenshot 3 — Dossiers | `04_CAPTURE_03_DOSSIERS_1080x1920.png` |
| Phone screenshot 4 — Archives | `05_CAPTURE_04_ARCHIVES_1080x1920.png` |
| Phone screenshot 5 — Éditeur | `06_CAPTURE_05_EDITEUR_1080x1920.png` |

Dans le même dossier, `00_LIRE_AVANT_IMPORT_GOOGLE_PLAY.txt` donne le rôle de chaque média et ses fichiers sources. Note épinglée CHK : « VISUELS GOOGLE PLAY — 7/7 nommés et prêts à importer ».

- [x] Sept médias CHK nommés et vérifiés.
- [ ] **Import Google Play non encore vérifié** : renseigner App icon, Feature graphic et les cinq Phone screenshots. Enregistrer et recharger les pages pour confirmer la persistance.
- [ ] Ne JAMAIS importer le dossier voisin « documents signés et autre » contenant des fichiers de signature privés.

---

## ÉTAT ACTUEL PRIORITAIRE — 13 novembre 2026, après correctifs examinateur

**Les anciennes cases de la liste ci-dessous décrivent l'historique. CE BILAN prévaut :**

- [x] Clé JKS d'upload vérifiée (SHA-256 certificat `95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB`), copies privées CHK Android contrôlées.
- [x] 4 **GitHub Actions Secrets** de signature renseignés et relus : `BLOCNOTE_UPLOAD_JKS_BASE64`, `BLOCNOTE_UPLOAD_STORE_PASSWORD`, `BLOCNOTE_UPLOAD_ALIAS`, `BLOCNOTE_UPLOAD_KEY_PASSWORD`. AUCUNE VALEUR de secret publiée dans le dépôt.
- [x] **AAB release signé pour tests internes** au commit code `3108ae00566622082cf1eff6ec1827a36b985847` : [run 43485466135](https://github.com/Chasmet/Bloc-note/actions/runs/43485466135) SUCCESS, artifact `14067930210`, expiration **20 novembre 2026**. Aucun paiement n'est activé sans endpoint CHK. Bundle NON déposé dans Google Play.
- [x] **AAB QA signé** sans ventes : [run 43485466361](https://github.com/Chasmet/Bloc-note/actions/runs/43485466361) SUCCESS, artifact `14067965145`.
- [x] **Android 10/16 + tests ReviewerAccess** : [run 43485466177](https://github.com/Chasmet/Bloc-note/actions/runs/43485466177) SUCCESS. Validation positive par code sur appareil encore à faire.
- [x] **Accès examinateur développé** : bouton dans `PaywallActivity`, code long dont seul le SHA-256 figure dans `ReviewerAccess`, accès 72 h et aucune fausse licence `purchased`. Code en clair UNIQUEMENT dans la note privée CHK « CONFIDENTIEL — Code examinateur Google Play Mes Notes & Dossiers ». Ne jamais placer ce code ici.
- [ ] **App access Play Console** : renseigner « certaines parties restreintes » et les étapes Menu → Mon accès : essai 24h / achat unique → Accès examinateur Google Play ; coller le code depuis la note privée CHK. Confirmer la persistance de l'enregistrement.
- [ ] **Store settings** : page relue le 13/11 ; catégorie **Not selected**. Enregistrer Productivité, e-mail professionnel, site et vérifier après rechargement. Le contrôle CHK du clic Edit était non confirmé lorsque le navigateur restait en arrière-plan.
- [ ] **Assets** : importer depuis le kit GitHub les 7 vrais visuels. Pas d'upload Play confirmé. L'icône et captures 04/05 ne sont pas encore vérifiées dans le dossier Images principal CHK ; une nouvelle tentative d'import automatique n'a pas confirmé leur présence.
- [ ] **5e secret** `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL` : ABSENT en attente de passerelle CHK Paiements HTTPS publiquement opérationnelle + Google Play Developer API (health ok/billingReady). Ancienne adresse Tailscale non accessible depuis un contrôle externe : état du serveur non concluant. Ne pas mettre Render en backend.
- [ ] **Autres déclarations Play** : santé, finances, IARC, public cible, Data safety (allowBackup + purchaseToken), AD_ID manifeste fusionné, statut professionnel/trader, identité Organisation. À valider et relire.
- [ ] Produit INAPP `blocnote_premium_lifetime` à **1,19 €** ; tester achat, annulation, restauration, remboursement/révocation, essai 24 h et export libre avant production.
- [ ] Piste interne Google Play, test device, revue et soumission **NON FAITS**. Aucune version publique ni vente.
- [x] **Dossier maître CHK** « PRIORITÉ 13-11-2026 — DOSSIER MAÎTRE MES NOTES & DOSSIERS » révision 4 et résumé court révision 30 actualisés. Préserver l'APK personnelle `com.chk.blocnote`.

---
# Play Console — vérifications de publication (Mes Notes & Dossiers)

## Fait dans le dépôt de préparation
- [x] Offre définie : 24 h de test, puis achat unique de 1,19 € (non automatique)
- [x] Code Google Play Billing (pas encore vérifié avec achat réel en Play Console)
- [x] Build Android API 36 et tests émulateurs Android 10/16 sur la branche publique : GitHub Actions 43170718093 et 43170718066 = SUCCESS
- [x] Dossier fiche Play Store français, politique de confidentialité brouillon
- [x] Icône 512x512, bannière 1024x500 et cinq captures 1080x1920 préparées dans le dépôt ; NON encore importées dans Play Console
- [ ] Signature de publication AAB sécurisée et testée

## À renseigner dans Play Console
- [x] Application « Mes Notes & Dossiers » créée dans Play Console le 12/11/2026, identifiant interne 4976373584470422373 ; package PUBLIC prévu : com.sync30.mesnotesdossiers (à confirmer par la Console ou le premier AAB accepté)
- [ ] Description courte / longue et fiche FR enregistrées
- [ ] Icône 512×512, bannière 1024×500, au moins 4 captures réelles 1080×1920
- [x] Politique de confidentialité publiée dans le dépôt du site et lien ajouté **dans l'application** (URL à vérifier sur Render)
- [x] Site public Sync 30 actualisé le 12/11/2026 : Render `sync30` (site statique exclusivement) a déployé le commit `ae956f0c8e83fe6461e609017e0bd0f61b34f10f` (deployment `dep-dbqqcb5bedkc73ee53k0`, statut `live`). Lecture de la politique en ligne confirme le texte CHK Paiements ; ne pas confondre avec l'enregistrement URL dans Play Console.
- [ ] Fiche « Sécurité des données » vérifiée selon code final (Billing, système, éventuel backend)
- [ ] Type « application » confirmé à la création ; catégorie Productivité, public cible et questionnaire de classification à finaliser
- [x] Déclaration Publicités : « Non, l'application ne contient pas de publicités » enregistrée dans Play Console ET confirmée après rechargement (12/11/2026, code sans SDK publicitaire vérifié)
- [ ] Produit non consommable Google Play `blocnote_premium_lifetime`, prix français 1,19 €, activé
- [ ] Vérification serveur achat / remboursement sécurisée avant vente réelle
- [ ] Comptes de testeurs internes et vérifications de commandes Pay/annulation/restauration
- [ ] AAB signé avec clé d'upload production (pas debug), Play App Signing et numéro de version unique
- [ ] Validation Google Play et publication

**Point technique** : l'essai 24 h actuellement local peut être remis à zéro après effacement des données ; pour un essai strictement limité par utilisateur, une vérification côté serveur sera nécessaire.

Documentation : https://github.com/Chasmet/Bloc-note/blob/feature/play-24h-lifetime-119-20261111/docs/GOOGLE_PLAY_24H_119.md

## Dossier livrable
L'archive `play-store/kit-google-play.zip` contient cinq vraies captures d'écran sur émulateur, le logo, la bannière, la fiche et la politique de confidentialité. Captures réalisées avec des données fictives, sans notes privées.

La politique HTML du kit correspond au texte du dépôt du site Sync 30 ; vérifier l'URL en ligne après déploiement Render et l'aligner avec le package public et le backend de facturation.

## Progression Play Console du 12 novembre 2026
- [x] App créée, identifiant interne `4976373584470422373`, téléchargement gratuit « Free ».
- [x] Politique de confidentialité : Google Play Dashboard marque désormais l'étape comme TERMINÉE le 12/11 (3/11 tâches validées). URL : `https://sync30.onrender.com/politique-confidentialite-blocnote.html`.
- [x] Déclaration gouvernement : « Non » enregistrée ET confirmée après rechargement, l'application est proposée par une entreprise privée.
- [ ] Déclaration accès examinateurs : **BLOQUANT**. Google indique explicitement ne pas pouvoir utiliser l'essai gratuit ni acheter pour examiner les sections payantes. La déclaration doit indiquer « Oui, accès restreint » et fournir un vrai mécanisme d'accès de démonstration sécurisé (à développer/tester) avant envoi.
- [ ] Déclaration de fonctionnalités financières/health, classification d'âge, sécurité des données et publicité ID Android à compléter après examen du code final. Un achat intégré ordinaire ne transforme pas l'app de notes en service bancaire.
- [ ] Compte marchand / statut de professionnel « Declare your trader status » : validation nécessaire du titulaire en Play Console.
- [ ] Pack d'images (logo 512, bannière 1024x500 et 5 captures 1080x1920) est en GitHub, pas encore chargé dans Play Console.
- [ ] AAB public release signé, compte de service CHK Paiements, passerelle HTTPS, test achat/restauration/remboursement non validés ; AUCUNE vente ni publication.
- [ ] Vérifier le package public fixé `com.sync30.mesnotesdossiers` dans Play Console et dans l'AAB accepté.

### Correction de vérification, même jour
Le simple clic « Save » avec bouton désactivé ne prouve PAS la sauvegarde distante. Pour la politique de confidentialité, la valeur saisie **disparaît après rechargement** (deux essais). Ne PAS affirmer la section terminée. Les déclarations Publicités/Non gouvernementale ont, elles, été relues et confirmées persistantes.

### Contrôle d'indépendance Render — confirmé le 12/11/2026
- Le propriétaire **autorise Render My Workspace uniquement pour le site statique** et exige que Mes Notes & Dossiers ne dépende pas de Render pour fonctionner.
- Application : notes/SQLite locales, sauvegardes locales et essai local ; le code Billing utilise Google Play + un vérificateur HTTPS CHK Paiements distinct du site Render.
- Vérification du dépôt : Gradle lit `chkPaymentsVerifyEndpoint` au build sans URL Render imposée. Aucun nouveau serveur Render pour l'application ; ne pas pointer Billing vers Render sans autorisation nouvelle.
- Les achats et restaurations nécessitent les services en ligne Google Play + CHK Paiements : ce n'est pas une dépendance Render. Les notes doivent rester consultables même sans Render (tester offline avant release).
- Page publique de confidentialité hébergée sur Render uniquement comme document du site éditeur, et mise à jour/vérifiée. Elle ne fait pas partie du moteur applicatif Android.

### Reprise autonome du 12/11/2026 — Play listing, médias, CI
- [x] 7 visuels téléchargés via CHK Browser Local 3 dans `Categories/Images/application bloc note/`, téléchargement complet + SHA-256 vérifié pour chacun :
  - `sync30-blocnote-icon-512.png` (512×512),
  - `sync30-blocnote-feature-1024x500.jpg` (1024×500),
  - `sync30-play-01-accueil-notes.png` à `sync30-play-05-editeur.png` (captures issues de la branche GitHub ; première vérifiée 1080×1920).
- [ ] **IMPORT GOOGLE PLAY NON CONFIRMÉ** : le bouton « Add assets » est visible mais le formulaire ne fournit pas de champ HTML file actif pour le connecteur CHK. Une tentative d'upload a renvoyé `unconfirmed` / « champ fichier absent ». Ne pas annoncer que les images sont dans Play Console.
- [x] Description FR courte (68 caractères) et longue (1 704 caractères) réellement présentes après rechargement, vérifiées le 12/11. Les visuels restent à importer et la fiche n'est pas terminée.
- [x] GitHub Actions candidat public du 12/11 : https://github.com/Chasmet/Bloc-note/actions/runs/43211756184 : SUCCESS. Cela ne prouve pas un AAB release signé.
- [x] Google Play Dashboard indique **3/11 tâches complétées** : publicités Non, gouvernement Non, politique de confidentialité validée.
- [ ] La publication nécessite encore : fiches persistées, photos réellement importées, déclarations réglementaires, validation du compte professionnel, clé d'upload, AAB RELEASE SIGNÉ, backend CHK Paiements HTTPS opérationnel et test d'achat/restauration.
- [ ] Le serveur CHK Paiements existant est sur PC Windows, hors Render ; l'app ne doit pas dépendre de Render. Ne pas lancer de vente si le PC/serveur n'est pas publiquement disponible et vérifié.

## Vérification de reprise 12/11/2026 (13 h+ heure Paris) — correctifs réellement persistants
- [x] URL **politique de confidentialité sauvegardée et retrouvée après rechargement** dans Play Console : `https://sync30.onrender.com/politique-confidentialite-blocnote.html`. La tentative antérieure « champ vide après rechargement » est désormais RESOLUE : la commande `browser_type` suivie d'un clic Save et de la relecture a fonctionné.
- [x] Description courte FR (68 caractères) et longue FR (1 704 caractères) **sauvegardées et retrouvées après rechargement** de la fiche Play Console. Les tentatives antérieures « texte non persistant » sont RESOLUES par la saisie directe. Les fichiers graphiques restent manquants.
- [x] Dashboard Play Console affiche désormais **3 des 11 tâches complétées**, dont politique de confidentialité, absence de publicités, absence d'activité gouvernementale.
- [x] Candidat public : compilation GitHub Actions réussie https://github.com/Chasmet/Bloc-note/actions/runs/43223514208 .
- [ ] **Test de signature release BLOQUÉ** : le workflow `.github/workflows/play-signed-aab.yml` a été limité à la branche publique candidate, lancé automatiquement sur push (aucune publication). Run https://github.com/Chasmet/Bloc-note/actions/runs/43223516271 : échec à « Restaurer clé d'upload uniquement depuis GitHub Secrets ». Les 4 secrets d'upload ne sont pas tous disponibles au workflow ; aucun AAB release signé n'a été produit. Noms attendus : `BLOCNOTE_UPLOAD_JKS_BASE64`, `BLOCNOTE_UPLOAD_STORE_PASSWORD`, `BLOCNOTE_UPLOAD_ALIAS`, `BLOCNOTE_UPLOAD_KEY_PASSWORD`. Ne pas enregistrer de mots de passe ou clés en clair. Le secret d'URL de vérification `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL` devra ensuite exister et pointer vers le serveur CHK Paiements hors Render.
- [ ] **Visuels non importés** : 7 fichiers locaux CHK sont valides et vérifiés (logo, bannière, 5 captures) ; mais Play Console n'expose pas de champ HTML `input[type=file]` exploitable dans le navigateur CHK autonome. Un clic sur « Add assets » n'a pas confirmé de sélection ou d'envoi. Ne pas affirmer que les captures figurent dans la fiche.
- [ ] Catégorie Productivité, coordonnées de contact et autres formulaires à terminer ; ne pas certifier une déclaration dont les choix n'ont pas été validés.
- [ ] Vente réelle bloquée tant que signature, vérificateur HTTPS CHK Paiements, compte Play Billing et achats de test ne sont pas vérifiés. Aucun déploiement en production ni AAB interne Google Play.

### Nouvelle vérification du 12/11 à 13 h locale (reprise)
- [x] Après nouvelle saisie et rechargement, descriptions courte et complète **persistantes** sur la page Store listings.
- [x] Dashboard Google Play : politique de confidentialité cochée ; total 3/11 tâches.
- [ ] Workflow AAB signé lancé par le push GitHub le 12/11, **ÉCHEC** à l'étape « Restaurer cle d'upload uniquement depuis GitHub Secrets », AVANT compilation du release : https://github.com/Chasmet/Bloc-note/actions/runs/43223516271 . Il manque au moins une partie des informations JKS upload ; ne pas utiliser la clé debug.
- [x] Script PowerShell préparé, sans valeurs secrètes, sauvegardé dans le dépôt et dans CHK-Workspace/CHK-Paiements-GooglePlay/windows/prepare-upload-key.ps1. Ne pas dire exécuté. Il utilise le JDK keytool et gh CLI en mode interactif, demande un support distinct pour sauvegarde et envoie les secrets par stdin sans les afficher.
- [ ] Exécuter et tester le script SUR LE PC avec le compte propriétaire, vérifier SHA-256 et sauvegarde externe, puis seulement relancer le AAB signé.

### Contrôle final du 13 novembre 2026
- [x] Identité publique dans le code corrigée : `MainActivity` n'affiche plus « Bloc Note Ultra » ni « Version de test » ; ressource `app_name` devient « Mes Notes & Dossiers ». Les commits sont `9e8d8f21` et `dc54502c` sur la seule branche publique ; APK personnelle intacte.
- [x] Compilation candidate du code final : SUCCESS https://github.com/Chasmet/Bloc-note/actions/runs/43361926027 ; audit Android 10/16 : SUCCESS https://github.com/Chasmet/Bloc-note/actions/runs/43361926137.
- [ ] AAB RELEASE signé : FAILED https://github.com/Chasmet/Bloc-note/actions/runs/43361926117. Étape en échec : restauration clé d'upload depuis GitHub Secrets. Pas de fichier JKS public confirmé ni AAB uploadable ; ne pas réutiliser une clé debug.
- [ ] Formulaire Santé : option de non-fonctionnalités sélectionnée une fois, mais passage étape 2 bloqué sur « Loading… » ; après rechargement, formulaire non sauvegardé. NON VALIDÉ.
- [ ] Formulaire accès des examinateurs : « Yes » sélectionné temporairement car paiement restreint certaines fonctions. L'interface « Add details » n'a pas ouvert ses champs ; aucune instruction d'accès enregistrée. Un mécanisme de revue sécurisé et réellement testable doit être fourni avant soumission.
- [ ] Catégorie, contacts, sécurité des données, finances, public cible, IARC, visuels et statut professionnel toujours à vérifier/terminer dans Play Console.
- [x] Fiche technique des déclarations créée : [declarations-play-console-fr.md](./declarations-play-console-fr.md). Ce document est préparatoire et n'est pas une déclaration effectuée.
- [ ] Essai de 24h actuel local : effacer les données peut redémarrer l'essai ; cette limite est documentée et nécessite une décision produit pour l'application commerciale (ne pas prétendre une protection côté compte Google).

### Audit direct du bundle Android non signé — 13/11/2026
- [x] Artefact CI téléchargé depuis `43361926027` (GitHub artifact `blocnote-play-candidate-24h-43`, ID 14008358419). Archive contenant `apk/debug/app-debug.apk` et `bundle/release/app-release.aab`.
- [x] Inspection du manifeste dans le module `base` de l'AAB : identifiant `com.sync30.mesnotesdossiers`, titre `Mes Notes & Dossiers`, permissions `android.permission.INTERNET` et `com.android.vending.BILLING` détectés.
- [x] Vérification locale par `jarsigner -verify -verbose` : réponse exacte **`jar is unsigned.`**. Aucun certificat/signature JAR dans `META-INF`. Ce fichier est un bundle candidat non signé, **NON ACCEPTABLE pour dépôt comme release Play**.
- [ ] Clé JKS upload publique à créer/retrouver en coffre sécurisé et sauvegarder ; renseigner les quatre GitHub Secrets JKS/mots de passe/alias. Conserver le certificat SHA-256 public. Ne jamais rendre le JKS ou mot de passe public.
- [ ] Publier une version AAB release signée et vérifier `jarsigner`, package, versionCode et certificat AVANT import test interne.

### Note Play Console — tentative de correction de la description courte
- La Console avertit « Should not contain special characters » pour le texte initial contenant notamment le symbole monétaire `€`.
- Texte SEO sobre retenu et enregistré **dans GitHub seulement** : « Prenez des notes et organisez vos dossiers et vos favoris simplement » (68 caractères), fichier `fiche-play-store-fr.md`, commit `b17a6aa1`.
- Tentative de saisie dans Play Console : valeur visible dans le formulaire, puis bouton « Save as draft » resté sur `Submitting…` ; après nouvelle navigation, l'ancien texte reste dans le champ. **Nouvelle description NON persistée**, ne pas la déclarer validée.


### Intervention vérifiée du 13/11/2026 — lecture hors ligne et reprise CHK
- [x] Sur la branche publique UNIQUEMENT, les notes existantes restent consultables en **lecture seule** après les 24 heures d'essai : `ReadOnlyNoteActivity` + contrôle d'accès conservé pour la création/modification. Commit final `c6d024f58e9d4162c9c8e79056f241da0c7e21f8`. Package personnel et données `com.chk.blocnote` inchangés.
- [x] CI candidate après correctif : **SUCCESS**, https://github.com/Chasmet/Bloc-note/actions/runs/43369224569 ; émulateurs Android 10/16 : **SUCCESS**, https://github.com/Chasmet/Bloc-note/actions/runs/43369224739. L'achat réel, la révocation et le mode hors ligne sur téléphone restent à tester.
- [ ] Signature AAB de ce code : workflow https://github.com/Chasmet/Bloc-note/actions/runs/43369224667 **FAILED** par absence de secrets JKS ; aucun AAB release signé, aucune piste interne.
- [ ] Assets Play Console : dossier CHK `Categories/Images/application bloc note` contient actuellement la bannière et captures PNG 01–03. Le logo et les captures 04–05 ont été retrouvés dans les **Téléchargements** CHK, pas dans ce dossier. L'import Files via Local 3 échoue avec « Choisis un fichier ou dossier ». **Ne pas confondre disponibilité en Téléchargements et upload Play validé.**
- [ ] Description courte SEO saisie dans Play Console, mais Save as draft reste sur « Submitting… ». Relecture après rechargement EFFECTUÉE : l'ancienne description courte « Notes, dossiers et favoris. 24 h d'essai puis achat unique à 1,19 €. » réapparaît. La proposition SEO n'est PAS enregistrée ; il faut résoudre la soumission avant la fiche Play.
- [x] Notes CHK de reprise et résumé actualisés ; aucune clé ni mot de passe transmis.
- [ ] Toujours bloqués : catégorie/contacts, images, déclarations, accès examinateur sécurisé, clé JKS, CHK Paiements HTTPS+Google Play Developer API, produit Play, achats de test et soumission.


### Nouvelle clé d'upload créée et archivée sur Android — 13/11/2026
- [x] **Clé JKS d'upload créée et testée** pour le futur package public `com.sync30.mesnotesdossiers` ; certificat SHA-256 `95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB` ; alias `sync30_mesnotes_upload`.
- [x] Copie **vérifiée** dans le stockage privé CHK Android : `Sync30/Signatures/MesNotesDossiers/sync30_mesnotes_upload.jks` (4012 octets, SHA-256 de fichier `0c224b9615e77a99f455072c1e98e2811c324c6cc1c853240859f0bd1e525c79`). Le certificat public PEM est également présent dans ce dossier.
- [x] Fiche de reprise et empreintes non sensibles créées dans CHK Notes et dans [UPLOAD_KEY_SYNC30_MESNOTES_20261113.md](./UPLOAD_KEY_SYNC30_MESNOTES_20261113.md).
- [ ] **Sauvegarde chiffrée indépendante du téléphone non encore confirmée** ; identifiants secrets remis au propriétaire dans un fichier de transfert confidentiel distinct, à placer dans un gestionnaire de mots de passe chiffré (JAMAIS dans Notes ou GitHub public).
- [ ] Secrets GitHub non configurés ; AAB release avec cette nouvelle clé non signé et Google Play App Signing non encore associé à ce certificat. Les précédents échecs CI pour absence de clé datent d'avant cette création ; les Secrets restent à renseigner avant retest.
- [ ] CHK Paiements HTTPS et produit Play Billing non confirmés : NE PAS soumettre à la production.


### Reprise durable entre chats — état au 13 novembre 2026
- [x] **Note maîtresse CHK créée et épinglée** : « PRIORITÉ 13-11-2026 — DOSSIER MAÎTRE MES NOTES & DOSSIERS — SIGNATURE OK / PLAY À FINIR » ; contient liens, packages, signature, workflow, état des formulaires, paiements et ordre de travail. La note « ÉTAT DU PROJET — Résumé 1 000 caractères » et les notes « REPRISE PLAY 12-11 » / « REPRISE 12-11 » / « SUIVI PROJET » / « BLOC NOTE — SIGNATURE... » ont été mises à jour avec l'état actuel.
- [x] Ancienne note « SUIVI PROJET » préservée dans le stockage privé CHK avant réécriture : `Sync30/Archives_CHK_Notes/SUIVI_PROJET_BlocNote_AVANT_MAJ_2026-11-13.txt` (écriture et SHA-256 confirmés). Ne pas se fier aux anciennes mentions « clé inexistante » ou « app Play non créée ».
- [x] Copies JKS du propriétaire contrôlées à deux emplacements privés CHK, 4012 octets, même SHA-256 `0c224b9615e77a99f455072c1e98e2811c324c6cc1c853240859f0bd1e525c79`. Certificat upload public correspondant ; utilisateur confirme l'accès privé. Cela **ne vaut pas** configuration GitHub Secrets, sauvegarde chiffrée indépendante vérifiée ni création d'un AAB signé.
- [ ] Prochaine intervention : injecter les secrets dans GitHub Actions par voie sécurisée, vérifier serveur CHK Paiements/HTTPS et produit Google Play, signer un vrai AAB RELEASE, tester en piste interne, compléter la fiche/les déclarations, puis soumettre. Aucun succès de publication constaté.


### Reprise exécutée — 13/11/2026 : vraie signature QA et configuration GitHub
- [x] Quatre secrets GitHub Actions de signature **créés et leurs noms relus après rechargement** dans `Chasmet/Bloc-note/settings/secrets/actions` : `BLOCNOTE_UPLOAD_ALIAS`, `BLOCNOTE_UPLOAD_STORE_PASSWORD`, `BLOCNOTE_UPLOAD_KEY_PASSWORD`, `BLOCNOTE_UPLOAD_JKS_BASE64`. **Ne jamais reproduire les valeurs**. La clé utilisée est exactement le JKS du propriétaire (SHA-256 de fichier `0c224b9615e77a99f455072c1e98e2811c324c6cc1c853240859f0bd1e525c79`).
- [x] Workflow séparé **QA interne SANS paiements** ajouté : `.github/workflows/play-internal-qa-signed.yml`. Le produit Premium reste désactivé en l'absence d'endpoint CHK Paiements ; aucune publication automatique.
- [x] **AAB RELEASE signé vérifié**, uniquement pour QA interne : GitHub Actions SUCCESS [43381391961](https://github.com/Chasmet/Bloc-note/actions/runs/43381391961), artifact `14015929085` nommé `sync30-mesnotes-qa-signed-INTERNAL-ONLY` (rétention jusqu'au 20/11/2026). Archive contenant `app-release.aab`, versionName **2.5.1**, versionCode **34001**, package `com.sync30.mesnotesdossiers`. `jarsigner -verify` confirme **jar verified**, certificat upload SHA-256 `95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB`. SHA-256 du bundle : `28025d15e3cf31ce8ee64af0e4a8810e903da46e0b81d5eaf22f964df3b300d8`.
- [x] Le workflow commercial `.github/workflows/play-signed-aab.yml` conserve sa vérification HTTPS `billingReady=true` AVANT signature d'une version prête à vendre. Son versionCode est désormais supérieur à celui de la QA (base 50000 au lieu de 24000), afin de préserver les futures mises à jour après piste interne. **Ne pas utiliser le workflow QA pour la production.**
- [ ] **PAS encore de dépôt AAB en piste interne Play Console**, Play App Signing pas confirmé, aucun test install/achat Play réel. Le bundle QA est signé mais achats désactivés. Le test interne commercial nécessitera l'URL HTTPS CHK Paiements dans le cinquième secret `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL`, le serveur et le produit Play opérationnels.
- [x] Bug serveur CHK Paiements corrigé SUR BRANCHE dédiée `feature/paiements-sync30-public-20261112` : la passerelle publique accepte désormais `com.sync30.mesnotesdossiers` en plus de `com.chk.blocnote`, et vérifie exactement le `packageName` de la réponse. Commit `de0d45325d66c112625ae38acb4ef5c14e506910` + tests `173a93816e59b1536b6c70bd9b4d2b857795694d`. CI SUCCESS [43382142668](https://github.com/Chasmet/CHK-serveur-Universal-Control/actions/runs/43382142668). **Pas déployé sur le serveur Windows ; HTTPS public/Google Play Developer API non validés.**
- [ ] Fiche, visuels, catégorie, contacts et formulaires Play Console, accès examinateur sécurisé, produit 1,19 €, tests achat/restauration/annulation et soumission restent à faire. La politique et les déclarations qui étaient à 3/11 ne doivent pas être présumées modifiées.

### Vérification réelle Play Console — 13 novembre 2026, 20h55
- [x] **Catégorie PRODUCTIVITY enregistrée** dans Store settings : la valeur « Productivity » subsiste après rechargement. Google affiche « Change saved. Send for review in Publishing overview » : modification en attente de revue, pas encore publiée.
- [x] **Description courte corrigée et persistante** dans Default store listing : « Prenez des notes et organisez vos dossiers et vos favoris simplement ». Le bouton Save as draft a confirmé l'enregistrement et une nouvelle navigation a retrouvé ce texte.
- [ ] Store listing contact details (e-mail et site professionnel) : non encore enregistrés, champs vides au contrôle. Les sélecteurs de contact et le picker Add assets sont difficiles à piloter via CHK en arrière-plan ; ne pas inventer de sauvegarde.
- [ ] Icon 512, banner 1024×500, 5 captures : aucun upload confirmé. Sources présentes dans `play-store/assets` et `play-store/screenshots` du dépôt ; préserver les captures réelles.


## Incident Play SDK minimum — 14/11/2026

- Le dépôt du bundle 2.6.17 / versionCode 50017 a été refusé par Google Play Automatic Protection : minSdk 23 au lieu de 24 minimum.
- Correction isolée sur la branche PUBLIQUE `feature/play-sync30-mesnotesdossiers-20261112` : `app/build.gradle` minSdk 24 (commit `8b6b051`). La version personnelle `com.chk.blocnote` n'est pas modifiée.
- Contrôle bloquant ajouté à `.github/workflows/play-signed-aab.yml` : le manifeste d'un bundle signé doit déclarer minSdk >= 24, avant livraison (commit `557ebbe`).
- Run signé de correction : https://github.com/Chasmet/Bloc-note/actions/runs/43588185140 . Ne valider SUCCESS qu'après lecture du statut final et de l'artefact.
- Ne pas réimporter l'ancien AAB `Sync30_MesNotes.aab` / 50017 : invalidé par Google. Effacer seulement ses lignes d'erreur dans la console, sans toucher aux archives ou données de l'application personnelle.
- La vérification de paiement CHK HTTPS renvoyait HTTP 503 au dernier contrôle ; **aucune vente ni publication avant `billingReady=true` et contrôle d'achat, restauration, annulation, remboursement.**
- Prévol obligatoire : vérifier dans l'AAB réel package, minSdk >= 24, targetSdk conforme, versionCode supérieur, certificat d'upload, Google Play Billing, endpoint de vérification HTTPS vivant, et réponses des formulaires Console.


## Contrôle final de la production — 14 novembre 2026

- **FAIT / RELU dans Play Console** : AAB de production `com.sync30.mesnotesdossiers` versionName **2.6.19**, versionCode **50019**, Android API **24+**, target **36**, bundle affiché `Enhanced` et accepté dans le brouillon de production. L'ancien AAB 50017 a été rejeté (minSdk 23) et ne doit pas être utilisé.
- **FAIT / RELU** : zone de diffusion **14 pays** sauvegardée par l'éditeur et relue sur la piste Production : Belgique, Canada, Djibouti, Égypte, France, Gabon, Gambie, Maroc, Sénégal, Togo, Tunisie, Émirats arabes unis, Royaume-Uni et États-Unis.
- **FAIT / RELU** : Play Console « Preview and confirm » validé et brouillon enregistré ; message Google « Your change has been saved. To send it to Google for review, go to Publishing overview. » Le tableau de bord affiche 3/5 étapes de la production complètes ; restent soumettre à examen puis publier. Aucune mise en examen confirmée.
- **BLOQUÉ — VENTES** : le point de contrôle HTTPS `https://chk-universal-1.taild9e4de.ts.net:10000/api/v1/play/health` n'est pas joignable depuis CHK Agent Browser Android (erreur `net::ERR_CONNECTION_CLOSED`) ; l'environnement d'inspection externe ne résout pas ce domaine privé. Ancien contrôle renvoyait HTTP 503. Pas de preuve du prérequis `billingReady=true`, ni de vérification Google Play Developer API, de prix locaux actifs pour `blocnote_premium_lifetime`, ni d'achat / restauration / remboursement validés.
- **DÉCISION DE SÉCURITÉ** : ne pas cliquer sur l'envoi final d'examen pour une application payante en fonctions premium tant que le service de validation d'achat n'est pas sain. L'éditeur maintient Free + essai 24 h + achat intégré unique 1,19 € sans abonnement ; la fiche ne doit pas devenir « app payante à télécharger ».
- **PRODUCTION** : brouillon prêt mais **non soumis / non publié** au dernier contrôle. La version personnelle `com.chk.blocnote` reste hors périmètre.


## Reprise publication Google Play — AAB 50022 (14/11/2026)

- **FAIT** : paiement Android remplacé sur la branche publique par un flux direct Google Play Billing sans dépendance au serveur CHK Paiements, commit `ef96b0a`. La validation côté client est moins résistante à la fraude que la vérification serveur. Ne pas prétendre que la vente réelle est testée.
- **FAIT** : suppression du paramètre endpoint serveur devenu inutilisé (commit `64cf663`) et correction du workflow de release (commit `394f0f7`).
- **TESTÉ** : GitHub Actions signé run `43591257475` **SUCCESS**, artefact `mesnotesdossiers-production-v50022` (code 50022, name 2.6.22, signature publique conforme).
- **TESTÉ** : AAB 50022 transféré et extrait sur CHK Android sous `Sync30/GooglePlay/MesNotesDossiers/Production_50022/app-release.aab`, taille 2 497 760 octets, SHA256 `9c61b6c7b1109c4b95296142183e7fe5f647a6188fd9ecec1dd9a39d2a361038` confirmé sur l'Android.
- **FAIT** : AAB 50022 importé dans Google Play Console, validé par Play (Enhanced, min API 24, target 36). Release notes FR décrivant le passage à Google Play Billing enregistrées.
- **FAIT, STATUT OBSERVÉ** : dans **Publishing overview**, Google Play affiche `Changes in review` avec **Production 50022 (2.6.22), Start full rollout**. Les contrôles automatiques affichent `Running quick checks for commonly found issues` ; la page précise que l'envoi à l'examen suit l'achèvement favorable de ces contrôles.
- **NON FAIT** : ne pas déclarer 50022 publiée / active tant que la piste Production n'indique pas `Active / Latest release 50022`. La précédente 50019 était active au début de cette intervention.
- **À TESTER** : état actif et tarif local Google Play du produit unique `blocnote_premium_lifetime` (prix de référence 1,19 EUR), achat, annulation, remboursement et restauration sur plusieurs comptes ; confirmer au besoin la cohérence Data Safety. Le client Billing seul ne remplace pas une vérification serveur antifraude.
- **AUCUN CHANGEMENT** de package/signature et pas de suppression des données de l'application personnelle `com.chk.blocnote`.


## Statut final observé — 14 novembre 2026 14:19

- **FAIT / VÉRIFIÉ DIRECTEMENT SUR PLAY CONSOLE** : piste Production `Active`, `Latest release: 50022 (2.6.22)`, 14 pays et régions, 0 installation affichée au moment de la lecture. Cela **remplace** le statut antérieur `Changes in review`.
- AAB signé 50022 **conservé dans CHK** : `Sync30/GooglePlay/MesNotesDossiers/Production_50022/app-release.aab`, SHA-256 `9c61b6c7b1109c4b95296142183e7fe5f647a6188fd9ecec1dd9a39d2a361038`, run GitHub Actions commercial `43591257475` SUCCESS.
- JKS **déjà créée et fonctionnelle**, ne jamais en recréer : `Sync30/Signatures/MesNotesDossiers/sync30_mesnotes_upload.jks`, SHA-256 JKS `0c224b9615e77a99f455072c1e98e2811c324c6cc1c853240859f0bd1e525c79`. Les quatre GitHub Secrets de signature sont fonctionnels ; JAMAIS exposer leur valeur.
- Prochaine version **strictement supérieure à 50022**, sur la branche `feature/play-sync30-mesnotesdossiers-20261112`, `.github/workflows/play-signed-aab.yml` ; contrôles `minSdk>=24`, `targetSdk`, package et certificat d'upload déjà intégrés au workflow.
- **RESTANT À TESTER** : produit à 1,19 € actif, achat réel/test, annulation, restauration et remboursement ; ne jamais assimiler installation ou début d'essai à une vente réussie.
- NOTE CHK IMPORTANTE épinglée dans `Sécurité & sauvegardes > les applications play store > bloc note > sauvegarde de la ou on` : `00 — REPRISE OBLIGATOIRE MES NOTES : AAB SIGNÉ, JKS, GOOGLE PLAY, MISES À JOUR` (ID `5b202474-7d30-415b-8739-e4761e4493c8`). À lire au démarrage de toute nouvelle intervention, afin d'éviter de demander à l'éditeur un AAB/JKS déjà fournis.
