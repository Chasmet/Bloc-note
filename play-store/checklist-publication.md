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
- [ ] URL de confidentialité enregistrée dans Play Console, MAIS le site Render affiche encore la version HTML du 11/11 au lieu du texte GitHub actualisé décrivant CHK Paiements. Déployer la version à jour et revérifier le contenu HTTPS public.
- [ ] Fiche « Sécurité des données » vérifiée selon code final (Billing, système, éventuel backend)
- [ ] Type « application » confirmé à la création ; catégorie Productivité, public cible et questionnaire de classification à finaliser
- [x] Déclaration Publicités : « Non, l'application ne contient pas de publicités » enregistrée dans Play Console (12/11/2026, code sans SDK publicitaire vérifié)
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
- [x] Politique de confidentialité enregistrée dans le formulaire Play Console : `https://sync30.onrender.com/politique-confidentialite-blocnote.html` (contenu public à actualiser par déploiement Render).
- [x] Déclaration gouvernement : « Non » enregistrée, l'application est proposée par une entreprise privée.
- [ ] Déclaration accès examinateurs : **BLOQUANT**. Google indique explicitement ne pas pouvoir utiliser l'essai gratuit ni acheter pour examiner les sections payantes. La déclaration doit indiquer « Oui, accès restreint » et fournir un vrai mécanisme d'accès de démonstration sécurisé (à développer/tester) avant envoi.
- [ ] Déclaration de fonctionnalités financières/health, classification d'âge, sécurité des données et publicité ID Android à compléter après examen du code final. Un achat intégré ordinaire ne transforme pas l'app de notes en service bancaire.
- [ ] Compte marchand / statut de professionnel « Declare your trader status » : validation nécessaire du titulaire en Play Console.
- [ ] Pack d'images (logo 512, bannière 1024x500 et 5 captures 1080x1920) est en GitHub, pas encore chargé dans Play Console.
- [ ] AAB public release signé, compte de service CHK Paiements, passerelle HTTPS, test achat/restauration/remboursement non validés ; AUCUNE vente ni publication.
- [ ] Vérifier le package public fixé `com.sync30.mesnotesdossiers` dans Play Console et dans l'AAB accepté.
