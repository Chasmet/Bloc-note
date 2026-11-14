# Sync 30 Numérique — Mes Notes & Dossiers — Production 50024
Date : 14 novembre 2026 (Europe/Paris)

## Situation actuelle (confirmée dans Google Play Console)
- **FAIT — release Production 50024 (2.6.24) enregistrée et soumise à Google**.
- Console : https://play.google.com/console/u/0/developers/6943961586606925576/app/4976373584470422373/publishing
- Dernière observation réelle : **« Changes in review »**, section « Production », version **50024 (2.6.24)**, « Start full rollout ».
- Vérifications rapides Google en cours : la console indiquait « Up to 10 minutes remaining » et « Changes will be sent for review as soon as checks complete successfully ».
- **NE PAS DIRE « PUBLIÉ »** : publication publique de 50024 non confirmée. « Last published on November 14, 2026 » renvoie à une publication précédente, non à une confirmation de 50024.
- Diffusion sélectionnée : déploiement 100 % des utilisateurs ciblés, 14 pays/régions préexistants.
- Écran de revue : **aucune erreur bloquante**, un avertissement non bloquant au sujet du fichier de désobfuscation absent ; application produite avec minifyEnabled=false.
- Avant l'envoi, la page disait « If you save, changes will be saved in Publishing overview ». Après enregistrement le message confirmait « Your change has been saved ». Le dialogue « Send 1 change for review? » a été validé et la page affiche maintenant « Changes in review ».

## Identité / construction
- Éditeur : Sync 30 Numérique
- Application : Mes Notes & Dossiers
- Package public : com.sync30.mesnotesdossiers (à conserver)
- Dépôt : https://github.com/Chasmet/Bloc-note
- Branche : feature/play-sync30-mesnotesdossiers-20261112
- Build GitHub Actions signé : https://github.com/Chasmet/Bloc-note/actions/runs/43623845608 — **success** ; artefact mesnotesdossiers-production-v50024.
- Commit déclencheur : faa92b8da1a34d9f3c57175e3ee848dfb91b5954
- AAB versionName 2.6.24, versionCode 50024, targetSdk 36, minSdk 24.
- Certificat d'upload identique à la précédente version ; le processus de build signé a réussi.
- Le versionCode 50023 avait déjà été utilisé par Play Console : nouvel AAB 50024 requis.

## Fichier confirmé sur Android
- Chemin CHK Browser : Categories/Images/application bloc note/documents signés et autre/Sync30_MesNotesDossiers_2.6.24_50024_DIRECT.aab
- Taille : 2 498 123 octets
- SHA-256 vérifié sur le téléphone : f6085395d06d1c86001696b9de14bec78978e47e06cf13f09c60082382b636f2
- SHA-256 identique à la copie locale issue du workflow GitHub Actions.
- Sauvegarde Drive directe : https://drive.google.com/file/d/1ZzDL9lNsP85r5oRBVpE0MuM2I5iYuyeT/view
- Les clés JKS et mots de passe ne figurent pas dans ce fichier ; ne pas les placer dans GitHub. Référence aux GitHub Secrets existants.

## Modèle commercial validé par l'éditeur (à vérifier en exploitation)
- Installation gratuite, essai premium 24 h au premier lancement.
- Déblocage premium à vie : achat intégré unique 1,19 € via Google Play Billing, pas d'abonnement.
- Les notes existantes restent consultables/exportables à la fin de l'essai.
- Vérifications NON réalisées sur appareil dans cette intervention : boutons paywall (signalement de barres grises), parcours de paiement réel, restauration, réouverture/stockage des données.

## Prochaines actions
1. Attendre le résultat des vérifications automatiques et de la revue Google ; recontrôler la console avant d'annoncer « publié ».
2. En cas de rejet, relever le motif officiel et corriger sur une versionCode supérieure.
3. Tester le paywall/l'achat/restauration, conserver les données et corriger les défauts observés lors de la prochaine mise à jour.
4. Mettre à jour cette note avec la date de publication effective, les pays et les résultats de test, uniquement une fois vérifiés.
