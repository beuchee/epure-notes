# Charte graphique et fonctionnelle — Épure Notes

> Partie A : version 1.0 du 24 octobre 2026, reprise **sans modification** de la charte d'Épure PDF, elle-même reprise du launcher Épure. Les références de la Partie A (`ActionRow.kt`, « Épure »…) désignent les fichiers et l'app du launcher, qui servent de modèle. Les écarts d'Épure Notes avec la Partie A sont listés en B.13.
> Partie B : version 0.1 du 28 octobre 2026, propre à Épure Notes.
> Ce document ne remplace pas `SPEC.md`, qui reste le cahier des charges détaillé. En cas d'écart, `SPEC.md` et `DECISIONS.md` font foi ; l'écart est signalé et tranché.

---

## Partie A — Ce qui se reprend dans chaque projet

Ces règles sont **génériques** : elles valent pour tout nouveau projet Android de Alain, sauf décision explicite contraire consignée dans `DECISIONS.md`.

### A.1 Principes

1. **Sobriété visuelle** : fond uni, texte seul, aucune icône décorative, aucune animation superflue.
2. **Sobriété technique** : pas de service permanent sans nécessité ; données lues à la demande ; dépendances réduites au strict minimum.
3. **Confidentialité** : les données restent sur l'appareil. Pas de bibliothèque d'analytics, de suivi de plantage ni de publicité. Pas de permission `INTERNET` si le projet n'en a pas besoin strictement.
4. **Permissions justifiées** : chaque permission sensible correspond à une fonction précise, expliquée à l'utilisateur au moment où elle sert. Si elle est refusée, seule cette fonction est désactivée.
5. **Simplicité d'abord** : la solution simple avant la solution sophistiquée.
6. **Respect du système** : ne pas dupliquer ni contrarier les outils natifs de l'appareil (ici, les outils Samsung / One UI).

### A.2 Identité d'un projet

| Élément | Règle | Exemple (Épure) |
|---|---|---|
| Nom affiché | Avec accents si la langue le permet, dans `strings.xml` (`app_name`) | « Épure » |
| Nom technique | Sans accent ni espace (projet, modules, classes) | `Epure` |
| `applicationId` | Espace de nom inversé, stable dès le premier build | `com.alain.epure` |
| Dépôt Git | Nom court, en minuscules, avec tirets | `epure-launcher` |
| Licence | GPL v3 pour les projets publics | GPL v3 |
| Marques | Ne jamais utiliser le nom d'une marque tierce (nom, dépôt, paquet, fiche de store) ni suggérer une affiliation | — |

### A.3 Pile technique figée

| Domaine | Choix |
|---|---|
| Langage | Kotlin uniquement (pas de code natif, pas de Rust, pas de NDK sauf décision contraire) |
| Interface | Jetpack Compose, Material 3, thème personnalisé |
| `minSdk` | 31 (Android 12) |
| `targetSdk` / `compileSdk` | 36 (Android 16) |
| Architecture | Une seule activité ; écrans en Compose ; `ViewModel` + `StateFlow` |
| Injection | Pas de framework : une classe `AppContainer` instancie les dépendances à la main |
| Navigation | État simple géré à la main (3 à 4 écrans) ; pas de `navigation-compose` tant que ce n'est pas nécessaire |
| Persistance | DataStore (Preferences) ; Room seulement si les données deviennent réellement structurées |
| Logique métier | Dans un paquet `domain/`, **sans dépendance Android**, couverte par des tests unitaires |
| Paquets | `ui/` (écrans), `data/` (dépôts, persistance), `system/` (accès aux API Android), `domain/` (logique pure) |
| Sauvegarde Android | Désactivée (`allowBackup` à `false`) pour un outil qui ne stocke que des préférences locales |

**Règle de dépendance** : toute nouvelle bibliothèque est justifiée dans `DECISIONS.md` (contexte, alternative, raison) **avant** d'être ajoutée au projet.

### A.4 Contraintes Android à anticiper dès le départ

- **Edge-to-edge imposé** (`targetSdk` 36) : gérer les marges avec `WindowInsets`.
- **Retour prédictif** : utiliser `BackHandler` (Compose) ou `OnBackPressedCallback`, jamais `onBackPressed()`.
- **Visibilité des paquets** (Android 11+) : lister les apps impose une déclaration `<queries>` ; `QUERY_ALL_PACKAGES` est interdite.
- **Permissions interdites par défaut** dans le manifeste : `INTERNET`, `SYSTEM_ALERT_WINDOW`, `QUERY_ALL_PACKAGES`. Toute autre permission doit correspondre à une ligne d'un tableau des permissions dans `SPEC.md`.
- **Paramètres restreints** (Android 13+) : une app installée hors Play Store peut voir l'accessibilité ou l'écoute des notifications bloquées ; le noter dans le README dès qu'un service de ce type est prévu.
- **Services d'accessibilité** : `canRetrieveWindowContent="false"` sauf nécessité, événements limités au minimum, aucun enregistrement.

### A.5 Charte visuelle de base

#### Couleurs

Thème **sombre uniquement**, sans couleur dynamique, sans thème clair.

| Jeton | Valeur | Usage |
|---|---|---|
| `Black` | `#000000` | Fond de tous les écrans, fond des fenêtres |
| `TextPrimary` | `#FFFFFF` | Texte principal, éléments actifs, titres, curseur |
| `TextSecondary` | `#B0B0B0` | Texte secondaire, libellés, éléments inactifs, filets, liserés |

- Aucune couleur d'accent. L'information se transmet par la taille, la graisse et le niveau de gris.
- Éléments inactifs : gris clair (`TextSecondary`), **lisibles** (jamais invisibles).
- Les couleurs sont déclarées une fois, comme jetons dans `ui/theme/Color.kt`, puis reprises dans `darkColorScheme` (`ui/theme/Theme.kt`). Aucune couleur écrite en dur dans un écran.
- Fond : toujours `Black`, y compris dans `res/values/themes.xml` (`android:windowBackground`) pour éviter un flash blanc au démarrage.

#### Typographie

- Police système (`FontFamily.Default`). Aucune police tierce par défaut.
- Graisses utilisées : `Light` (très grand affichage uniquement), `Normal`. Pas de gras, sauf nécessité.
- **Toutes les tailles sont en `sp`** : elles suivent le réglage de taille de police du téléphone. Jamais en `dp` pour du texte.
- Échelle utilisée dans Épure (`ui/theme/Type.kt`) :

| Rôle | Taille | Graisse | Style Compose |
|---|---|---|---|
| Heure (accueil) | 120 sp | Light | `ClockStyle` |
| Date (accueil) | 20 sp | Normal | `DateStyle` |
| Nom d'app, titre d'écran, titre de fenêtre, champ de recherche | 28 sp | Normal | `AppNameStyle` |
| Libellé, option secondaire, texte d'aide, bouton texte | 20 sp | Normal | `LabelStyle` |
| Corps de texte (rarement utilisé) | 16 sp | Normal | `Typography.bodyLarge` |

- Interligne généreux pour les textes courants : 1,3 à 1,4 fois la taille (28 sp / 36 sp ; 20 sp / 28 sp). L'heure seule a un interligne égal à sa taille (120 sp).
- Un nom trop long **passe à la ligne**, il n'est pas tronqué.

#### Espacements et géométrie

| Élément | Valeur |
|---|---|
| Marge horizontale des écrans | 24 dp (accueil : 16 dp pour les raccourcis) |
| Marge verticale du haut d'écran | 24 dp |
| Espace entre un titre et son premier élément | 24 dp |
| Espace entre un bloc et son libellé | 16 dp à 32 dp |
| Hauteur minimale d'une cible tactile | 48 dp |
| Rayon des fenêtres (dialogue, menu) | 16 dp |
| Épaisseur des liserés et filets | 1 dp, couleur `TextSecondary` |

#### Iconographie

- **Par défaut, pas d'icône, pas de pictogramme** : loupe, roue dentée, flèches décoratives sont exclus. Les libellés sont du texte.
- Exception documentée : les icônes de raccourcis de l'accueil (Material Symbols, licence Apache 2.0, licence conservée dans chaque fichier `.xml`). Toute autre exception passe par `DECISIONS.md`.
- Les icônes conservées sont en `TextPrimary`, sans fond ni cadre, avec une zone tactile d'au moins 48 dp.

#### Mouvement

- Aucune animation décorative. Pas de transition d'apparition, pas de rebond.
- Un défilement natif, un changement d'écran immédiat, c'est tout.

### A.6 Composants types

Ces composants se réutilisent d'un projet à l'autre. Leur style doit rester identique.

| Composant | Description | Référence dans Épure |
|---|---|---|
| **Action en liste** | Texte seul, `AppNameStyle`, `TextPrimary`, hauteur minimale 48 dp, cible pleine largeur | `ActionRow.kt` |
| **Action secondaire** | Texte `LabelStyle` en `TextSecondary`, cible ≥ 48 dp | `TextAction` dans `ActionRow.kt` |
| **Champ de recherche** | Texte seul, sans cadre ni loupe ; une ligne soulignée implicite ; curseur `TextPrimary` ; clavier ouvert à l'arrivée sur l'écran | `AppListScreen.kt` |
| **Fenêtre de menu** | Centrée, fond `Black`, liseré 1 dp `TextSecondary`, rayon 16 dp, titre en `TextPrimary`, filet, actions en liste ; fond de l'écran assombri ; fermeture par Retour ou toucher à l'extérieur | `AppActionsMenu.kt` |
| **Boîte de saisie** | Même fenêtre que le menu, champ de saisie en `AppNameStyle`, deux actions texte | `RenameDialog.kt` |
| **Écran d'autorisation** | Plein écran, texte seul : titre, pourquoi, ce que l'app ne fait pas, consignes numérotées, bouton « Ouvrir le réglage », mention de révocation, bouton « Plus tard » | `PermissionScreen.kt` |
| **Index alphabétique** | Colonne verticale de lettres à droite de la liste, `TextSecondary`, largeur 40 dp, seules les lettres présentes, masquée pendant une recherche | `LetterIndex.kt` |
| **Liste de réglages** | Titre (`AppNameStyle`) puis actions en liste, défilement vertical | `SettingsScreen.kt` |
| **Séparateur** | `HorizontalDivider` de 1 dp en `TextSecondary` | menu et boîte de saisie |

#### États d'un élément

| État | Rendu |
|---|---|
| Actif | `TextPrimary` |
| Inactif ou indisponible | `TextSecondary`, reste visible, explication disponible au toucher |
| Pendant une action | Pas d'indicateur animé ; le résultat apparaît à la fin |
| Vide | Une phrase courte en `TextSecondary`, sans encadré ni illustration (ex. « Aucune app ») |

### A.7 Textes et langue

- **Tous les textes d'interface** sont dans `res/values/strings.xml`. Aucune chaîne en dur dans le code Kotlin.
- Langue par défaut : **français**. L'anglais peut être ajouté en seconde langue.
- **Tutoiement** dans l'interface.
- Ton **factuel et neutre** : pas de promesse chiffrée, pas de formule promotionnelle, pas de culpabilisation.
- Phrases courtes. Libellés à l'infinitif pour les actions (« Renommer », « Ouvrir le réglage »). Majuscule en début de phrase seulement.
- Les libellés des réglages système reprennent **exactement** le texte affiché par Android ou One UI, entre guillemets (ex. « Données d'utilisation » → « Octroi d'autorisation »).
- Les commentaires de code et les identifiants sont en **anglais**.

### A.8 Accessibilité (minimum)

- Tailles en `sp` ; l'interface doit rester utilisable avec la taille de police système agrandie.
- Cibles tactiles ≥ 48 dp.
- Contraste élevé : texte clair sur fond noir.
- Chaque élément interactif a un libellé lisible par TalkBack (`contentDescription` ou texte). Les lettres de l'index sont annoncées comme actions (« Aller à la lettre M »).
- Un écran qui échoue (permission retirée, donnée absente) affiche un message, sans plantage.

### A.9 Gestes et navigation (règles communes)

- **Bouton Retour** : ferme d'abord le premier niveau ouvert (fenêtre, clavier, liste) ; sur l'écran d'accueil d'un launcher, il ne fait rien.
- **Bouton Accueil** (launcher) : ramène toujours à l'état initial de l'écran d'accueil.
- **Geste de navigation** : un balayage clair, avec un seuil de distance (ici 100 dp), pour éviter les déclenchements involontaires lors d'un simple toucher. Un libellé texte est toujours disponible en plus du geste, pour ne pas dépendre du seul geste.
- **Appui court** : action principale (ouvrir, valider). **Appui long** : menu d'actions sur l'élément.
- Pas de fenêtre de confirmation pour une action réversible ; confirmation pour une action irréversible.

### A.10 Permissions et autorisations spéciales

Modèle commun, à réutiliser tel quel :

1. La demande **apparaît au moment où la fonction est utilisée**, jamais dans un assistant au premier lancement.
2. L'écran d'autorisation (A.6) explique : la fonction, pourquoi, ce qu'Épure ne fait pas, les consignes exactes, le bouton d'ouverture du réglage, la mention « Révocable à tout moment », le bouton « Plus tard ».
3. Au retour du réglage système, l'autorisation est **vérifiée automatiquement** ; l'écran se ferme si elle est accordée.
4. Un refus ne désactive que la fonction concernée ; le reste de l'app fonctionne.
5. Aucune illustration ni capture du réglage système (sobriété, et pas de visuels tiers dans le dépôt).

### A.11 Méthode de travail et documents

Chaque projet contient un dossier `docs/` avec :

| Fichier | Contenu |
|---|---|
| `SPEC.md` | Cahier des charges : exigences `F-x.y` (fonctions) et `T-n` (transversales), critères d'acceptation, tableau des permissions, points ouverts `[À DÉCIDER]` |
| `ROADMAP.md` | Jalons et tâches cochables ; une tâche n'est cochée qu'une fois **validée sur l'appareil** |
| `DECISIONS.md` | Journal des décisions `DEC-xxxx` (contexte, options, décision, justification, réversibilité) ; les entrées ne sont jamais effacées |
| `CHARTE.md` | Le présent document |
| `CLAUDE.md` (racine) | Consignes pour l'assistant de code : lire les documents avant toute tâche, ne travailler que sur le jalon en cours |

Règles de méthode :

- **Un point `[À DÉCIDER]` n'est jamais implémenté comme s'il était tranché.** Il est d'abord consigné dans `DECISIONS.md`, puis retiré du `SPEC.md`.
- **Un point `[À VÉRIFIER]` se teste sur l'appareil** ; le résultat est consigné.
- **Ne pas modifier `SPEC.md` pour l'aligner sur le code** : l'écart est signalé et tranché.
- Une session de travail par jalon ou par tâche.
- Ne travailler que sur le jalon en cours : pas d'anticipation des jalons suivants.

### A.12 Dépôt public et Git

- Jamais versionnés : clé de signature (`*.jks`, `*.keystore`) et ses mots de passe, `local.properties`, informations personnelles, captures ou visuels de produits tiers.
- `LICENSE` présent dès le premier commit ; `.gitignore` Android vérifié avant le premier envoi.
- Avant chaque commit : `git status` et contenu indexé relus.
- **Commit seulement après validation sur l'appareil.** Commit avec l'adresse e-mail « noreply » de GitHub.
- Messages de commit en **anglais**, courts, à l'impératif (ex. `Add text-only app list`).
- Pas de `git push`, pas de réécriture d'historique, pas de force sans demande explicite.
- Ressources tierces seulement sous licence compatible avec la redistribution (Google Fonts, Material Symbols, Apache 2.0 ou équivalent). Code repris d'un projet open source : licence du fichier vérifiée, avis de copyright conservés, origine mentionnée en commentaire et dans « À propos ».
- README : mention factuelle si le projet s'inspire d'une autre app (« inspiré de … »), sans logo ni suggestion d'affiliation ; section Confidentialité ; procédure d'activation des permissions spéciales.

### A.13 Contrôles avant de déclarer une tâche terminée

1. `./gradlew assembleDebug` sans erreur.
2. `./gradlew testDebugUnitTest` au vert.
3. `./gradlew lint` sans **nouvelle** erreur.
4. Vérification sur l'appareil de test (Samsung, Android 16 / One UI 8.0) selon une liste de contrôle écrite dans `ROADMAP.md`.
5. Cocher la tâche dans `ROADMAP.md` seulement après validation.

---

## Partie B — Épure Notes

Cette partie décrit ce qu'Épure Notes fait et comment la Partie A s'applique à une app d'écriture. Les exigences détaillées sont dans `SPEC.md` (références entre parenthèses), les décisions dans `DECISIONS.md`. Tout ce qui est marqué **[À DÉCIDER]** n'est pas tranché ; les propositions sont signalées comme telles.

### B.1 Objet

App Android de prise de notes minimaliste, entièrement hors ligne : écrire sans distraction, sans réseau, sans collecte de données. Usages : prise de notes rapide (ouvrir l'app et écrire tout de suite), écriture libre sans mise en forme, extraction d'une ligne vers une liste de tâches. Usage personnel en priorité ; code public ; publication sur le Play Store éventuelle, non prioritaire.

Référence ergonomique : l'app freewrite (Android, Mohammed Farseen ; macOS, Farza). Inspiration d'interface seulement, aucun code repris (DEC-0006).

### B.2 Identité (DEC-0001 à DEC-0003, DEC-0006)

| Élément | Valeur |
|---|---|
| Nom affiché | « Épure Notes » |
| Nom technique | `EpureNotes` |
| `applicationId` et `namespace` | `com.alain.epure.notes` |
| Dépôt Git | `epure-notes` |
| Licence | GPL v3 |
| Relation avec Épure et Épure PDF | Apps distinctes ; cohérence visuelle seulement ; thème copié, pas partagé |
| Marques | « freewrite » jamais dans le nom, le paquet ou une fiche de store ; aucune mention dans le README ni dans « À propos » (DEC-0025) |

### B.3 Écrans

| Écran | Rôle | Accès | Jalon |
|---|---|---|---|
| Éditeur | Écran principal : la note en cours, plein écran, barre de commandes en bas | Lancement de l'app | 1 |
| Notes | Liste des notes, de la plus récente à la plus ancienne ; champ de recherche (jalon 4) | Commande « Notes » | 2 |
| Réglages | Sauvegarde et restauration (jalon 2) ; taille du texte, thème, effacement (jalon 3) ; « À propos » | Commande « Réglages » | 2, 3 |
| À propos | Version, licence, dépôt, confidentialité, crédits | Réglages | 2 |
| Tâches | Liste de tâches unique | Commande « Tâches » | 5 |
| Menu d'une note | « Partager », « Supprimer » | Appui long sur une note | 2 |
| Confirmations | Suppression d'une note, arrêt de la sauvegarde, effacement des tâches faites | Action irréversible | 2, 5 |

- Il n'y a pas d'écran d'accueil distinct : l'éditeur est l'accueil (F-1).
- Forme des réglages : écran « Liste de réglages » (A.6) ou panneau dépliable au-dessus de la barre, comme freewrite : **[À DÉCIDER]** (`SPEC.md` § 3). Proposition : panneau dépliable.
- Pas d'écran d'autorisation (A.6, A.10) : l'app ne demande aucune permission (B.10). Le choix du dossier de sauvegarde passe par le sélecteur système, qui n'est pas une permission.

### B.4 Application de la charte visuelle

#### Thèmes Nuit et Jour (dérogation à A.5, DEC-0009)

Le thème Nuit, par défaut, reprend exactement les trois jetons de A.5. Le thème Jour est une dérogation décidée : l'écran entier passe en clair, car dans une app d'écriture le texte est l'interface (dans Épure PDF, seul le contenu des pages était inversé).

| Rôle | Nuit (défaut, = A.5) | Jour (proposition, à valider sur l'appareil) |
|---|---|---|
| Fond | `#000000` | `#FFFFFF` |
| Texte principal, curseur, éléments actifs | `#FFFFFF` | `#000000` |
| Texte secondaire, filets, liserés, éléments inactifs | `#B0B0B0` | `#5F5F5F` (contraste ≥ 4,5:1 à vérifier) |

- Toujours sans couleur dynamique ni couleur d'accent.
- Proposition de mise en œuvre : jetons nommés par rôle et par thème dans `Color.kt` (ex. `NightBackground`, `DayBackground`), repris dans `darkColorScheme` et `lightColorScheme` ; le choix suit le réglage de l'app, pas le thème du téléphone.
- `themes.xml` garde un fond noir (`android:windowBackground`) : au démarrage en thème Jour, un bref passage par le noir est accepté plutôt qu'un flash blanc en thème Nuit **[À VÉRIFIER]**.

#### Éditeur

| Élément | Règle |
|---|---|
| Zone de texte | Plein écran, texte brut, sans cadre ni soulignement |
| Marges | 24 dp latérales, 24 dp en haut (A.5) |
| Clavier | La zone de texte et la barre restent au-dessus du clavier (`WindowInsets.ime`) |
| Texte indicatif (note vide) | Même style que le texte, en texte secondaire ; libellé proposé « Commence à écrire » |
| Barre de commandes | En bas, filet de 1 dp en haut, actions texte ; toujours visible au jalon 1, masquée pendant la frappe à partir du jalon 3 (`SPEC.md` F-7.4, **[À DÉCIDER]**) |
| Enregistrement | Aucun indicateur « enregistré » (A.6, « Pendant une action ») |
| Mode immersif | Proposition : barre d'état masquée, barre de navigation conservée **[À DÉCIDER]** (F-2.7) |

#### Typographie propre à Épure Notes

L'échelle de A.5 s'applique. `ClockStyle` et `DateStyle` ne servent pas. Un style est ajouté pour le texte des notes.

| Élément | Style |
|---|---|
| Texte de la note | Nouveau style `NoteTextStyle` : taille réglable 16, 18, 20, 22 ou 24 sp (défaut proposé 20 sp, **[À DÉCIDER]**), graisse Normal, texte principal ; interligne proposé 1,5 (**[À DÉCIDER]**, A.5 prévoit 1,3 à 1,4) |
| Titre d'écran (« Notes », « Tâches », « Réglages ») | `AppNameStyle`, texte principal |
| Champs « Rechercher », « Ajouter une tâche » | Champ de recherche (A.6), `AppNameStyle` |
| Titre d'une note dans la liste | **[À DÉCIDER]** : `AppNameStyle` (conforme) ou `LabelStyle` en texte principal ; 2 lignes au plus puis « … » (dérogation proposée, F-4.3) |
| Date de la note (seconde ligne) | `LabelStyle`, texte secondaire |
| Tâche à faire / faite | Proposition : `LabelStyle`, texte principal / texte secondaire (F-12.3) |
| Actions de la barre | `LabelStyle` ; texte principal si disponible, texte secondaire sinon |
| Réglages | Action en liste, valeur dans le libellé (« Taille du texte : 20 », « Thème : Nuit », « Effacer : oui », « Sauvegarde : non ») |
| Avertissement de perte de données | `LabelStyle`, texte secondaire, **italique** (dérogation décidée, DEC-0019) |

- Police : système au jalon 1 ; Lato embarquée (SIL OFL 1.1) ou police système : **[À DÉCIDER]** (DEC-0010). Si Lato est retenue, elle ne sert qu'au texte des notes ou à toute l'interface : à préciser dans la décision.

#### Iconographie

Aucune icône (DEC-0011). Les icônes de freewrite (étincelles, +, historique, chevron) sont remplacées par des libellés texte. L'exception des raccourcis d'Épure (A.5) ne s'applique pas.

Point de vigilance : le libellé proposé « → Tâche » contient une flèche, que A.5 exclut comme pictogramme décoratif. Libellé à trancher **[À DÉCIDER]** : « → Tâche » (flèche considérée comme du texte) ou un libellé sans flèche, par exemple « En tâche ».

#### Dérogations à la Partie A

| Sujet | Règle A | Dérogation | État |
|---|---|---|---|
| Thème clair | A.5 : sombre uniquement | Thème Jour en option | Décidée (DEC-0009) |
| Italique | A.5 : graisses Light et Normal seulement | Avertissement de sauvegarde en italique | Décidée (DEC-0019) |
| Interligne du texte des notes | A.5 : 1,3 à 1,4 | 1,5, proche de freewrite | **[À DÉCIDER]** (F-2.3) |
| Police tierce | A.5 : police système | Lato embarquée | **[À DÉCIDER]** (DEC-0010) |
| Troncature des titres | A.5 : pas de troncature | 2 lignes puis « … » | **[À DÉCIDER]** (F-4.3) |

Toute dérogation retenue est consignée dans `DECISIONS.md` puis reportée ici.

### B.5 Composants propres à Épure Notes

À créer dans ce projet, en respectant les composants types de A.6 :

| Composant | Description | Exigences |
|---|---|---|
| **Éditeur** | Zone de texte plein écran, enregistrement automatique, gestion du clavier ; filtrage des suppressions quand « Effacer : non » | F-1 à F-3, F-8.3 |
| **Barre de commandes** | Rangée d'actions texte en bas de l'éditeur, filet de 1 dp en haut, cibles ≥ 48 dp ; apparition et disparition immédiates | F-7 |
| **Ligne de note** | Titre (première ligne de la note) et date en seconde ligne ; appui court = ouvrir, appui long = menu | F-6 |
| **Ligne de tâche** | Texte de la tâche ; appui court = fait / à faire, appui long = menu ; tâche faite en texte secondaire | F-12 |
| **Réglage à valeurs** | Action en liste dont le libellé affiche la valeur courante ; un toucher passe à la valeur suivante | F-8 |
| **Avertissement** | Phrase en `LabelStyle`, texte secondaire, italique, sous le réglage « Sauvegarde » et dans « À propos » | F-18.2 |

Composants de A.6 réutilisés tels quels : action en liste, action secondaire, champ de recherche, fenêtre de menu (menu d'une note, confirmations), boîte de saisie (modifier une tâche), liste de réglages (si l'écran est retenu), séparateur. L'index alphabétique et l'écran d'autorisation ne servent pas.

### B.6 Gestes et navigation

| Geste | Où | Effet |
|---|---|---|
| Toucher | Texte de la note | Place le curseur et ouvre le clavier ; à partir du jalon 3, fait aussi réapparaître la barre |
| Appui long, double toucher | Texte de la note | Sélection système (copier, coller…) ; non détournés (DEC-0013) |
| Pincement | Texte de la note | Aucun effet ; la taille se règle dans les réglages (DEC-0012) |
| Défilement | Texte de la note | Défilement natif |
| Toucher | Action de la barre | Exécute l'action |
| Appui court / appui long | Note dans la liste | Ouvrir / menu « Partager », « Supprimer » |
| Appui court / appui long | Tâche | Fait ou à faire / menu « Modifier », « Supprimer » |
| Toucher | Réglage | Valeur suivante, ou sélecteur de dossier pour la sauvegarde |
| Retour | Partout | Ferme d'abord la fenêtre ou le panneau ouvert, puis masque le clavier |
| Retour | Notes, Tâches, Réglages | Revient à l'éditeur |
| Retour | Éditeur | Quitte l'app (comportement Android standard ; Épure Notes n'est pas un launcher) |

- Aucun geste sans libellé équivalent (A.9) : toutes les fonctions passent par une action texte.

### B.7 Textes propres

Libellés de référence, à reprendre dans `strings.xml` ; ceux marqués (p) sont des propositions.

| Contexte | Texte |
|---|---|
| Éditeur | « Commence à écrire » (p) |
| Barre de commandes | « Nouvelle », « Notes », « Tâches », « Réglages » (p) ; « → Tâche » ou équivalent, « Ajoutée » (p, jalon 5) |
| Notes | « Notes », « Rechercher », « Aucune note », « Aucun résultat » |
| Menu d'une note | « Partager », « Supprimer » |
| Suppression d'une note | « Supprimer cette note ? » ; si la sauvegarde est active : « Supprimer cette note ? Elle sera aussi supprimée du dossier de sauvegarde. » ; « Annuler », « Supprimer » |
| Tâches | « Tâches », « Ajouter une tâche », « Aucune tâche », « Modifier », « Supprimer », « Effacer les tâches faites » |
| Réglages | « Taille du texte », « Thème : Nuit » / « Thème : Jour », « Effacer : oui » / « Effacer : non », « Sauvegarde : oui » / « Sauvegarde : non », « Restaurer depuis un dossier », « À propos » |
| Avertissement (italique) | « Tes notes sont enregistrées seulement sur ce téléphone. Elles seront perdues si tu désinstalles l'app ou si tu perds le téléphone. » |
| Sauvegarde | « Arrêter la sauvegarde ? », « Annuler », « Arrêter », « Dossier de sauvegarde introuvable. », « 12 notes importées. » (nombre réel), « Aucune note trouvée dans ce dossier. » |

Les messages exacts restent ajustables au jalon concerné ; le ton reste neutre et factuel (A.7).

### B.8 Fonctions

| Fonction | Détail | Jalon | État |
|---|---|---|---|
| Éditeur plein écran | Texte brut, défilement continu, clavier géré | 1 | Prévu |
| Enregistrement automatique | Après pause de frappe et en arrière-plan, écriture atomique | 1 | Prévu |
| Reprise | Ouverture sur la dernière note, curseur à la fin | 1 | Prévu |
| Nouvelle note | Note vide jamais conservée | 1 | Prévu |
| Liste des notes | Titre = première ligne, date, suppression confirmée | 2 | Prévu |
| Partage | Feuille de partage système, texte de la note | 2 | Prévu |
| Réception de texte | Texte partagé depuis une autre app → nouvelle note | 2 | **[À DÉCIDER]** (F-10) |
| Sauvegarde et restauration | Copie optionnelle dans un dossier choisi ; suppression partout | 2 | Décidée (DEC-0019) |
| « À propos » | Version, licence, dépôt, confidentialité, crédits | 2 | Prévu |
| Réglages d'écriture | Taille du texte, thème Nuit / Jour, effacement oui / non | 3 | Prévu |
| Barre masquée pendant la frappe | — | 3 | Prévu, modalités **[À DÉCIDER]** |
| Écran allumé, compteur de mots | — | 3 | **[À DÉCIDER]** (F-16, F-17) |
| Recherche | Dans toutes les notes, sans tenir compte des majuscules ni des accents | 4 | Prévu |
| Liste de tâches | Liste unique, fait / à faire | 5 | Prévu |
| Ligne → tâche | Action de la barre, ligne du curseur | 5 | Décidée (DEC-0013) |
| Tâches sur l'écran d'accueil | Widget ou raccourci d'app | 6 | Conditionnel (DEC-0016) |

### B.9 Fonctions écartées

- Fonction d'IA de freewrite (exigerait le réseau), minuteur d'écriture, choix entre plusieurs polices (DEC-0017, proposée).
- Fonctions de lecteur PDF reprises par erreur dans le brief : zoom, demi-page, indicateur de page, inversion des couleurs (DEC-0012, proposée).
- Hors périmètre de la version 1 : mise en forme, images, dossiers et étiquettes, rappels, synchronisation cloud propre à l'app, chiffrement, verrouillage par code (`SPEC.md` § 9).

### B.10 Permissions

| Permission / accès | Fonction | Obtention |
|---|---|---|
| Stockage interne de l'app | Notes, tâches | Aucune permission |
| DataStore | Réglages, note en cours, dossier de sauvegarde | Aucune permission |
| Feuille de partage (émission) | Partager une note | Aucune permission |
| Filtre d'intent `ACTION_SEND` `text/plain` (si F-10 retenue) | Créer une note depuis une autre app | Aucune permission |
| Autorisation persistante sur un dossier (`ACTION_OPEN_DOCUMENT_TREE`) | Sauvegarde et restauration | Choix de l'utilisateur ; libérée à l'arrêt de la sauvegarde |
| `INTERNET`, `ACCESS_NETWORK_STATE`, `MANAGE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `READ_MEDIA_*`, `POST_NOTIFICATIONS`, `WAKE_LOCK`, `QUERY_ALL_PACKAGES`, `SYSTEM_ALERT_WINDOW`, `RECEIVE_BOOT_COMPLETED` | — | **Non déclarées** |

Objectif : **aucune permission déclarée** dans le manifeste fusionné (la permission interne `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` ajoutée par AndroidX, propre à l'app, est acceptée).

### B.11 Garanties de confidentialité

- Par défaut, aucune donnée ne quitte le téléphone, ni par l'app, ni par la sauvegarde système Android (sauvegarde cloud et transfert d'appareil à appareil fermés, DEC-0008).
- Pas de permission `INTERNET` : garantie vérifiable dans le manifeste (DEC-0005).
- Les notes sont des fichiers texte dans le stockage interne de l'app (DEC-0007) ; aucune statistique, aucun journal d'usage, aucun contenu de note dans les journaux techniques.
- Sans sauvegarde activée, les notes sont perdues à la désinstallation ou en cas de perte du téléphone ; l'app l'affiche en clair (F-18.2).
- Avec la sauvegarde activée, les copies vont uniquement dans le dossier choisi par l'utilisateur. Si ce dossier est synchronisé par une app de cloud, c'est cette app qui envoie les fichiers, à l'initiative de l'utilisateur (DEC-0019).
- Supprimer une note la supprime partout, y compris dans le dossier de sauvegarde ; les autres fichiers de ce dossier ne sont jamais touchés.
- Le lien « Dépôt GitHub » de « À propos » est ouvert par le navigateur.

### B.12 Jalons et état d'avancement (28 octobre 2026)

| Jalon | Contenu | État |
|---|---|---|
| 0 | Mise en place du projet, dépôt, licence, documents | En cours (projet créé, premier commit local) |
| 1 | Éditeur, enregistrement automatique, reprise, nouvelle note | À faire |
| 2 | Liste des notes, sauvegarde et restauration, partage, réception, « À propos » | À faire |
| 3 | Réglages d'écriture, thème Jour, effacement, barre masquée | À faire |
| 4 | Recherche | À faire |
| 5 | Liste de tâches, ligne → tâche | À faire |
| 6 | Tâches sur l'écran d'accueil | Conditionnel |

### B.13 Écarts avec la Partie A (pile technique)

La Partie A est reprise sans modification ; ces écarts sont propres à Épure Notes et à reporter dans une future version 1.1 de la Partie A commune.

| Règle A | Écart dans Épure Notes | Décision |
|---|---|---|
| A.3 `targetSdk` / `compileSdk` 36 | `compileSdk` 37, `targetSdk` 36 : les bibliothèques AndroidX actuelles exigent `compileSdk` 37 ; le comportement de l'app reste celui d'Android 16 | DEC-0020 |
| A.3 Persistance DataStore | Notes et tâches en fichiers texte `.md` dans le stockage interne ; DataStore pour les réglages | DEC-0007 |
| A.3 Sauvegarde Android | `allowBackup` à `false` **et** `dataExtractionRules` excluant tout : sous Android 12+, `allowBackup` seul n'empêche pas le transfert d'appareil à appareil. Sauvegarde par l'utilisateur en option | DEC-0008, DEC-0019 |
| A.5 Couleurs, typographie | Voir « Dérogations à la Partie A » en B.4 | DEC-0009, DEC-0019 |

---

## Partie C — Démarrer un nouveau projet avec cette charte

Liste à suivre dans l'ordre :

1. Créer le projet Android Studio : Kotlin, Compose, `minSdk` 31, `targetSdk`/`compileSdk` 36, `applicationId` au format `com.alain.<nom>`.
2. Renseigner l'identité (A.2) : `app_name`, nom technique, dépôt, licence.
3. Copier le thème : `Color.kt` (trois jetons), `Theme.kt` (schéma sombre), `Type.kt` (échelle en `sp`), `themes.xml` (fond noir).
4. Créer les composants types (A.6) avant les écrans : liste en texte, fenêtre de menu, écran d'autorisation.
5. Créer `docs/` avec `SPEC.md`, `ROADMAP.md`, `DECISIONS.md` (modèle d'entrée en tête du fichier) et `CLAUDE.md` à la racine.
6. Rédiger la **Partie B** du présent document pour le nouveau projet (écrans, fonctions, permissions, garanties).
7. Vérifier le manifeste : aucune permission hors tableau de `SPEC.md`.
8. Mettre en place `.gitignore`, `LICENSE`, puis le premier build, les tests et le lint.
9. Consigner toute dérogation à cette charte dans `DECISIONS.md` ; ne pas la modifier silencieusement.

---

## Historique

- Partie A 1.0 (24/10/2026) : reprise sans modification de la charte du launcher Épure, via Épure PDF.
- Partie B 0.1 (28/10/2026) : première version pour Épure Notes, à partir de `SPEC.md` 0.2 et `DECISIONS.md` (DEC-0001 à DEC-0020).
