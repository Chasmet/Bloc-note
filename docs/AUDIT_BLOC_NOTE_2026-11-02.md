# Audit complet — Sync 30 Bloc-note

**Date :** 2 novembre 2026
**Version analysée :** APK test v2.2.27
**Dépôt :** Chasmet/Bloc-note
**Source initiale :** commit `91a1722961ca2beb916eafa89e7006609da123ce`

## Vérification sur Android réel simulé

- [Exécution de référence, défaut reproduit](https://github.com/Chasmet/Bloc-note/actions/runs/41440185873) : 14 tests sur Android 10, 13 réussis, 1 échec sur la recherche dans les favoris.
- [Exécution après correctif](https://github.com/Chasmet/Bloc-note/actions/runs/41441468646) : **14/14 réussis sur Android 10 (API 29) et 14/14 réussis sur Android 16 (API 36)**.
- APK débogage : compilation, signature et présence de l'APK vérifiées dans [GitHub Actions](https://github.com/Chasmet/Bloc-note/actions/runs/41426724990).
- Les quatre tests d'interface Android 16 avaient initialement échoué avec RootViewWithoutFocusException. La répétition avec réveil et déverrouillage explicites de l'émulateur a réussi à 100 %. Ce résultat ne prouve pas un bug propre à l'application.

### Scénarios instrumentés

1. Création d'un dossier, d'une note, modification puis relecture SQLite.
2. Favoris, épinglage, archivage, mise à la corbeille, restauration.
3. Refus de supprimer un dossier non vide ou contenant un sous-dossier.
4. Export JSON, modification des données et import rétablissant les notes.
5. Refus d'une sauvegarde JSON invalide sans suppression de notes existantes.
6. Vérification qu'une chaîne ressemblant à une injection SQL est traitée comme une recherche.
7. Vidage sélectif de la corbeille.
8. Recherche respectant le filtre notes/favoris/archives/corbeille.
9. Conservation d'une note de 100 000 caractères dans SQLite.
10. Ouverture de l'interface avec navigation accessible.
11. Navigation Dossiers, Favoris et Accueil.
12. Création via éditeur, saisie et conservation du texte au retour.
13. Démarrage de l'interface en mode clair.
14. Détection des versions de mise à jour plus récentes.

## Défaut confirmé et corrigé

**Recherche dans les collections** — La requête testée ignorait le filtre en présence d'un mot-clé; une note ordinaire pouvait apparaître dans les favoris et la corbeille afficher le mauvais résultat. Correction de `NoteDatabase.listItems` : collection appliquée avant filtre textuel. Confirmé sur Android API 29 et API 36.

## Problèmes observés par inspection statique (non reproduits par crash)

- **Protection des notes :** SQLite en clair; code PIN contrôlant seulement l'interface avec hachage SHA-256 non salé. Ne pas présenter ceci comme du chiffrement des notes.
- **Sauvegarde Android :** `android:allowBackup="true"`; une sauvegarde du système peut transférer les données hors de l'appareil si activée. Adapter les règles, la notice de confidentialité et les contrôles de sauvegarde. Le service Android chiffre les sauvegardes selon la version de l'OS.
- **Autosauvegarde :** un échec d'écriture laisse `dirty=true`, mais l'éditeur peut tout de même se fermer; risque de perdre les changements non enregistrés.
- **Performance :** lectures SQLite, statistiques et recherche `LIKE` sur le fil principal; risque de ralentissement lorsque les notes s'accumulent.
- **Limite d'affichage :** requête principale plafonnée à 1 000 notes sans pagination.
- **Protection de session :** `unlockedSession` reste vrai tant que l'activité vit; prévoir un verrouillage sur retour d'arrière-plan après une durée définie.
- **Brouillons volumineux :** le texte intégral est sauvegardé dans `onSaveInstanceState`; risque Android de `TransactionTooLargeException` sur des notes très longues.

## Préparation Google Play et monétisation (non testées et non prêtes)

- `targetSdk=34` alors que Google Play exige l'API 36 pour une nouvelle publication/mise à jour mobile depuis le 31 août 2026 : https://support.google.com/googleplay/android-developer/answer/11926878
- `minSdk=21`; la protection automatique Google Play nécessite au minimum API 24 si activée : https://support.google.com/googleplay/android-developer/answer/10183279
- APK de test signée en débogage et publiée dans GitHub Releases; prévoir une AAB de production, Play App Signing et une signature adaptée.
- Paiement unique **1,19 €** après **trois jours d'essai** : pas encore implémenté; aucun achat Google Play Billing/restauration/validation du droit d'accès n'est couvert par ces tests.
- Tester la version commerciale via le suivi Play Console (tests internes), puis Play Billing et restauration sur les appareils cibles.

## Limites du rapport

Les tests se sont déroulés sur deux **émulateurs Android**, pas sur le téléphone personnel de l'utilisateur. Aucune batterie terrain exhaustive, mesure de mémoire ou d'autonomie, vérification de lecteur d'écran, test microphone, test réseau hors ligne prolongé, ni validation Google Play Console n'a été exécutée. Les tests réussis prouvent le fonctionnement des scénarios couverts, pas l'absence de tout bug.

## Ordre de traitement recommandé

**P0 :** préserver données, sécuriser autosauvegarde et politique de confidentialité/PIN.

**P1 :** adapter `targetSdk` et `minSdk`, mettre en place signature de production/AAB, tests de mise à jour et Google Play Billing avant monétisation.

**P2 :** pagination/recherche performantes, grands documents, accessibilité et ergonomie.
