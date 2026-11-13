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
