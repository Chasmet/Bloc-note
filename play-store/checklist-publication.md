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
- [ ] Vérifier que https://sync30.onrender.com/politique-confidentialite-blocnote.html répond bien avec la dernière version
- [ ] Fiche « Sécurité des données » vérifiée selon code final (Billing, système, éventuel backend)
- [ ] Type « application », catégorie Productivité, public cible et questionnaire de classification
- [ ] Déclaration publicité conforme (pas de SDK pub dans code vérifié)
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
