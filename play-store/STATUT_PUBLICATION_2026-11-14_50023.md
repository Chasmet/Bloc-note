# Mes Notes & Dossiers — reprise de publication du 14 novembre 2026

## Identité et branche
- Éditeur : Sync 30 Numérique
- Dépôt : https://github.com/Chasmet/Bloc-note
- Branche : feature/play-sync30-mesnotesdossiers-20261112
- Package public immuable : com.sync30.mesnotesdossiers
- Distribution souhaitée : directement Production, sans piste interne
- Modèle : téléchargement gratuit, essai 24 h, achat unique 1,19 € (blocnote_premium_lifetime), aucun abonnement
- SDK : min 24, target/compile 36 ; Billing 9.1.0

## Contrôles réels (14 novembre 2026)
- Play Console > Publication : « Changes in review », version 50022 (2.6.22), modification « Start full rollout » et 14 pays/régions en examen.
- Play Console > Production : « Release 50022 (2.6.22) in review » ; 0 installation indiquée au moment du contrôle.
- Version 50023 (2.6.23) : AAB commercial signé présent dans l'archive Drive « MesNotesDossiers_50023_signe.zip » ; https://drive.google.com/file/d/14CBL86uvsy8ekYFJWDdaH-qc6w3MBMDj/view
- AAB extrait : app-release.aab, 2 498 125 octets ; SHA-256 = 2c1d9b511329f162da696ac31aab851977196c46e3d60bc08edcd25cd39895c9.
- Signature AAB : « jar verified » ; empreinte SHA-256 du certificat d'upload = 95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB.
- Le manifeste du bundle contient com.sync30.mesnotesdossiers, la chaîne 2.6.23 et la valeur varint 50023 (inspection binaire indicative ; validation Play Console encore requise).
- Le workflow de génération .github/workflows/play-signed-aab.yml valide package, versions, signature, SDK et permissions à la compilation ; aucune nouvelle compilation n'a été lancée dans cette intervention.
- Les clics « Create new release » du navigateur partagé n'ont pas abouti (« Élément introuvable ») ; l'onglet Play Console est préparé mais non affiché au premier plan dans CHK Browser.
- Aucun import du bundle 50023 dans Play Console n'a été confirmé, aucune nouvelle soumission effectuée, aucun statut de publication du 50023 n'est revendiqué.

## Prochaines étapes
1. Depuis Google Play Console > Production, attendre ou prendre en compte la décision sur le lot 50022 actuellement en examen, sans supprimer ces changements par inadvertance.
2. Créer une release Production 50023, importer le fichier AAB signé EXTRAlT (pas le ZIP Drive), renseigner les notes de version, et vérifier versionCode 50023 / certificat.
3. Contrôler l'affichage des boutons du paywall sur appareil (signalement : boutons grisés sans texte) et le parcours Billing achat/restauration avant validation finale.
4. Soumettre les changements si aucun blocage, puis revérifier le statut réel (en examen vs publié) et préserver les données utilisateur.
5. Mettre à jour ce suivi après résultat Console.

## Signature et secret
- Clé JKS conservée dans un stockage privé sécurisé ; le dépôt ne conserve que des références aux secrets GitHub (BLOCNOTE_UPLOAD_JKS_BASE64, BLOCNOTE_UPLOAD_STORE_PASSWORD, BLOCNOTE_UPLOAD_ALIAS, BLOCNOTE_UPLOAD_KEY_PASSWORD).
- Aucune clé, aucun mot de passe n'est inscrit dans ce fichier.
