# Audit de monétisation — Mes Notes & Dossiers
**Date : 11 novembre 2026 — statut : préparation / aucun achat réel Bloc-note testé**

## Offre imposée
- Installation gratuite sur Google Play, essai complet **24 heures**, puis **achat définitif unique 1,19 €** pour continuer à créer/modifier, **sans abonnement ni débit automatique**.
- Produit Google Play **INAPP non consommable** : `blocnote_premium_lifetime`. Prix réel affiché depuis le catalogue Play, à paramétrer côté Play Console.
- Toutes les notes et tous les dossiers restent stockés ; **export JSON libre même après expiration**, jamais de suppression automatique.

## Comparaison documentée avec Rush Studio
- Rush Studio utilise `rush_premium` (0,49 €) avec BillingClient 9.1 sur la branche `fix/rush-premium-checkout-2371`. La restauration passe par `queryPurchasesAsync`, `PENDING` ne donne pas d'accès et la confirmation `acknowledgePurchase` est gérée.
- Son code prévoit un **mode client-only** lorsque `RUSH_PREMIUM_VERIFY_ENDPOINT` n'est pas défini. Ce mode est moins résistant à la fraude ; il ne suffit pas de le copier pour sécuriser une nouvelle application.
- La branche Rush Studio comporte `backend/rush-premium/server.mjs`, **prototype de vérification serveur** avec Google Play API et PostgreSQL, sans preuve ici qu'il soit déployé.
- Selon le propriétaire, Rush Studio a déjà traité un achat Premium réel ; cela ne prouve pas à lui seul la configuration du produit Bloc-note ni l'activation de CHK Paiements.

## Infrastructure CHK Paiements inspectée
- Dépôt `Chasmet/Chk-paiment-` : l'application Android/Windows est conçue pour le suivi et les validations, avec sidecar indépendant. Documentation : paiements réels non activés et MCP dédié non connecté dans la version vérifiée.
- Code du sidecar dans `Chasmet/CHK-serveur-Universal-Control`, **branche isolée `feature/paiements-sync30`** ; il écoute 127.0.0.1:8791. Les routes Tailscale privées 11001–11004 ne sont **pas publiques pour les utilisateurs ordinaires de Google Play**.
- **Ajout effectué sur cette branche seulement** : `payments/google-play-verifier.mjs`, routes `GET /api/v1/play/health` et `POST /api/v1/play/verify`, suivi idempotent des paiements certifiés dans le hub CHK.
- La vérification exige `CHK_GOOGLE_PLAY_BILLING_ENABLED=true` et `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` **côté serveur**, avec droits Play Console pour `com.chk.blocnote`. Rien de secret dans le dépôt ni l'APK.
- Les routes publiques doivent passer par une **terminaison HTTPS dédiée sécurisée**, sans toucher au MCP universel ni exposer directement le port 8791.
- Les tests automatisés Node et CI CHK ont été exécutés et réussis, **mais sans requête de paiement réel auprès de Google**.

## Changements côté application Android (branche de préparation uniquement)
- `PlayBillingController.java` : plus aucun déblocage Premium sur la seule réponse client `PURCHASED`. La réponse CHK doit être `verified=true` avec le package `com.chk.blocnote` et le SKU exact. Reconnaissance et confirmation Play uniquement après validation CHK.
- `PENDING`, annulation ou produit incorrect : aucun droit attribué. Achat indisponible si vérificateur HTTPS absent. Restauration des achats à partir de Google Play suivie d'une nouvelle vérification CHK ; un remboursement peut entraîner la révocation au prochain contrôle.
- `BuildConfig.CHK_PAYMENTS_VERIFY_ENDPOINT` injecté à la compilation ; configuration via `-PchkPaymentsVerifyEndpoint=https://<hote-public>/api/v1/play/verify`.
- Workflow `.github/workflows/play-signed-aab.yml` : impose `BLOCNOTE_CHK_PAYMENTS_VERIFY_URL` avant production d'un AAB signé et exige les secrets d'upload propres à Bloc-note.
- Correction compilation XML : `Mes Notes &amp; Dossiers` dans les attributs XML. Le nom public reste **Mes Notes & Dossiers**, développeur **Sync 30 Numérique**.
- **La branche principale ne contient pas ces changements de paiement** et la version Play n'est pas encore publiée.

## Défaillances et limites encore ouvertes
**BLOQUANTS POUR VENTE**
1. La fiche `com.chk.blocnote` doit être créée/validée dans Play Console, puis le produit `blocnote_premium_lifetime` activé à 1,19 € (France). Vérifier la visibilité du produit sur une piste interne.
2. L'instance CHK Paiements **actuellement déployée** n'est pas prouvée accessible publiquement via HTTPS dédié. Le sidecar nouveau n'a pas été déployé en production sur le PC.
3. Un compte de service autorisé dans **Google Play Developer API** doit être configuré et une transaction de test doit être vérifiée, pas seulement un test OAuth/health.
4. Tester en piste interne une commande réelle de test : achat, annulation, PENDING, succès, confirmation, restauration sur deuxième appareil, absence réseau et remboursement.
5. Clé d'upload **propre à Bloc-note**, signature AAB vérifiée dans Google Play Console. Ne jamais réutiliser les clés Rush Studio.
6. Mettre en place un contrôle de taux côté passerelle HTTPS et un audit des accès.

**RISQUES À AMÉLIORER**
- Le compteur d'essai de 24 heures reste dans les préférences Android : effacer les données ou réinstaller peut le remettre à zéro. Pour une limite par utilisateur, établir une identité stable avec consentement et une date conservée côté serveur.
- Le contrôle des remboursements est réalisé lors des requêtes de l'app. Les notifications Play RTDN et la Voided Purchases API ne sont pas encore raccordées ; un achat remboursé peut rester reconnu jusqu'au prochain contrôle en ligne.
- Les droits validés sont mis en cache local pour permettre l'utilisation hors connexion ; ce cache n'est pas une protection contre une APK modifiée.
- Le vérificateur CHK renvoie `billingReady` après authentification OAuth, ce qui ne garantit pas à lui seul que le produit est publié ni que le compte de service a tous les droits.
- Prix et revenus dépendent de Google Play et du pays ; le journal CHK stocke `unknown` pour les sommes qui ne sont pas confirmées, plutôt que d'inventer un montant de vente.

## Conditions à vérifier avant fusion sur main / publication
- [x] Audit de Rush et CHK Paiements
- [x] Code Android imposant vérification CHK et prix catalogue Play
- [x] Code vérificateur Google Play côté sidecar CHK Paiements **en branche isolée**
- [x] Tests Node côté CHK (simulations, sans vrais achats)
- [ ] Compilation Android API 36 et tests instrumentés sur la dernière version **à confirmer après les correctifs**
- [ ] CHK Paiements déployé avec compte Google Play autorisé, HTTPS public dédié
- [ ] Fiche Google Play + produit activé à 1,19 €
- [ ] AAB signé par la clé officielle de Bloc-note
- [ ] Tests de licence Google Play en piste interne
- [ ] Tests de révocation, fraude, robustesse réseau et confidentialité
- [ ] Publication initiale avec paiement actif

## Liens
- Android : https://github.com/Chasmet/Bloc-note/tree/feature/play-24h-lifetime-119-20261111
- CHK Paiements : https://github.com/Chasmet/CHK-serveur-Universal-Control/tree/feature/paiements-sync30/payments
- Google : https://developer.android.com/google/play/billing/lifecycle/one-time
