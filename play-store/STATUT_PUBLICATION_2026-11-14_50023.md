# Mes Notes & Dossiers — suivi de publication, 14 novembre 2026

## Produit
- Éditeur : Sync 30 Numérique
- Dépôt : https://github.com/Chasmet/Bloc-note
- Branche : feature/play-sync30-mesnotesdossiers-20261112
- Package public immuable : com.sync30.mesnotesdossiers
- Modèle : application gratuite, essai premium 24 h, achat unique à vie 1,19 €, aucune reconduction
- Product ID Google Play Billing : blocnote_premium_lifetime
- SDK : min 24, target/compile 36 ; Billing 9.1.0
- Publication demandée : production directe, sans piste interne

## Vérifications réelles
- Au contrôle de la page "Publishing overview", Google Play Console affichait « Changes in review » pour la production 50022 (2.6.22), avec 14 pays/régions.
- Le 14 novembre 2026, sur la page réelle « Create production release / Prepare release », la version 50023 (2.6.23), API 24+, target SDK 36, est visible comme App bundle « Enhanced ».
- Popover « Google Play enhanced your app bundle » : « App bundle is signed by Google Play », « Automatic protection is on », « Your app text is not automatically translated » (absence de traduction automatique, sans indication d'erreur bloquante).
- La page affiche 50023 sous « Previous release > Not Included », avec action « Include ». Cet emplacement se rapporte à l'inclusion des versions d'une release précédente ; il ne prouve pas à lui seul la prise en compte de 50023 dans la release en cours. Vérifier le détail avant validation finale.
- « Release name * » était vide et le bouton « Next » désactivé. Saisie confirmée dans ce champ : « 50023 (2.6.23) » ; « Next » est devenu actif.
- « Release notes » fr-FR saisies et reconnues par Google Play : « Amélioration de la lisibilité de l'interface et des boutons d'accès premium. Optimisations de la stabilité et de l'expérience utilisateur. »
- DERNIER ÉTAT VÉRIFIÉ : bouton « Next » actif ; aucune validation de l'étape 2, aucune soumission ni publication de la release 50023 constatée. Les champs ont été saisis mais leur enregistrement durable n'est pas confirmé.

## AAB et sauvegarde
- Archive privée sur Google Drive « MesNotesDossiers_50023_signe.zip » : https://drive.google.com/file/d/14CBL86uvsy8ekYFJWDdaH-qc6w3MBMDj/view
- AAB commercial extrait, 2 498 125 octets, SHA-256 2c1d9b511329f162da696ac31aab851977196c46e3d60bc08edcd25cd39895c9
- « jar verified » ; signature upload SHA-256 95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB
- Compilation et validation de manifeste sont réalisées par le workflow « .github/workflows/play-signed-aab.yml », qui valide notamment package et signature ; aucun nouveau build n'a été déclenché pendant ce contrôle.
- Les secrets GitHub attendus (jamais publier leurs valeurs) : BLOCNOTE_UPLOAD_JKS_BASE64, BLOCNOTE_UPLOAD_STORE_PASSWORD, BLOCNOTE_UPLOAD_ALIAS, BLOCNOTE_UPLOAD_KEY_PASSWORD

## Prochaines étapes réelles
1. Sur l'écran actuel, appuyer sur « Next » et vérifier la page « Preview and confirm ». Si validation indique aucun bundle ajouté à la release, utiliser « Include » ou « Add from library » pour inclure précisément 50023.
2. Contrôler l'absence d'erreurs Google Play bloquantes et la cohérence de la release.
3. Tester sur appareil le paywall (problème utilisateur de barres grises illisibles), l'achat Billing à 1,19 €, la restauration, l'export des notes et la conservation des données.
4. Envoyer en examen / lancer production quand les contrôles et la Console le permettent, puis revérifier l'état (en examen ≠ publié).
5. Actualiser ce fichier après vérification.
