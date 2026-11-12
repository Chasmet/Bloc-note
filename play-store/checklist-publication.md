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
- [ ] Politique de confidentialité : URL saisie puis Save cliqué dans Play Console, mais après deux contrôles par rechargement le champ est VIDE. Enregistrement NON confirmé, à résoudre avant publication. URL envisagée : `https://sync30.onrender.com/politique-confidentialite-blocnote.html`.
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
- [ ] Description FR courte (68 caractères) et longue (1 704 caractères) saisies, mais le brouillon est revenu VIDE après rechargement. Aucun enregistrement distant confirmé. Les visuels obligatoires manquants provoquent « Some languages have errors ».
- [x] GitHub Actions candidat public du 12/11 : https://github.com/Chasmet/Bloc-note/actions/runs/43211756184 : SUCCESS. Cela ne prouve pas un AAB release signé.
- [ ] Google Play Dashboard indique **2/11 tâches complétées** (publicités Non, gouvernement Non). Politique de confidentialité saisie précédemment mais vide après rechargement.
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
