# Audit automatisé - Sync 30 Bloc-note

Branche isolée d'audit de la version de test v2.2.27 (sans publication Play et sans modifier les données des utilisateurs).

## Périmètre
- APK compilée sur CI, tests instrumentés sur émulateur Android API 29
- SQLite: création, lecture, mise à jour, favoris, archive, corbeille, dossiers imbriqués, export/import JSON, injection SQL et notes longues
- UI: navigation, lancement, éditeur et sauvegarde, mode clair
- Versions: comparateur des nouvelles versions

## Limites
- Aucune interaction directe avec l'application du téléphone de l'utilisateur
- Aucun test de facturation Google Play (non implémentée)
- Le test de recherche dans chaque collection vérifie une exigence UX; il doit échouer tant que l'anomalie est présente.
- La compilation/test ne certifie pas à elle seule la sécurité, l'absence de crash sur d'autres appareils, ou la conformité Play.
