# REPRISE DE PROJET — Sync 30 Bloc-note

**Point de situation vérifié le 11 novembre 2026.**

Ce fichier permet à une autre conversation ou à un agent de reprendre le projet sans supposer connaître les échanges précédents. Vérifier les nouveaux commits et releases avant toute intervention.

## Identité
- Marque/éditeur : Sync 30 Numérique
- Application Android : Sync 30 Bloc-note (initialement Bloc Note Ultra)
- Dépôt officiel : https://github.com/Chasmet/Bloc-note
- Branche de développement stable : `main`
- Package Android : `com.chk.blocnote`
- Technologie : Android natif Java, SQLite local, interface XML
- Dernier commit observé : `ac9fe7ccb6f3babd080f2428c0498dc053792c54`
- APK de test : [v2.2.28](https://github.com/Chasmet/Bloc-note/releases/tag/v2.2.28), fichier `Bloc-Note-Ultra-test-2.2.28.apk`

## Exigences immuables
1. **Ne pas faire perdre les notes existantes.** Ne pas changer l'ID Android, la signature de test ou le schéma SQLite sans migration vérifiée. Recommander l'export JSON avant toute opération sensible.
2. **Identité visuelle validée :** noir/marron/cuivre, touches orange/ambre, style proche de Rush Studio, logo carnet spiralé et stylo.
3. **Stabilité, fluidité et ergonomie** priment sur les fonctions secondaires.
4. **Monétisation demandée, non implémentée :** trois jours d'accès gratuit puis **paiement unique 1,19 €**, **aucun abonnement**. Play Billing doit valider et restaurer les droits, après tests internes Play.

## Fonctionnalités existantes
Notes et sous-dossiers, recherche, favoris, épinglage, archives, corbeille, tags, dictée, couleurs et emojis, thème sombre/clair, liste/grille, taille du texte, code PIN d'interface, autosauvegarde, import/export JSON, notifications de nouvelles APK GitHub en phase de test.

## Dernière vérification de tests
[Audit complet du 2 novembre 2026](AUDIT_BLOC_NOTE_2026-11-02.md) :
- **28 passages réussis / 28** : 14 scénarios instrumentés sur Android API 29 + les mêmes 14 sur API 36, tous sur **émulateurs**. Les tests ne constituent pas une validation exhaustive sur téléphone.
- Création, modification et conservation SQLite, dossiers, favoris, archives, corbeille, sauvegarde/import JSON, recherche filtrée, texte ressemblant à une injection SQL, note de 100 000 caractères, UI/nav, thème clair et comparaison de versions.
- Défaut confirmé puis corrigé : avec un texte recherché, certaines collections (favoris, archives, corbeille) perdaient leur filtrage; correctif dans `NoteDatabase.listItems`.

## Points ouverts classés
**P0 — protéger les données :** si SQLite refuse une écriture, l'éditeur peut se fermer alors que les derniers changements ne sont pas enregistrés. Sauvegarde système autorisée (`allowBackup=true`) à documenter/maîtriser. Le PIN n'est pas du chiffrement des notes; hachage SHA-256 non salé et session restant déverrouillée.
**P1 — publication Android :** `targetSdk 34` insuffisant pour nouvelle publication Google Play en novembre 2026; préparer la mise à niveau, l'AAB, Play App Signing, signature production et tests de mise à jour. Le système 3 jours/1,19 € et son achat définitif ne sont pas intégrés.
**P2 — volume/UX :** SQLite et recherche `LIKE` sur thread UI, limite 1 000 notes sans pagination, risque de grand brouillon dans `onSaveInstanceState`. À mesurer/tester : téléphone réel, accessibilité, réseau absent, microphone, performance, stabilité en arrière-plan.

## Reprise recommandée pour chaque nouveau chat
1. Lire ce fichier et le rapport d'audit, inspecter `main`, GitHub Releases/Actions et vérifier les nouveaux commits.
2. Décrire séparément « fait », « testé », « non fait » ; ne jamais déclarer prête la monétisation avant intégration Play Billing.
3. Travailler d'abord sur sauvegarde fiable, sécurité PIN/backup, stabilité. Utiliser une branche pour les corrections sensibles et des tests sur Android 10 et 16.
4. Pour toute APK : compilation GitHub Actions réelle, preuve de signature et release vérifiée; installation sans perte de données.
5. Mettre à jour ce document lors d'une version importante.

## Lien avec les notes privées de CHK Agent Browser
Une note épinglée « SUIVI PROJET — Sync 30 Bloc-note — audit, APK et monétisation » contient la version privée de reprise. Elle a été créée le 11/11/2026 dans Notes de CHK Agent Browser. Son déplacement dans le sous-dossier montré par l'utilisateur reste à vérifier.

**À ne pas confondre :** cette fiche et les tests ne signifient pas que l'application est déjà publiée/commercialisée sur Google Play.
