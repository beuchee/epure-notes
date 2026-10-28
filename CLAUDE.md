# CLAUDE.md — Épure Notes

Consignes pour l'assistant de code. À lire en entier au début de chaque session.

## Avant toute tâche

1. Lire `docs/SPEC.md`, `docs/ROADMAP.md`, `docs/DECISIONS.md` et `docs/CHARTE.md`.
2. Identifier le **jalon en cours** dans `ROADMAP.md` (premier jalon dont toutes les tâches ne sont pas cochées). Ne travailler que sur ce jalon ; ne rien anticiper des jalons suivants.
3. Un point marqué **[À DÉCIDER]** n'est jamais implémenté comme s'il était tranché : demander à Alain, puis consigner la décision dans `DECISIONS.md` avant de coder.
4. Ne pas modifier `SPEC.md` pour l'aligner sur le code : signaler l'écart et le faire trancher.
5. Alain n'est pas développeur Android de métier : expliquer en français, simplement, ce qui est fait et pourquoi ; proposer la solution simple avant la solution sophistiquée.

## Langues

- Échanges avec Alain : **français**.
- Code, identifiants, commentaires de code, messages de commit : **anglais**.
- Textes d'interface : français, tutoiement, dans `res/values/strings.xml` uniquement (aucune chaîne en dur dans le Kotlin).

## Choix techniques figés

- Kotlin uniquement ; pas de code natif, pas de NDK, pas de Rust.
- Jetpack Compose, Material 3, thème personnalisé (pas de couleur dynamique).
- `minSdk` 31, `targetSdk` 36, `compileSdk` 37 (DEC-0020 : `compileSdk` = dernière version requise par les bibliothèques AndroidX ; `targetSdk` = version du téléphone de test).
- Une seule activité ; navigation par état simple ; `ViewModel` + `StateFlow` ; pas de framework d'injection (classe `AppContainer`).
- Persistance : fichiers texte (notes, tâches) dans le stockage interne + DataStore Preferences (réglages). Pas de Room sans décision.
- Sauvegarde système fermée : `allowBackup="false"` **et** `dataExtractionRules` excluant tout (cloud et transfert d'appareil). Seule sauvegarde : la copie optionnelle dans un dossier choisi par l'utilisateur (SPEC F-18, DEC-0019).
- Paquets : `ui/` (écrans), `data/` (dépôts, fichiers, DataStore), `system/` (API Android), `domain/` (logique pure **sans aucun import Android**, couverte par des tests unitaires).
- Edge-to-edge et clavier : `WindowInsets` (`systemBars`, `ime`). Retour : `BackHandler`, jamais `onBackPressed()`.
- Couleurs uniquement via les jetons de `ui/theme/Color.kt` ; tailles de texte en `sp` ; cibles tactiles ≥ 48 dp ; aucune icône ; aucune animation.

## Règle absolue : aucune permission

- **Aucune permission `INTERNET`**, et aucune autre permission, dans le manifeste **fusionné**.
- Toute nouvelle bibliothèque : entrée dans `DECISIONS.md` (contexte, alternative, licence compatible GPL v3, raison) **avant** de l'ajouter.
- Après chaque ajout de dépendance et avant de clore un jalon, vérifier l'APK :

```bash
./gradlew assembleDebug
# Option 1 (Android SDK cmdline-tools):
apkanalyzer manifest permissions app/build/outputs/apk/debug/app-debug.apk
# Option 2 (build-tools; replace <version>):
"$ANDROID_HOME/build-tools/<version>/aapt2" dump permissions app/build/outputs/apk/debug/app-debug.apk
```

  La sortie ne doit lister **aucune** permission utilisateur. Une permission injectée par une bibliothèque est retirée dans `AndroidManifest.xml` avec `tools:node="remove"`, ou la bibliothèque est écartée. Note : AndroidX ajoute une permission interne `<applicationId>.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` ; elle est propre à l'app et acceptée, à signaler à Alain si elle apparaît.

## Commandes

```bash
./gradlew assembleDebug          # build
./gradlew testDebugUnitTest      # unit tests (domain/)
./gradlew lint                   # lint: no NEW errors allowed
./gradlew installDebug           # install on the connected phone
```

Une tâche n'est **terminée** que lorsque : build sans erreur, tests au vert, lint sans nouvelle erreur, aucune permission dans l'APK, et **validation par Alain sur le téléphone** (Samsung, Android 16 / One UI 8.0) selon la liste de contrôle de `ROADMAP.md`. Alors seulement, cocher la tâche.

## Données et confidentialité

- Les notes restent sur l'appareil ; aucun journal du contenu des notes (`Log`), aucune donnée de test personnelle.
- Notes de test : fictives, générées dans les tests ou conservées hors du dépôt.
- Écriture des fichiers : atomique (`AtomicFile`) ; jamais de perte de texte en cas d'arrêt du processus.

## Git et dépôt public

- Jamais versionnés : `*.jks`, `*.keystore`, mots de passe, `local.properties`, notes personnelles, captures d'apps tierces.
- Avant chaque commit : `git status` et `git diff --staged` relus avec Alain.
- Commit seulement après validation sur l'appareil ; messages courts, en anglais, à l'impératif (ex. `Add autosaving text editor`).
- **Pas de `git push`**, pas de réécriture d'historique, pas de `--force` sans demande explicite d'Alain.
- Ressources tierces : licence compatible avec la redistribution (ex. police sous SIL OFL 1.1, fichier de licence copié dans le dépôt). Aucun code repris de freewrite (DEC-0006).
