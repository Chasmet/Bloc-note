# Guide réutilisable Google Play — achats intégrés (Sync 30 Numérique)

SYNC 30 NUMÉRIQUE — MÉTHODE RÉUTILISABLE POUR ACTIVER UN ACHAT INTÉGRÉ GOOGLE PLAY
Version documentée : 14/11/2026 — issue du dépannage réussi de « Mes Notes & Dossiers » (2.6.25 / 50025).

OBJECTIF
Application gratuite à télécharger, achat unique premium à vie (non consommable), sans abonnement. Exemple validé : produit « Accès premium à vie » à 1,19 € en France, 14 pays sélectionnés. Cette méthode s'applique à une FUTURE application avec SON propre package et SON propre identifiant de produit.

PROCÉDURE DANS GOOGLE PLAY CONSOLE
1. Ouvrir l'application concernée > Monetize with Play > Products > One-time products > Create one-time product.
2. Remplir TOUS les champs obligatoires de Product details, avant d'aller aux tarifs :
   - Product ID : EXACTEMENT l'identifiant utilisé dans le code Android Billing (exemple Mes Notes : blocnote_premium_lifetime). Ne pas confondre cet ID avec l'ID d'option d'achat.
   - Name : « Accès premium à vie ».
   - Description française claire de plus de 50 caractères, par exemple « Débloquez toutes les fonctionnalités premium de l'application définitivement. Paiement unique à vie, sans abonnement ni renouvellement automatique. »
   - Tax category cohérente (Digital app sales pour une fonctionnalité numérique). Vérifier autres déclarations requises.
3. Cliquer Next > Purchase option : choisir « Buy » (achat unique, pas location). Définir un Purchase option ID distinct, par exemple « acces-a-vie ».
4. Availability and pricing :
   - Sous « Edit availability and access », utiliser « Set to available » POUR LES PAYS CIBLÉS si nécessaire.
   - Si Google affiche « Available » pour tous les pays alors que des dizaines sont « Set a price », éviter de laisser des régions non souhaitées disponibles sans prix : sélectionner uniquement les marchés visés et « Set to unavailable » pour les autres (vérifier soigneusement la sélection).
   - « Set prices » : cocher les pays/régions ciblés > « Continue » ; définir le montant de base en EUR, exemple France 1,19 € ; laisser Google calculer les prix convertis > contrôler TOUS les montants > Apply/Continue.
   - Dans l'exemple Mes Notes, la distribution de l'app visait 14 pays. Les prix des autres devises peuvent varier : ne pas recopier sans contrôle les anciens taux.
5. « Save as draft » si souhaité, puis « Activate ». IMPORTANT : la création seule ne suffit pas. Sur la page de détail du produit, l'option Buy doit afficher « Active », et NON « Draft ». Vérifier le nombre de pays.
6. Après activation, ouvrir l'app installée depuis Google Play, laisser Billing interroger les produits et appuyer sur « Acheter ». Vérifier que la fenêtre officielle Google Play s'ouvre avec le BON nom et le BON prix ; ceci est un test d'affichage, PAS un achat complet.
7. Tester via comptes testeurs de licence Google Play : annulation, achat, acquittement, accès premium après relance, restauration et conservation/export des données. Ne pas désinstaller sans sauvegarde contrôlée et ne pas déclencher involontairement un achat facturé.

PIÈGES IDENTIFIÉS DANS L'EXPÉRIENCE DU 14/11/2026
- « No results » dans One-time products = aucun produit enregistré : une application publiée/AAB signé n'active pas automatiquement ses achats.
- « Your changes couldn't be saved » : revenir sur Product details et contrôler Product ID, Name, Description ; ils étaient redevenus VIDES dans le formulaire lors du dépannage, empêchant l'enregistrement. Ne pas conclure immédiatement que le problème vient des prix.
- « Set a price » sur une région AVAILABLE : région à tarifer ou à rendre indisponible.
- « Draft » sur l'option : non achetable par les utilisateurs. Utiliser « Activate » et vérifier l'état « Active » par relecture.
- Produit ID ≠ Purchase option ID : le code cherche le Product ID.
- La fenêtre de paiement dépend de Google Play Billing ; ce n'est pas une ouverture directe de Google Pay.

FAITS CONFIRMÉS LE 14/11/2026
- Mes Notes & Dossiers, package com.sync30.mesnotesdossiers ; produit blocnote_premium_lifetime ; option acces-a-vie, type Buy / Backwards compatible, 14 pays ; statut « Active » lu dans Play Console.
- Une capture réelle montrait ensuite l'écran Google Play « Accès premium à vie » à 1,19 € et le bouton « Achat immédiat » : chargement du produit ET ouverture du paiement TESTÉS avec succès.
- Achat abouti, restauration de licence et persistance après paiement NON TESTÉS dans cette session.

RÈGLE DE REPRISE POUR LES PROCHAINES APPLIS
Contrôler successivement : package, Product ID dans le code, détails obligatoires, « Set to available », « Set prices », regions, option Buy « Active », test de la fenêtre, test achat/restauration. Ne jamais marquer FAIT sans confirmation du statut réel. Ne pas publier de clés/mots de passe.