# Mes Notes & Dossiers — clé d'upload Google Play (métadonnées publiques)

Date : **13 novembre 2026**. Éditeur : **Sync 30 Numérique**.

## Identification

- Application Play : **Mes Notes & Dossiers** ; `applicationId = com.sync30.mesnotesdossiers`.
- Installation personnelle distincte à préserver : `com.chk.blocnote`.
- Branche publique : `feature/play-sync30-mesnotesdossiers-20261112`.
- Play Console app ID : `4976373584470422373`.

## Clé d'upload créée

- Fichier privé : `sync30_mesnotes_upload.jks` (ne jamais ajouter au dépôt).
- Alias : `sync30_mesnotes_upload`.
- Format : JKS ; RSA 4096 bits ; SHA256withRSA ; validité 20 000 jours.
- Certificat public : `sync30_mesnotes_upload_cert.pem`.
- Empreinte **SHA-256 certificat d'upload** :
  `95:4C:04:AC:6F:3A:66:66:E0:69:1B:17:95:CB:15:6C:D9:7C:9D:F9:2F:B0:0A:AD:29:AC:0B:AE:1F:02:2C:EB`.
- Empreinte **SHA-256 du fichier JKS** (sert à contrôler la copie, à ne pas confondre avec celle du certificat) :
  `0c224b9615e77a99f455072c1e98e2811c324c6cc1c853240859f0bd1e525c79`.

## Localisation et sécurité

- Copie vérifiée sur **CHK Android Local 3, stockage privé du téléphone** : `Sync30/Signatures/MesNotesDossiers/sync30_mesnotes_upload.jks` ; 4 012 octets ; empreinte SHA-256 confirmée par le transfert.
- Certificat public également copié dans ce même dossier.
- **Une deuxième sauvegarde chiffrée et indépendante doit encore être confirmée par le titulaire.** Un dossier Android n'est pas à lui seul une sauvegarde indépendante.
- Les mots de passe ont été générés sous forme d'un fichier confidentiel distinct remis au titulaire dans la conversation ChatGPT du 13/11/2026. **Ne jamais recopier les valeurs, la base64 du JKS ou la clé privée dans les Notes CHK ni dans GitHub public.** Les valeurs doivent rester dans un coffre sécurisé.

## Résultats des contrôles

- Génération de la clé JKS : **FAIT**.
- Export du certificat PEM : **FAIT**.
- Signature et vérification d'une archive JAR de test avec `jarsigner` : **TESTÉ / SUCCESS**.
- Copie du JKS sur CHK Android Local 3 : **FAIT / SHA-256 vérifié**.
- Enregistrement du certificat d'upload par Play App Signing : **PAS CONFIRMÉ**.
- GitHub Secrets configurés : **NON**.
- AAB RELEASE Android signé avec cette clé : **NON**.
- Publication Play : **NON**.

## Secrets GitHub attendus (noms uniquement)

```text
BLOCNOTE_UPLOAD_JKS_BASE64
BLOCNOTE_UPLOAD_STORE_PASSWORD
BLOCNOTE_UPLOAD_ALIAS
BLOCNOTE_UPLOAD_KEY_PASSWORD
BLOCNOTE_CHK_PAYMENTS_VERIFY_URL
```

Seuls les quatre premiers correspondent à la signature ; le cinquième est nécessaire au workflow pour CHK Paiements et reste distinct.

## Futures mises à jour

1. Ne pas changer `com.sync30.mesnotesdossiers` ni remplacer la clé d'upload sans procédure Google Play officielle.
2. Augmenter `versionCode` et utiliser **la même clé** via les secrets GitHub, après sauvegarde chiffrée.
3. Vérifier package, `versionCode`, certificat et signature du bundle avec `jarsigner -verify -verbose -certs app-release.aab`.
4. Tester hors ligne, intégrité des notes, achats, restauration, remboursement et piste interne.
5. Ne jamais confondre upload d'un bundle et publication effective. Conserver `com.chk.blocnote` intact.

Documentation Android : https://developer.android.com/studio/publish/app-signing
