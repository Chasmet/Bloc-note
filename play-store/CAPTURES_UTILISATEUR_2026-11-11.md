# Captures originales fournies dans CHK Browser Local 3 — 11 novembre 2026

**Statut :** cinq photos JPG originales vérifiées dans le dossier du téléphone `Categories/Images/application bloc note/`. Les miniatures ont été inspectées depuis le connecteur CHK ; les fichiers JPEG complets ne sont **pas encore téléchargés dans GitHub ni envoyés à Google Play Console**. Ne pas assimiler cette liste à un téléversement réussi.

| Photo (préfixe date/heure) | Résolution | Taille | Aperçu vérifié | Conseils |
|---|---:|---:|---|---|
| `Screenshot_20261111_170525_com_chk_blocnote_MainActivity.jpg` | 1188 × 2392 | 343 443 octets | Accueil, dossiers colorés « foot en salle » / « football » | Image utilisable pour illustrer les dossiers, après contrôle de la résolution |
| `Screenshot_20261111_170538_com_chk_blocnote_MainActivity.jpg` | 1200 × 2458 | 324 849 octets | Sous-dossier « foot en salle », vide | Ne pas mettre en tête de fiche : pas de note |
| `Screenshot_20261111_170608_com_chk_blocnote_MainActivity.jpg` | 1200 × 2664 | 481 844 octets | Accueil avec menu/voile | Préférer la suivante, affichage du menu plus lisible |
| `Screenshot_20261111_170622_com_chk_blocnote_MainActivity.jpg` | 1200 × 2465 | 452 805 octets | Menu déroulant, mode clair, export/import JSON, PIN | Image secondaire qui démontre les fonctionnalités ; vérifier le libellé « mises à jour (test) » |
| `Screenshot_20261111_170639_com_chk_blocnote_MainActivity.jpg` | 1200 × 2443 | 305 323 octets | Favoris vides | Ne pas utiliser en tête de fiche ; favoriser un écran avec une note favorite |

Chemin exact du dossier Android : `Categories/Images/application bloc note`. Transfert direct vers la fiche Play Store possible par le navigateur CHK avec `browser_upload_file` **une fois le champ de dépôt observé dans la console**, sans utiliser les miniatures comme fichiers originaux.

## Sélection pour la fiche Google Play
Utiliser prioritairement les cinq **captures réelles depuis un émulateur Android avec des notes fictives**, déjà enregistrées dans `play-store/screenshots/` au format 1080 × 1920 px. Les photos authentiques fournies par le propriétaire peuvent compléter l'accueil et les réglages. Ne pas promouvoir d'écran vide, une interface de test, ni des données personnelles.

## Statut Play Console
La création de l'app `com.chk.blocnote` n'était pas encore confirmée au dernier contrôle ; n'affirmer aucun upload en console sans résultat explicite. Le dossier ZIP `play-store/kit-google-play.zip` contient les captures de l'émulateur et les autres visuels préparés précédemment.

Source de vérification : interface du dossier CHK Agent Browser via `files_catalog` et aperçus de ces cinq fichiers.

## Contrôle technique réel du JPEG (11/11/2026)
Les résolutions ci-dessus ont été extraites de l'en-tête SOF des **fichiers binaires originaux** via `files_read_bytes` de CHK Browser Local 3 ; ce ne sont pas les tailles réduites des aperçus.
**Les cinq rapports hauteur/largeur dépassent 2:1** : ils ne respectent pas les dimensions acceptées par Google Play pour les captures de téléphone. Ne PAS envoyer les fichiers bruts à Play Console ; recadrer/adapter en **1080 × 1920** sans modifier ni inventer l'interface, supprimer les éléments privés de barre d'état, puis contrôler avant dépôt. Les captures d'émulateur déjà présentes dans `play-store/screenshots` sont déjà en 1080 × 1920.
Source : https://support.google.com/googleplay/android-developer/answer/9866151
